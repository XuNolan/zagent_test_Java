package org.example.refactor.sprocess.ovsdbRegister;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.cpe.CpeInfo;
import org.example.ovsdb.entity.response.result.OvsdbRegisterResult;
import org.example.refactor.sprocess.protocol.reformedOpenflow.OFPHeader;
import org.example.refactor.sprocess.protocol.reformedOpenflow.OFPTType;
import org.example.refactor.sprocess.protocol.reformedOpenflow.ReformedOpenflowPkgEntity;
import org.example.refactor.sprocess.protocol.reformedOpenflow.payload.ReformedOvsdbData;
import org.example.ovsdb.entity.request.OvsdbRequestPkg;
import org.example.ovsdb.entity.request.ovsdbParam.OvsdbParam;
import org.example.ovsdb.entity.request.ovsdbParam.OvsdbRegisterEntity;
import org.example.refactor.sprocess.protocol.coder.ReformedOpenflowCoder;
import org.example.utils.RandomStringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

@Slf4j
@Getter
public class OvsdbRegister {

    private String ip;
    private int port;
    private String token;
    private Socket socket;


    public OvsdbRegister(String ip, int port, String token) {
        this.ip = ip;
        this.port = port;
        this.token = token;
    }

    private ReformedOpenflowCoder coder;
    private String requestId;

    public ReformedOpenflowCoder doReformedOpenflowConnEstablish(){
        OutputStream out;
        InputStream in;
        try {
            socket = new Socket(ip, port);
            out = socket.getOutputStream();
            in = socket.getInputStream();
        } catch (IOException e) {
            log.error("ovsdb register socket conn failed, {}",e.getMessage());
            throw new RuntimeException(e);
        }
        ReformedOpenflowCoder reformedOpenflowCoder =  new ReformedOpenflowCoder(in, out, socket);
        this.coder = reformedOpenflowCoder;
        return reformedOpenflowCoder;
    }


    public void doRegister() throws Exception {
        ReformedOpenflowPkgEntity ovsdbRegisterRequestPkg = constructRegisterPkg();
        try {
            coder.writeAndFlush(ovsdbRegisterRequestPkg);
        } catch (IOException e) {
            log.error("openflow coder write failed, message {}", ovsdbRegisterRequestPkg);
            try {
                socket.close();
            } catch (IOException ex) {
                log.error("socket close failed, {}", ex.getMessage());
            }
            throw new Exception("openflow coder write failed");
        }

        ReformedOpenflowPkgEntity response = null;

        while(true){
            try{
                response = coder.read();
            } catch (IOException e) {
                log.error("openflow coder read failed, message {}", response);
                try {
                    socket.close();
                } catch (IOException ex) {
                    log.error("socket close failed, {}", ex.getMessage());
                }
                throw new Exception("openflow coder read failed");
            }

            if(response == null){
                throw new Exception("openflow coder read ovsdb register response null");
            }
            log.info("response:{}",response);

            if(response.header.type == OFPTType.OFPT_OVSDB.getValue()){
                ReformedOvsdbData responseReformedOvsdbData = (ReformedOvsdbData)response.payload;
                JSONObject jsonObject = JSON.parseObject(responseReformedOvsdbData.getJsonStr());
                String id = jsonObject.getString("id");
                if(!id.equals(requestId)){
                    log.info("getResp but id not equal. msg{}", responseReformedOvsdbData);
                    continue;
                }
                JSONArray jsonArray = jsonObject.getJSONArray("result");
                if(jsonArray!=null){
                    OvsdbRegisterResult ovsdbRegisterResult = jsonArray.getObject(0, OvsdbRegisterResult.class);
                    if (ovsdbRegisterResult != null) {
                        log.info("OVSDB REGISTER SUCCESS. msg{}", ovsdbRegisterResult);
                        return;
                    }
                    else {
                        log.info("OVSDB REGISTER FAILED. unexpected Result, msg{}", response);
                        throw new Exception("openflow coder write failed");
                    }
                }

            }else if(response.header.type == OFPTType.OFPT_ECHO_REQUEST.getValue()){ //echo
                response.header.setType(OFPTType.OFPT_ECHO_REPLY.getValue());
                try {
                    coder.writeAndFlush(response);
                }catch (IOException e) {
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        log.error("socket close failed, {}", ex.getMessage());
                    }
                    throw new Exception("openflow coder write failed");
                }
            }
        }
    }

    private ReformedOpenflowPkgEntity constructRegisterPkg(){
        CpeInfo cpeInfo = CpeInfo.getCpeInfo();
        OvsdbRegisterEntity ovsdbRegisterEntity = CpeInfo.toOvsdbRegisterEntity(cpeInfo).initRemains(token);
        cpeInfo.setDatapathId(ovsdbRegisterEntity.datapath_id);//在这里设置回去是为了传出去。
        requestId = RandomStringUtils.getNumber(16);
        OvsdbRequestPkg ovsdbRequestPkg = OvsdbRequestPkg.builder().id(requestId).method("dm.deviceRegister").params(new OvsdbParam[]{ovsdbRegisterEntity}).build();
        String jsonstr = JSON.toJSONString(ovsdbRequestPkg);

        ReformedOpenflowPkgEntity reformedOpenflowPkgEntity = new ReformedOpenflowPkgEntity();
        OFPHeader ofpHeader = OFPHeader.getHeaderOfOvsdb();

        ReformedOvsdbData reformedOvsdbData = new ReformedOvsdbData();
        reformedOvsdbData.setLength(jsonstr.length()+ ReformedOpenflowPkgEntity.OVSDBHEADERLEN);
        reformedOvsdbData.setJsonStr(jsonstr);

        reformedOpenflowPkgEntity.setHeader(ofpHeader);
        reformedOpenflowPkgEntity.setPayload(reformedOvsdbData);

        log.info("jsonstr:{}",jsonstr);
        log.info("ovsdbEntity:{}", ovsdbRequestPkg);
        log.info("openflowPkg:{}", reformedOpenflowPkgEntity);
        log.info("pkgLen:{}", reformedOpenflowPkgEntity.getHeader().getLength());
        log.info("ovsdbLen:{}", reformedOvsdbData.getLength());
        return reformedOpenflowPkgEntity;
    }
}
