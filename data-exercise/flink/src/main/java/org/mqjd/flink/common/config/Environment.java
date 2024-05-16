package org.mqjd.flink.common.config;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;
import org.mqjd.flink.util.ReflectionUtil;

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

    void merge(Environment environment) {
        ReflectionUtil.invoke(source, "merge", environment.getSource());
        ReflectionUtil.invoke(sink, "merge", environment.getSink());
    }
}
