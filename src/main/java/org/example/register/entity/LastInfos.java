package org.example.register.entity;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Builder
@Data
@Accessors(chain = true)
@ToString
public class LastInfos implements Serializable {
    public String last_upgrade_status;
    public String last_upgrade_version;
    public String last_error_code;
    public String last_error_message;
}
