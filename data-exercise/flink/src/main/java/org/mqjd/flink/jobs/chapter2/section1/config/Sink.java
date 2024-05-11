package org.mqjd.flink.jobs.chapter2.section1.config;

public class Sink extends Vertex {

    private String topic;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
