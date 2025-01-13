package org.example.refactor.sprocess.httpRegister.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class RegisterResponse implements Serializable {
    private String id;
    private String code;
    private String message;
    private int connection_retry_interval;
    private int retry_times;
    private Result result;
}
