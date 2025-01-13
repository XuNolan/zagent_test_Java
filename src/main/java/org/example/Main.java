package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.cpe.CpeInfo;
import org.example.openflow.OpenflowConnProccessor;
import org.example.openflow.OpenflowHandShake;
import org.example.refactor.sprocess.httpRegister.HttpRegister;
import org.example.refactor.sprocess.httpRegister.entity.RegisterRequest;
import org.example.refactor.sprocess.httpRegister.entity.RegisterResponse;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Main {

    public static void main(String[] args) {
        CpeInfo cpeInfo = CpeInfo.getRamdonCpe();
        RegisterRequest registerRequest = CpeInfo.toRegisterRequest(cpeInfo).initRemains();
        RegisterResponse registerResponse = HttpRegister.doRegister(registerRequest);
        System.out.println(registerResponse);
        assert registerResponse != null;
        int retryTime = registerResponse.getRetry_times();
        OpenflowConnProccessor proccessor = null;
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
                    log.error(e.getMessage());
                    return;
                }
                proccessor = new OpenflowConnProccessor();
                if (proccessor.doOvsdbRegister(ip, port, token, cpeInfo)) {
                    log.info("OVSDB REGISTER SUCCESS, begin openflow handshake");
                    //开始openflow 握手；
                    break;
                }
            } else {
                log.info("fail, msg:{}, sleep seconds {}", registerResponse, registerResponse.getConnection_retry_interval());
                try {
                    TimeUnit.SECONDS.sleep(registerResponse.getConnection_retry_interval());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        assert proccessor != null;
        OpenflowHandShake openflowHandShake = new OpenflowHandShake(proccessor.reformedOpenflowCoder, cpeInfo.getDatapathId());
        openflowHandShake.doOpenflowHandShakeWithServer();
    }
}