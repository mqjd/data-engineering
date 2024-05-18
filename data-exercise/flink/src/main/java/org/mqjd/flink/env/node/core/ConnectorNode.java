package org.mqjd.flink.env.node.core;

public interface ConnectorNode extends SinkNode, SourceNode {
    String getType();
    default NodeType getDataType() {
        return NodeType.CONNECTOR;
    }
}
