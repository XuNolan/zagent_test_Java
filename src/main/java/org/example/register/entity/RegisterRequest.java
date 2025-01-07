package org.example.register.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@Accessors(chain = true)
@ToString
public class RegisterRequest implements Serializable {
    private String id;
    private String loid;
    private String password;
    private String sn;
    private String ip_address;
    private String mac_address;
    private String oui;
    private String duration;
    @JSONField(name = "is_initialize")
    private boolean is_initialize;
    private String fw_version;
    private String hw_version;
    private String kit_version;
    private String model;
    private String vendor;
    private String connection_trigger;

    private LastInfos last_infos;

    public RegisterRequest initRemains(){
        LastInfos lastInfos = LastInfos.builder().last_upgrade_status("-1").last_upgrade_version("").last_error_code("0").last_error_message("success")
                .build();
        this.setId(UUID.randomUUID().toString()).set_initialize(true).setConnection_trigger("Reboot").setLast_infos(lastInfos);
        return this;
    }
}
