package org.mqjd.flink.env.node.core;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonSubTypes;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.mqjd.flink.env.node.connector.JdbcConnectorNode;
import org.mqjd.flink.env.node.source.KafkaSourceNode;
import org.mqjd.flink.env.node.source.MySqlCdcNode;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({@JsonSubTypes.Type(value = KafkaSourceNode.class, name = "kafka"),
    @JsonSubTypes.Type(value = MySqlCdcNode.class, name = "mysql-cdc"),
    @JsonSubTypes.Type(value = JdbcConnectorNode.class, name = "jdbc")})
public interface SourceNode extends Node {

    default NodeType getDataType() {
        return NodeType.SOURCE;
    }
}
