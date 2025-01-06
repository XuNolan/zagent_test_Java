package org.example.openflow.entity.ovsdbParam;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.example.utils.RandomStringUtils;

import java.io.Serializable;

@Data
@Builder
@Accessors(chain = true)
@ToString
public class OvsdbRegisterEntity implements OvsdbParam, Serializable {

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
        this.setToken(token);
        this.setIs_initialize("true");
        String datapath_id = RandomStringUtils.getCapitalAndNumber(16);
        String newDatapath;
        if(!Character.isDigit(datapath_id.charAt(0)) || datapath_id.charAt(0) > '7'){
            newDatapath = "7"+datapath_id.substring(1);
            this.setDatapath_id(newDatapath);
        } else this.setDatapath_id(datapath_id);
        return this;
    }
}
