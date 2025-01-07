package org.example.openflow;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.cpe.CpeInfo;
import org.example.openflow.entity.openflow.OFPHeader;
import org.example.openflow.entity.openflow.OpenflowPkgEntity;
import org.example.openflow.entity.ovsdb.OvsdbRequestPkg;
import org.example.openflow.entity.openflow.payload.OvsdbData;
import org.example.openflow.entity.ovsdb.OvsdbRespPkg;
import org.example.openflow.entity.ovsdb.ovsdbParam.OvsdbParam;
import org.example.openflow.entity.ovsdb.ovsdbParam.OvsdbRegisterEntity;
import org.example.openflow.entity.ovsdb.result.OvsdbRegisterResult;
import org.example.utils.RandomStringUtils;

import java.io.*;
import java.net.Socket;

@Slf4j
public class OpenflowConnProccessor {

    private OpenflowCoder openflowCoder;

    public boolean doOvsdbRegister(String ip, int port, String token, CpeInfo cpeInfo){
        Socket socket;
        OutputStream out;
        InputStream in;
        try {
            socket = new Socket(ip, port);
            out = socket.getOutputStream();
            in = socket.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        openflowCoder = new OpenflowCoder(in, out);

        OvsdbRegisterEntity ovsdbRegisterEntity = CpeInfo.toOvsdbRegisterEntity(cpeInfo).initRemains(token);
        String id = RandomStringUtils.getNumber(16);
        OvsdbRequestPkg ovsdbRequestPkg = OvsdbRequestPkg.builder().id(id).method("dm.deviceRegister").params(new OvsdbParam[]{ovsdbRegisterEntity}).build();
        String jsonstr = JSON.toJSONString(ovsdbRequestPkg);

        OpenflowPkgEntity openflowPkgEntity = new OpenflowPkgEntity();
        OFPHeader ofpHeader = new OFPHeader((byte)4, (byte)99, (byte)0, (byte)0);

        OvsdbData ovsdbData = new OvsdbData();
        ovsdbData.setLength(jsonstr.length()+OpenflowPkgEntity.OVSDBHEADERLEN);
        ovsdbData.setJsonStr(jsonstr);

        openflowPkgEntity.setHeader(ofpHeader);
        openflowPkgEntity.setPayload(ovsdbData);

        log.info("jsonstr:{}",jsonstr);
        log.info("ovsdbEntity:{}", ovsdbRequestPkg);
        log.info("openflowPkg:{}",openflowPkgEntity);
        log.info("pkgLen:{}", openflowPkgEntity.getHeader().getLength());
        log.info("ovsdbLen:{}", ovsdbData.getLength());
        try {
            openflowCoder.writeAndFlush(openflowPkgEntity);
        } catch (IOException e) {
            log.error("openflow coder write failed, message {}", openflowPkgEntity);
            try {
                socket.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        OpenflowPkgEntity response = null;

        while(true){
            try{
                response = openflowCoder.read();
            } catch (IOException e) {
                log.error("openflow coder read failed, message {}", response);
                try {
                    socket.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            log.info("response:{}",response);

            assert response != null;

            if(response.header.type == 99){
//                log.info(String.valueOf(response));
                OvsdbData responseOvsdbData = (OvsdbData)response.payload;
                OvsdbRespPkg ovsdbRespPkg = JSON.parseObject(responseOvsdbData.getJsonStr(), OvsdbRespPkg.class);
                if(!ovsdbRespPkg.getId().equals(id)){
                    log.info("getResp but id not equal. msg{}", ovsdbRespPkg);
                    continue;
                }
                OvsdbRegisterResult ovsdbRegisterResult = (OvsdbRegisterResult) ovsdbRespPkg.getResult()[0];
                log.info("OVSDB REGISTER SUCCESS. msg{}", ovsdbRegisterResult);
                return true;
            }else if(response.header.type == 2){ //reply
                response.header.setType((byte)3);

                try {
                    openflowCoder.writeAndFlush(response);
                }catch (IOException e) {
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }
    }
}
