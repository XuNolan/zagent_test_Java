package org.example.ovsdb.entity.response.result;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class OvsdbRegisterResult implements Result, Serializable {
    Integer connection_echo_interval;
    Integer connection_retry_interval;
}
