package org.example.openflow.entity.ovsdb;

import lombok.Data;
import lombok.ToString;
import org.example.openflow.entity.ovsdb.result.Result;


@Data
@ToString
public class OvsdbRespPkg {
    String id;
    Result[] result;
}
