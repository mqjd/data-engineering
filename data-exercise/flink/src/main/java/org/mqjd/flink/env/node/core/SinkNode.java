package org.mqjd.flink.env.node.core;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonSubTypes;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.mqjd.flink.env.node.connector.JdbcConnectorNode;
import org.mqjd.flink.env.node.sink.KafkaSinkNode;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({@JsonSubTypes.Type(value = KafkaSinkNode.class, name = "kafka"),
    @JsonSubTypes.Type(value = JdbcConnectorNode.class, name = "jdbc")})
public interface SinkNode extends Node{
    default NodeType getDataType() {
        return NodeType.SINK;
    }
}
