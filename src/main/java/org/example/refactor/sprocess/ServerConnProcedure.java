package org.example.refactor.sprocess;

import lombok.extern.slf4j.Slf4j;
import org.example.cpe.CpeInfo;
import org.example.refactor.sprocess.httpRegister.HttpRegister;
import org.example.refactor.sprocess.httpRegister.entity.RegisterRequest;
import org.example.refactor.sprocess.httpRegister.entity.RegisterResponse;
import org.example.refactor.sprocess.ovsdbRegister.OvsdbRegister;
import org.example.refactor.sprocess.protocol.coder.ReformedOpenflowCoder;
import org.example.refactor.sprocess.protocol.reformedOpenflow.ReformedOpenflowPkgEntity;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ServerConnProcedure {


    private String httpRegisterIP = "127.0.0.1";
    private String httpRegisterPort = "8080";
    private String httpUrlPrefix = "http://";
    private String httpUrl = "/loidAuth";
    private String httpRegisterUrl = "http://10.1.64.16:38081/loidAuth";
//    private String httpRegisterUrl = "http://10.1.19.51:8081/loidAuth";


    private ReformedOpenflowCoder reformedOpenflowCoder;

    public void doServerConnProcedure() {
        RegisterResponse registerResponse;
        try{
            registerResponse = doHttpRegister();
        } catch (Exception e) {
            log.error("http register fail , error:{}", e.getMessage());
            e.printStackTrace();
            return;
        }

        try{
            doOvsdbRegister(registerResponse);
        } catch (Exception e) {
            log.error("ovsdb register fail , error:{}", e.getMessage());
            e.printStackTrace();
            return;
        }

        //todo: 前置检查其他另一侧代理是否成功。
        try {
            consistProcedure();
        } catch (Exception e) {
            log.error("consistency fail , error:{}", e.getMessage());
            return;
        }
    }


    private void consistProcedure() throws Exception {
        while(true){
            ReformedOpenflowPkgEntity reformedOpenflowPkgEntity;
            reformedOpenflowPkgEntity = reformedOpenflowCoder.read();
            ReformedOpenflowPkgEntity response = ServerPkgDispatcher.dispatch(reformedOpenflowPkgEntity);
            if(response != null){
                try {
                    reformedOpenflowCoder.writeAndFlush(response);
                }catch (IOException e) {
                    try {
                        reformedOpenflowCoder.getSocket().close();
                    } catch (IOException ex) {
                        log.error("socket close failed, {}", ex.getMessage());
                    }
                    throw new Exception("openflow coder write failed");
                }
            }
        }
    }


    private RegisterResponse doHttpRegister() throws Exception {
//        String httpRegisterUrl = httpUrlPrefix + httpRegisterIP + httpRegisterPort + httpUrl;
        CpeInfo cpeInfo = CpeInfo.getCpeInfo();
        RegisterRequest registerRequest = CpeInfo.toRegisterRequest(cpeInfo).initRemains();
        RegisterResponse registerResponse;
        try {
            registerResponse = HttpRegister.doRegister(registerRequest, httpRegisterUrl);
        } catch (IOException e){
            log.error("register error, {}", e.getMessage());
            throw e;
        }
        if(registerRequest == null){
            throw new Exception("register request is null");
        }
        log.info("got register Request : {}", registerRequest);
        return registerResponse;
    }

    private void doOvsdbRegister(RegisterResponse registerResponse) throws Exception {

        int retryTime = registerResponse.getRetry_times();
        for(int i=0; i<retryTime; i++) {
            if (Objects.equals(registerResponse.getCode(), "200000")) {
                String ip = registerResponse.getResult().getController_ip();
                int port = registerResponse.getResult().getController_port();
                String token = registerResponse.getResult().getToken();
                double SleepTime = registerResponse.getResult().getDelay_period();

                log.info("http register success, sleep {} seconds", SleepTime);

                try {
                    TimeUnit.SECONDS.sleep((long) SleepTime);
                } catch (InterruptedException e) {
                    throw e;
                }

                OvsdbRegister ovsdbRegister = new OvsdbRegister(ip, port, token);
                reformedOpenflowCoder = ovsdbRegister.doReformedOpenflowConnEstablish();
                CpeInfo.getCpeInfo().setServerReformedOpenflowCoder(reformedOpenflowCoder);

                try {
                    ovsdbRegister.doRegister();
                }catch (Exception e){
                    log.info("ovsdb register fail, msg:{}, sleep seconds {}", registerResponse, registerResponse.getConnection_retry_interval());
                    TimeUnit.SECONDS.sleep(registerResponse.getConnection_retry_interval());
                }
                log.info("ovsdb register success.");
                return;
            } else {
                log.info("fail, msg:{}, sleep seconds {}", registerResponse, registerResponse.getConnection_retry_interval());
                try {
                    TimeUnit.SECONDS.sleep(registerResponse.getConnection_retry_interval());
                } catch (InterruptedException e) {
                    throw e;
                }
            }
        }

    }



}
