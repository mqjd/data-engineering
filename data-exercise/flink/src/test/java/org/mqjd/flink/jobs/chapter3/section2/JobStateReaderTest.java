package org.mqjd.flink.jobs.chapter3.section2;

import org.junit.Test;
import org.mqjd.flink.containers.ContainerBaseTest;
import org.mqjd.flink.jobs.chapter3.section1.JobWithState;

public class JobStateReaderTest  extends ContainerBaseTest {

    private static final String JOB_YAML = "conf/chapter3/section2/job.yaml";

    @Test
    public void testStateRead() throws Exception {
        String[] args = {"-c", JobWithState.class.getName()};
        new JobStateReader().read(JOB_YAML, args);
    }

}