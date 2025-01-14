package org.example.cprocess.protocal.openflow;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.refactor.sprocess.protocol.coder.ReformedOpenflowCoder;
import org.example.refactor.sprocess.protocol.reformedOpenflow.OFPHeader;
import org.example.refactor.sprocess.protocol.reformedOpenflow.OFPTType;
import org.example.refactor.sprocess.protocol.reformedOpenflow.ReformedOpenflowPkgEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;

@Slf4j
public class OpenflowStateMachine {

    private final static int init = 0;
    private final static int waitHello = 4;
    private final static int sendFeatureRequest = 5;
    private final static int waitFeaturesReply = 6;
    private final static int established = 7;

    @Getter
    @Setter
    private ReformedOpenflowCoder coder;

    private int state;

    public static OpenflowStateMachine openSocket(String ip, int port) throws IOException {
        OutputStream out;
        InputStream in;
        Socket socket;
        try {
            socket = new Socket(ip, port);
            out = socket.getOutputStream();
            in = socket.getInputStream();
        } catch (IOException e) {
            log.error("ovsdb register socket conn failed, {}",e.getMessage());
            throw new RuntimeException(e);
        }

        OpenflowStateMachine stateMachine = new OpenflowStateMachine();
        stateMachine.coder = new ReformedOpenflowCoder(in, out, socket);
        stateMachine.state = init;
        return stateMachine;
    }


    public ReformedOpenflowCoder doOpenflowHandshakeWithTerminal() throws IOException {
        ReformedOpenflowPkgEntity readPkg;
        while(true) {
            readPkg = coder.read();
            if(readPkg.header.type == OFPTType.OFPT_ECHO_REQUEST.getValue()){
                readPkg.header.setType(OFPTType.OFPT_ECHO_REPLY.getValue());
                coder.writeAndFlush(readPkg);
            } else {
                switch(this.state){
                    case init:
                        this.state = waitHello;
                    case waitHello:
                        coder.writeAndFlush(readPkg);
                        this.state = sendFeatureRequest;
                    case sendFeatureRequest:
                        coder.writeAndFlush(getFeatureRequest());
                        this.state = waitFeaturesReply;
                        break;
                    case waitFeaturesReply:
                        log.info("recv feature reply success : {}", readPkg);
                        log.info("openflow conn established");
                        this.state = established;
                        return coder;
                }
            }
        }
    }

    private static ReformedOpenflowPkgEntity getFeatureRequest(){
        ReformedOpenflowPkgEntity featureRequest = new ReformedOpenflowPkgEntity();
        featureRequest.setHeader(new OFPHeader((byte)4, OFPTType.OFPT_FEATURES_REQUEST.getValue(), (short) 8, new Random().nextInt()));
        featureRequest.setPayload(null);
        return featureRequest;
    }
}
