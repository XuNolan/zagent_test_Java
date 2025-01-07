package org.example.openflow.entity.openflow;

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
}