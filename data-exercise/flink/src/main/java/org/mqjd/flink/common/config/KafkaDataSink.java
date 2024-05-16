package org.mqjd.flink.common.config;

public class KafkaDataSink extends DataSink {

    private String topic;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        if (topic != null) {
            this.topic = topic;
        }
    }

    public void merge(KafkaDataSink sink) {
        if (sink != null) {
            super.merge(sink);
            if (sink.getTopic() != null) {
                this.setTopic(sink.getTopic());
            }
        }
    }
}
