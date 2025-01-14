package org.example.refactor.sprocess.protocol.reformedOpenflow;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum OFPTType {

    OFPT_HELLO("OFPT_HELLO", (byte)0),
    OFPT_ERROR("OFPT_ERROR", (byte)1),
    OFPT_ECHO_REQUEST("OFPT_ECHO_REQUEST", (byte)2),
    OFPT_ECHO_REPLY("OFPT_ECHO_REPLY", (byte)3),
    OFPT_VENDOR("OFPT_VENDOR", (byte)4),
    OFPT_FEATURES_REQUEST("OFPT_FEATURES_REQUEST", (byte)5),
    OFPT_FEATURES_REPLY("OFPT_FEATURES_REPLY", (byte)6),
    OFPT_GET_CONFIG_REQUEST("OFPT_GET_CONFIG_REQUEST", (byte)7),
    OFPT_GET_CONFIG_REPLY("OFPT_GET_CONFIG_REPLY", (byte)8),
    OFPT_SET_CONFIG("OFPT_SET_CONFIG", (byte)9),
    OFPT_PACKET_IN("OFPT_PACKET_IN", (byte)10),
    OFPT_FLOW_REMOVED("OFPT_FLOW_REMOVED", (byte)11),
    OFPT_PORT_STATUS("OFPT_PORT_STATUS", (byte)12),
    OFPT_PACKET_OUT("OFPT_PACKET_OUT", (byte)13),
    OFPT_FLOW_MOD("OFPT_FLOW_MOD", (byte)14),
    OFPT_GROUP_MOD("OFPT_GROUP_MOD", (byte)15),
    OFPT_PORT_MOD("OFPT_PORT_MOD", (byte)16),
    OFPT_TABLE_MOD("OFPT_TABLE_MOD", (byte)17),
    OFPT_MULTIPART_REQUEST("OFPT_MULTIPART_REQUEST", (byte)18),
    OFPT_MULTIPART_REPLY("OFPT_MULTIPART_REPLY", (byte)19),
    OFPT_BARRIER_REQUEST("OFPT_BARRIER_REQUEST", (byte)20),
    OFPT_BARRIER_REPLY("OFPT_BARRIER_REPLY", (byte)21),
    OFPT_QUEUE_GET_CONFIG_REQUEST("OFPT_QUEUE_GET_CONFIG_REQUEST", (byte)22),
    OFPT_QUEUE_GET_CONFIG_REPLY("OFPT_QUEUE_GET_CONFIG_REPLY", (byte)23),
    OFPT_ROLE_REQUEST("OFPT_ROLE_REQUEST", (byte)24),
    OFPT_ROLE_REPLY("OFPT_ROLE_REPLY", (byte)25),
    OFPT_GET_ASYNC_REQUEST("OFPT_GET_ASYNC_REQUEST", (byte)26),
    OFPT_GET_ASYNC_REPLY("OFPT_GET_ASYNC_REPLY", (byte)27),
    OFPT_SET_ASYNC("OFPT_SET_ASYNC", (byte)28),
    OFPT_METER_MOD("OFPT_METER_MOD", (byte)29),

    OFPT_OVSDB("OFPT_OVSDB", (byte)99),
    ;

    private final String describe;
    private final byte value;

    public byte getValue() {
        return value;
    }
    public String getDescribe() {
        return describe;
    }
}
