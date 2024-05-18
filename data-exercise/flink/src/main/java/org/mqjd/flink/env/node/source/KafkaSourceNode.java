package org.mqjd.flink.env.node.source;

import org.mqjd.flink.env.node.core.BaseNode;
import org.mqjd.flink.env.node.core.SourceNode;
import org.mqjd.flink.util.ReflectionUtil;

public class KafkaSourceNode extends BaseNode implements SourceNode {

    private String topics;

    public String getTopics() {
        return topics;
    }

    public void setTopics(String topics) {
        if (topics != null) {
            this.topics = topics;
        }
    }

    public void merge(KafkaSourceNode source) {
        if (source != null) {
            super.merge(source);
            ReflectionUtil.copyProperties(source, this);
        }
    }
}
