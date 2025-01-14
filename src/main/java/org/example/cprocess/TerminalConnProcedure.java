package org.example.cprocess;

import lombok.extern.slf4j.Slf4j;
import org.example.cpe.CpeInfo;
import org.example.cprocess.protocal.coder.OvsdbCoder;
import org.example.cprocess.protocal.openflow.OpenflowStateMachine;
import org.example.refactor.sprocess.protocol.coder.ReformedOpenflowCoder;

import java.io.IOException;

@Slf4j
public class TerminalConnProcedure {
    //两个线程openflow线程直接握手，ovsdb线程直接连接。
    String terminalIp = "127.0.0.1";
    int openflowPort = 6633;
    String OvsdbUnixSocketFileName = "/var/run/openvswitch/db.sock";

    public void doTerminalConnProcedure() throws IOException {
        //openflow握手；
        try {
            OpenflowStateMachine openflowStateMachine = OpenflowStateMachine.openSocket(terminalIp, openflowPort);
            ReformedOpenflowCoder reformedOpenflowCoder = openflowStateMachine.doOpenflowHandshakeWithTerminal();
            CpeInfo.getCpeInfo().setServerReformedOpenflowCoder(reformedOpenflowCoder);
        }catch (IOException e){
            log.error("openflow conn establish failed");
            log.error(e.getMessage());
            throw e;
        }
        log.info("openflow conn established");
        //todo: 继续接收相关echo信息；

        //ovsdb握手；
        OvsdbCoder ovsdbCoder;
        try {
            ovsdbCoder = OvsdbCoder.OpenOvsdbConnection(OvsdbUnixSocketFileName);
        }catch (IOException e){
            log.error("ovsdb coder is null, dial unix socekt fail");
            throw e;
        }
        CpeInfo.getCpeInfo().setOvsdbCoder(ovsdbCoder);
        log.info("ovsdb conn established");
    }
}
