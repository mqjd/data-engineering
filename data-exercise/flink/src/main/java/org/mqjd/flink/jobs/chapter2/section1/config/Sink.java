package org.mqjd.flink.jobs.chapter2.section1.config;

public class Sink extends Vertex {

    private String topic;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        if (topic != null) {
            this.topic = topic;
        }
    }

    public void merge(Sink sink) {
        if (sink != null) {
            super.merge(sink);
            if (sink.getTopic() != null) {
                this.setTopic(sink.getTopic());
            }
        }
    }
}
