package org.mqjd.flink.jobs.chapter2.section1.config;

public class Source extends Vertex {

    private String topics;

    public String getTopics() {
        return topics;
    }

    public void setTopics(String topics) {
        if (topics != null) {
            this.topics = topics;
        }
    }

    public void merge(Source source) {
        if (source != null) {
            super.merge(source);
            this.setTopics(source.getTopics());
        }
    }

}
