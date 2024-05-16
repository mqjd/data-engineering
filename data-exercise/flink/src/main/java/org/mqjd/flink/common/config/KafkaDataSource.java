package org.mqjd.flink.common.config;

public class KafkaDataSource extends DataSource {

    private String topics;

    public String getTopics() {
        return topics;
    }

    public void setTopics(String topics) {
        if (topics != null) {
            this.topics = topics;
        }
    }

    public void merge(KafkaDataSource source) {
        if (source != null) {
            super.merge(source);
            this.setTopics(source.getTopics());
        }
    }
}
