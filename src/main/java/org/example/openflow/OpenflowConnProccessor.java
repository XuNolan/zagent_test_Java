package org.example.openflow;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.cpe.CpeInfo;
import org.example.openflow.entity.OpenflowPkgEntity;
import org.example.openflow.entity.OvsdbPkg;
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
        OvsdbPkg ovsdbPkg = OvsdbPkg.builder().id(RandomStringUtils.getNumber(16)).method("dm.deviceRegister").params(ovsdbRegisterEntity).build();
        String jsonstr = JSON.toJSONString(ovsdbPkg);

        OpenflowPkgEntity openflowPkgEntity = new OpenflowPkgEntity();
        openflowPkgEntity.getHeader().setVersion((byte) 4);
        openflowPkgEntity.getHeader().setType((byte)99);
        openflowPkgEntity.getHeader().setLength((byte) 0);
        openflowPkgEntity.getHeader().setXid(0);

        openflowPkgEntity.setLength(jsonstr.length()+OpenflowPkgEntity.OVSDBHEADERLEN);
        openflowPkgEntity.setJsonStr(jsonstr);

        log.info("jsonstr:{}",jsonstr);
        log.info("ovsdbEntity:{}",ovsdbPkg);
        log.info("openflowPkg:{}",openflowPkgEntity);
        log.info("pkgLen:{}", openflowPkgEntity.getHeader().getLength());
        log.info("ovsdbLen:{}", openflowPkgEntity.getLength());
        openflowCoder.writeAndFlush(openflowPkgEntity);

        log.info("write");
        while(true){
            OpenflowPkgEntity response = openflowCoder.read();

        }

    }
}
