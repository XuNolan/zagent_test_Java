package org.example.ovsdb.entity.request;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.example.ovsdb.entity.request.ovsdbParam.OvsdbParam;

@Data
@Builder
@Accessors(chain = true)
@ToString
public class OvsdbRequestPkg {
    String id;
    String method;
    OvsdbParam[] params;
}
