package org.example.ovsdb.entity.response;

import lombok.Data;
import lombok.ToString;
import org.example.ovsdb.entity.response.result.Result;


@Data
@ToString
public class OvsdbRespPkg {
    String id;
    Result[] result;
}
