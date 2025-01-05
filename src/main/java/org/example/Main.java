package org.example;

import org.example.cpe.CpeInfo;
import org.example.openflow.OpenflowConnProccessor;
import org.example.register.Register;
import org.example.register.entity.RegisterRequest;
import org.example.register.entity.RegisterResponse;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        CpeInfo cpeInfo = CpeInfo.getRamdonCpe();
        RegisterRequest registerRequest = CpeInfo.toRegisterRequest(cpeInfo).initRemains();
        RegisterResponse registerResponse = Register.doRegister(registerRequest);
        System.out.println(registerResponse);
        assert registerResponse != null;
        if(Objects.equals(registerResponse.getCode(), "200000")) {
            String ip = registerResponse.getResult().getController_ip();
            int port = registerResponse.getResult().getController_port();
            String token = registerResponse.getResult().getToken();
            double SleepTime = registerResponse.getResult().getDelay_period();
            try {
                TimeUnit.SECONDS.sleep((long) SleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
            OpenflowConnProccessor proccessor = new OpenflowConnProccessor();
            proccessor.doOvsdbRegister(ip, port, token, cpeInfo);
        }else
            return;
    }
}