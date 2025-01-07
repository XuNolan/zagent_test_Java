package org.example.openflow.entity;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.example.openflow.entity.ovsdbParam.OvsdbParam;

@Data
@Builder
@Accessors(chain = true)
@ToString
public class OvsdbPkg {
    String id;
    String method;
    OvsdbParam[] params;
}
