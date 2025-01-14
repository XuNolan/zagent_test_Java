package org.example.refactor.sprocess.protocol.reformedOpenflow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OFPHeader implements Serializable {
    public byte version;
    public byte type;
    public short length;
    public int xid;
    public static OFPHeader getHeaderOfOvsdb(){
        return new OFPHeader((byte)4, org.example.refactor.sprocess.protocol.reformedOpenflow.OFPTType.OFPT_OVSDB.getValue(), (short) 0, 0);
    }
}