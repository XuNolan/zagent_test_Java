package org.example.openflow.entity.ovsdb;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.example.openflow.entity.ovsdb.ovsdbParam.OvsdbParam;

@Data
@Builder
@Accessors(chain = true)
@ToString
public class OvsdbRequestPkg {
    String id;
    String method;
    OvsdbParam[] params;
}
