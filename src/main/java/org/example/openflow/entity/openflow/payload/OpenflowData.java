package org.example.openflow.entity.openflow.payload;

import lombok.Data;

import java.io.Serializable;

@Data
public class OpenflowData implements OpenflowPayload, Serializable {
    byte[] openflowData;
}
