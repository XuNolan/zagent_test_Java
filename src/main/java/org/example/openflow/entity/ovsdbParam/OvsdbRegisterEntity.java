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
        String[] macs = mac_address.split(":");
        String datapath_id = "0000" + macs[0] + macs[1] + macs[2] + macs[3] + macs[4] + macs[5];
        this.setDatapath_id(datapath_id);
        return this;
    }
}
