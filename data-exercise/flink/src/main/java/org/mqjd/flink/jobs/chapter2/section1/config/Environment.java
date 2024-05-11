package org.mqjd.flink.jobs.chapter2.section1.config;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;

public class Environment {

    private Source source;

    private Sink sink;

    @JsonProperty("job-config")
    private JobConfig jobConfig;

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Sink getSink() {
        return sink;
    }

    public void setSink(Sink sink) {
        this.sink = sink;
    }

    public JobConfig getJobConfig() {
        return jobConfig;
    }

    public void setJobConfig(JobConfig jobConfig) {
        this.jobConfig = jobConfig;
    }

    public void merge(Environment environment) {
        source.addProperties(environment.getSource().getProps());
        sink.addProperties(environment.getSink().getProps());
    }
}
