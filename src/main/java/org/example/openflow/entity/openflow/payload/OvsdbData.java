package org.example.openflow.entity.openflow.payload;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class OvsdbData implements OpenflowPayload, Serializable {
    public int length;
    public String jsonStr;
}
