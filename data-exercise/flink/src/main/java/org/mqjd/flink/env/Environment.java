package org.mqjd.flink.env;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;
import org.mqjd.flink.env.node.core.SinkNode;
import org.mqjd.flink.env.node.core.SourceNode;
import org.mqjd.flink.util.ReflectionUtil;

public class Environment {

    private SourceNode source;

    private SinkNode sink;

    @JsonProperty("job-config")
    private JobConfig jobConfig;

    public <T extends SourceNode> T getSource() {
        //noinspection unchecked
        return (T) source;
    }

    public void setSource(SourceNode source) {
        this.source = source;
    }

    public <T extends SinkNode> T getSink() {
        //noinspection unchecked
        return (T) sink;
    }

    public void setSink(SinkNode sink) {
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
