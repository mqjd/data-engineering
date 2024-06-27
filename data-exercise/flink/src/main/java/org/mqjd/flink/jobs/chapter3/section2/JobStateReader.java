package org.mqjd.flink.jobs.chapter3.section2;

import org.mqjd.flink.env.Environment;
import org.mqjd.flink.env.EnvironmentParser;

public class JobStateReader {

    public JobStateBackend read(String jobConfig, String[] args) throws Exception {
        Environment environment = EnvironmentParser.parse(jobConfig, new String[0]);
        JobStateContext jobStateContext = JobStateContext.create(environment, args);
        return jobStateContext.getJobStateBackend();
    }

}
