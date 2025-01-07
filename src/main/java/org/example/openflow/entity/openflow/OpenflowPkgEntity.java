package org.example.openflow.entity.openflow;

import lombok.*;
import org.example.openflow.entity.openflow.payload.OpenflowPayload;

import java.io.Serializable;

@Data
@ToString
@NoArgsConstructor
public class OpenflowPkgEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    public static int OFPHEADERLEN = 8;
    public static int OVSDBHEADERLEN = 12;


    public OFPHeader header;
    public OpenflowPayload payload;


}
