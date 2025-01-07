package org.example.openflow.entity.openflow.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OpenflowData implements OpenflowPayload, Serializable {
    byte[] openflowData;
}
