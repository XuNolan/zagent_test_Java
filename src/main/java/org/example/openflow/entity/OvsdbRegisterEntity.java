package org.example.openflow.entity;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.example.register.entity.LastInfos;
import org.example.register.entity.RegisterRequest;
import org.example.utils.RandomStringUtils;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@Accessors(chain = true)
@ToString
public class OvsdbRegisterEntity implements Serializable {
    public String sn;
    public String password;
    public String mac_address;
    public int duration;
    public String is_initialize;
    public String fw_version;
    public String model;
    public String vendor;
    public String datapath_id;
    public String token;
    public String kit_version;
    public String hw_version;

    public OvsdbRegisterEntity initRemains(String token){
        this.setDatapath_id(RandomStringUtils.getCapitalAndNumber(16)).setIs_initialize("true").setToken(token);
        return this;
    }
}
