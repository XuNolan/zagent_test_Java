package org.example.refactor.sprocess.protocol.reformedOpenflow;

import lombok.*;
import org.example.refactor.sprocess.protocol.reformedOpenflow.payload.ReformedOpenflowPayload;

import java.io.Serializable;

@Data
@ToString
@NoArgsConstructor
public class ReformedOpenflowPkgEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    public static int OFPHEADERLEN = 8;
    public static int OVSDBHEADERLEN = 12;


    public OFPHeader header;
    public ReformedOpenflowPayload payload;


}
