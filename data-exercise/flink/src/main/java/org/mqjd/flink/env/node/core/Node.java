package org.mqjd.flink.env.node.core;

interface Node {

    String getType();

    default NodeType getDataType() {
        return NodeType.CONNECTOR;
    }

    <T extends BaseNode> void merge(T node);
}
