package org.example.cpe;

import lombok.*;
import lombok.experimental.Accessors;
import org.example.openflow.entity.ovsdbParam.OvsdbRegisterEntity;
import org.example.register.entity.RegisterRequest;
import org.example.utils.RandomStringUtils;

@Data
@Accessors(chain = true)
@Builder
public class CpeInfo {
    private String loid;
    private String password; //12
    private String sn; //15
    private String ip_address;
    private String mac_address;
    private String oui; //6
    private Integer duration;
    private String fw_version;
    private String hw_version;
    private String kit_version;
    private String model;
    private String vendor;

    public static CpeInfo getRamdonCpe(){
        CpeInfo cpeInfo = CpeInfo.builder().
                loid(null).
                password(RandomStringUtils.getCapitalAndNumber(12)).
                sn(RandomStringUtils.getNumber(15)).
                oui(RandomStringUtils.getCapitalAndNumber(6)).
                ip_address("10.1.80.26").
                mac_address("7C:6A:60:D3:D9:90").duration(5521).
                fw_version("V6F4.AA1.01").hw_version("HX6-FM1_V1").kit_version("2.1.0").model("HX6-FM1").vendor("CMDC")
        .build();
        return cpeInfo;

    }

    public static RegisterRequest toRegisterRequest(CpeInfo cpeInfo) {
        return RegisterRequest.builder()
                .loid(cpeInfo.getLoid()).password(cpeInfo.getPassword()).sn(cpeInfo.getSn()).ip_address(cpeInfo.getIp_address())
                .mac_address(cpeInfo.getMac_address()).oui(cpeInfo.getOui()).duration(cpeInfo.getDuration())
                .fw_version(cpeInfo.getFw_version()).hw_version(cpeInfo.getHw_version()).kit_version(cpeInfo.getKit_version())
                .vendor(cpeInfo.getVendor()).model(cpeInfo.getModel())
                .build();
    }

    public static OvsdbRegisterEntity toOvsdbRegisterEntity(CpeInfo cpeInfo) {
        return OvsdbRegisterEntity.builder()
                .sn(cpeInfo.getSn()).password(cpeInfo.getPassword()).mac_address(cpeInfo.getMac_address())
                .duration(cpeInfo.getDuration()).fw_version(cpeInfo.getFw_version()).kit_version(cpeInfo.getKit_version())
                .hw_version(cpeInfo.getHw_version()).model(cpeInfo.getModel()).vendor(cpeInfo.getVendor())
                .build();
    }
}
