package org.example.refactor.sprocess;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.ovsdb.entity.request.OvsdbRequestPkg;
import org.example.refactor.sprocess.protocol.reformedOpenflow.OFPHeader;
import org.example.refactor.sprocess.protocol.reformedOpenflow.OFPTType;
import org.example.refactor.sprocess.protocol.reformedOpenflow.ReformedOpenflowPkgEntity;
import org.example.refactor.sprocess.protocol.reformedOpenflow.payload.ReformedOpenflowData;
import org.example.refactor.sprocess.protocol.reformedOpenflow.payload.ReformedOvsdbData;

import java.io.IOException;

@Slf4j
public class ServerPkgDispatcher {
    //信息分发；

    public static ReformedOpenflowPkgEntity dispatch(ReformedOpenflowPkgEntity reformedOpenflowPkgEntity){
        //处理封装和解封装
        if(OFPTType.OFPT_OVSDB.getValue() == reformedOpenflowPkgEntity.header.type){
            String ovsdbStr = ((ReformedOvsdbData)reformedOpenflowPkgEntity.payload).jsonStr;
            String jsonResp = processOvsdbRequest(ovsdbStr);
            if(jsonResp == null) return null;
            //封装;
            ReformedOpenflowPkgEntity response = new ReformedOpenflowPkgEntity();
            response.setHeader(OFPHeader.getHeaderOfOvsdb());
            ReformedOvsdbData reformedOvsdbData = new ReformedOvsdbData();
            reformedOvsdbData.setJsonStr(jsonResp);
            reformedOvsdbData.setLength(jsonResp.length());
            response.setPayload(reformedOvsdbData);
            return response;
        }else{
            ReformedOpenflowPkgEntity response = processOpenflow(reformedOpenflowPkgEntity);
            return response;
        }
    }


    public static String processOvsdbRequest(String jsonStr) {
        if(jsonStr.contains("methods")){//请求；
            OvsdbRequestPkg ovsdbRequestPkg = JSON.parseObject(jsonStr, OvsdbRequestPkg.class);
            switch (ovsdbRequestPkg.getMethod()){
                case "":
            }

        } else if(jsonStr.contains("result")){ //响应。直接丢掉。

        } else{
            log.error("unknow msg: {}", jsonStr);
        }
        return null;
    }

    public static ReformedOpenflowPkgEntity processOpenflow(ReformedOpenflowPkgEntity reformedOpenflowPkgEntity) {
        OFPHeader ofpHeader = reformedOpenflowPkgEntity.header;
        if(ofpHeader.getType() == OFPTType.OFPT_ECHO_REQUEST.getValue()){
           ofpHeader.setType(OFPTType.OFPT_ECHO_REPLY.getValue());
           return reformedOpenflowPkgEntity;
        } else if(ofpHeader.type == OFPTType.OFPT_HELLO.getValue()) {
            return reformedOpenflowPkgEntity;
        } else {
            log.error("not complete yet {}", reformedOpenflowPkgEntity);
        }
        return null;
    }

}
