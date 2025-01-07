package org.example.openflow;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.cpe.CpeInfo;
import org.example.openflow.entity.openflow.OpenflowPkgEntity;
import org.example.openflow.entity.OvsdbPkg;
import org.example.openflow.entity.openflow.payload.OpenflowData;
import org.example.openflow.entity.openflow.payload.OvsdbData;
import org.example.openflow.entity.ovsdbParam.OvsdbParam;
import org.example.openflow.entity.ovsdbParam.OvsdbRegisterEntity;
import org.example.utils.RandomStringUtils;

import java.io.*;
import java.net.Socket;

@Slf4j
public class OpenflowConnProccessor {

    private OpenflowCoder openflowCoder;

    public void doOvsdbRegister(String ip, int port, String token, CpeInfo cpeInfo){
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
        OvsdbPkg ovsdbPkg = OvsdbPkg.builder().id(RandomStringUtils.getNumber(16)).method("dm.deviceRegister").params(new OvsdbParam[]{ovsdbRegisterEntity}).build();
        String jsonstr = JSON.toJSONString(ovsdbPkg);

        OpenflowPkgEntity openflowPkgEntity = new OpenflowPkgEntity();
        openflowPkgEntity.getHeader().setVersion((byte) 4);
        openflowPkgEntity.getHeader().setType((byte)99);
        openflowPkgEntity.getHeader().setLength((byte) 0);
        openflowPkgEntity.getHeader().setXid(0);

        OvsdbData ovsdbData = new OvsdbData();
        ovsdbData.setLength(jsonstr.length()+OpenflowPkgEntity.OVSDBHEADERLEN);
        ovsdbData.setJsonStr(jsonstr);
        openflowPkgEntity.setPayload(ovsdbData);

        log.info("jsonstr:{}",jsonstr);
        log.info("ovsdbEntity:{}",ovsdbPkg);
        log.info("openflowPkg:{}",openflowPkgEntity);
        log.info("pkgLen:{}", openflowPkgEntity.getHeader().getLength());
        log.info("ovsdbLen:{}", ovsdbData.getLength());
        openflowCoder.writeAndFlush(openflowPkgEntity);


        OpenflowPkgEntity response;

        while((response = openflowCoder.read()) != null){
            log.info("response:{}",response);
            if(response.header.type == 99){
                log.info(String.valueOf(response));
                return;
            }else if(response.header.type == 2){
                response.header.setType((byte)3);
                openflowCoder.writeAndFlush(response);
            }

        }

    }
}
