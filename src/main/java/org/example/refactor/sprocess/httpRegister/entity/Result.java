package org.example.refactor.sprocess.httpRegister.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@ToString
@Data
public class Result implements Serializable {
    private String controller_ip;
    private Double delay_period;
    private String file_path;
    private String md5;
    private String token;
    private Integer controller_port ;
}
