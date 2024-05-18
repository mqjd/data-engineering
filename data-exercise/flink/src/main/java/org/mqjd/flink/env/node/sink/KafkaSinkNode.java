package org.mqjd.flink.env.node.sink;

import org.mqjd.flink.env.node.core.BaseNode;
import org.mqjd.flink.env.node.core.SinkNode;

public class KafkaSinkNode extends BaseNode implements SinkNode {

    private String topic;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        if (topic != null) {
            this.topic = topic;
        }
    }

    public void merge(KafkaSinkNode sink) {
        if (sink != null) {
            super.merge(sink);
            if (sink.getTopic() != null) {
                this.setTopic(sink.getTopic());
            }
        }
    }
}
