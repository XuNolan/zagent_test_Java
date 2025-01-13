package org.example.refactor.sprocess.protocol.reformedOpenflow.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReformedOpenflowData implements ReformedOpenflowPayload, Serializable {
    byte[] reformedOpenflowData;
}
