package org.example;

import org.example.cpe.CpeInfo;
import org.example.register.Register;
import org.example.register.entity.RegisterRequest;
import org.example.register.entity.RegisterResponse;

public class Main {

    public static void main(String[] args) {
        CpeInfo cpeInfo = CpeInfo.getRamdonCpe();
        RegisterRequest registerRequest = CpeInfo.toRegisterRequest(cpeInfo).initRemains();
        RegisterResponse registerResponse = Register.doRegister(registerRequest);
        System.out.println(registerResponse);
    }
}