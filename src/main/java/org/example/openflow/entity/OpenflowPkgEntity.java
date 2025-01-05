package org.example.openflow.entity;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@ToString
public class OpenflowPkgEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    public static int OFPHEADERLEN = 8;
    public static int OVSDBHEADERLEN = 12;

    public OpenflowPkgEntity() {
        header = new OFPHeader();
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public class OFPHeader implements Serializable {
        public byte version;
        public byte type;
        public short length;
        public int xid;
    }

    public OFPHeader header;
    public int length;
    public String jsonStr;

    public byte[] OFPData;


}
