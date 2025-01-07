package org.example.openflow;

import lombok.extern.slf4j.Slf4j;
import org.example.openflow.entity.openflow.OFPHeader;
import org.example.openflow.entity.openflow.OpenflowPkgEntity;
import org.example.openflow.entity.openflow.payload.OpenflowData;
import org.example.openflow.entity.openflow.payload.OpenflowPayload;
import org.example.utils.HexStringUtils;

import java.io.IOException;
import java.util.Random;

@Slf4j
public class OpenflowHandShake {
    //发起HELLO；
    //接收HELLO和FEATUREQUEST
    //响应RESPONSE;
    static byte OFPT_HELLO = (byte) 0;
    static byte OFPT_FEATURES_REQUEST = (byte) 8;
    static byte OFPT_FEATURES_REPLY = (byte) 8;

    public OpenflowCoder openflowCoder;
    public String datapathId;

    public OpenflowHandShake(OpenflowCoder openflowCoder, String datapathId) {
        this.openflowCoder = openflowCoder;
        this.datapathId = datapathId;
    }

    public void doOpenflowHandShakeWithServer(){
        try {
            int xid = sendHello();
            if(! recvHello(xid)){
                return;
            }
            xid = recvFeatureRequest();

            sendFeatureResponse(xid);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int sendHello() throws IOException {
        OpenflowPkgEntity pkgEntity = new OpenflowPkgEntity();

        Random random = new Random();
        int xid = random.nextInt();

        OFPHeader header = new OFPHeader((byte)4, OFPT_HELLO, (short)8, xid);
        pkgEntity.setHeader(header);
        pkgEntity.setPayload(null);
        openflowCoder.writeAndFlush(pkgEntity);
        log.info("send Hello success");
        return xid;
    }

    public boolean recvHello(int xid) throws IOException {
        OpenflowPkgEntity pkgEntity = openflowCoder.read();
        if(pkgEntity.header.type == OFPT_HELLO && pkgEntity.getHeader().xid == xid){
            log.info("recv Hello success");
            return true;
        }
        log.info("recv Hello fail, hello:{}",pkgEntity);
        return false;
    }

    public int recvFeatureRequest() throws IOException {
        OpenflowPkgEntity pkgEntity = openflowCoder.read();
        if(pkgEntity.header.type != OFPT_FEATURES_REQUEST){
            log.info("recv OFPT_FEATURES_REQUEST fail");
        } else {
            log.info("recv OFPT_FEATURES_REQUEST success");
        }
        return pkgEntity.getHeader().xid;
    }

    public boolean sendFeatureResponse(int xid) throws IOException {
        OpenflowPkgEntity pkgEntity = new OpenflowPkgEntity();
        String hexs = datapathId + "00000000fe0000000000004f000000";
        OFPHeader header = new OFPHeader((byte)4, OFPT_FEATURES_REPLY, (short)32, xid);
        pkgEntity.setHeader(header);
        OpenflowPayload payload = new OpenflowData(HexStringUtils.hexTobytes(hexs));
        pkgEntity.setPayload(payload);
        openflowCoder.writeAndFlush(pkgEntity);
        log.info("send FeatureResponse success");
        return true;
    }
}
