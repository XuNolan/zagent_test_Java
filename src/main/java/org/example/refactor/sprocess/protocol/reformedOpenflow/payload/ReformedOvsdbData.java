package org.example.refactor.sprocess.protocol.reformedOpenflow.payload;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class ReformedOvsdbData implements ReformedOpenflowPayload, Serializable {
    public int length;
    public String jsonStr;
}
