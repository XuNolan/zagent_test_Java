package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.refactor.sprocess.ServerConnProcedure;


@Slf4j
public class Main {

    public static void main(String[] args) {
        ServerConnProcedure serverConnProcedure = new ServerConnProcedure();
        serverConnProcedure.doServerConnProcedure();
    }
}