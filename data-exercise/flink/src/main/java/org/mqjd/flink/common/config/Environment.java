package org.mqjd.flink.common.config;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;

public class Environment {

    private DataSource source;

    private DataSink sink;

    @JsonProperty("job-config")
    private JobConfig jobConfig;

    public <T extends DataSource> T getSource() {
        //noinspection unchecked
        return (T) source;
    }

    public void setSource(DataSource source) {
        this.source = source;
    }

    public <T extends DataSink> T getSink() {
        //noinspection unchecked
        return (T) sink;
    }

    public void setSink(DataSink sink) {
        this.sink = sink;
    }

    public JobConfig getJobConfig() {
        return jobConfig;
    }

    public void setJobConfig(JobConfig jobConfig) {
        this.jobConfig = jobConfig;
    }

    public void merge(Environment environment) {
        source.merge(environment.getSource());
        sink.merge(environment.getSink());
    }
}
