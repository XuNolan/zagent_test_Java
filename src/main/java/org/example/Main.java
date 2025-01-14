package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.cprocess.TerminalConnProcedure;
import org.example.refactor.sprocess.ServerConnProcedure;


@Slf4j
public class Main {

    public static void main(String[] args) {
//        TerminalConnProcedure proc = new TerminalConnProcedure();
//        try {
//            proc.doTerminalConnProcedure();
//        }catch (Exception e){
//            log.error("conn with terminal fail, {}",e.getMessage());
//            return;
//        }
        ServerConnProcedure serverConnProcedure = new ServerConnProcedure();
        serverConnProcedure.doServerConnProcedure();


    }
}