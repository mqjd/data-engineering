package org.mqjd.flink.jobs.chapter3.section2;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.flink.shaded.guava31.com.google.common.collect.Lists;
import org.junit.Test;
import org.mqjd.flink.jobs.chapter3.section1.JobWithState;
import org.mqjd.flink.jobs.chapter3.section2.core.States;
import org.mqjd.flink.source.CustomIteratorSourceSplit;

public class JobStateReaderTest {

    private static final String JOB_YAML = "jobs/chapter3/section2/job.yaml";

    @Test
    public void testReadOperatorState() throws Exception {
        String[] args = { "-c", JobWithState.class.getName() };
        JobStateBackend jobStateBackend = new JobStateReader().read(JOB_YAML, args);
        Map<Integer, List<CustomIteratorSourceSplit>> actualState =
            jobStateBackend.getVertexStateBackend(0).readOperatorState(States.SOURCE_SPLITS_STATE_DESC);

        Map<Integer, List<CustomIteratorSourceSplit>> expectState = new HashMap<>();
        expectState.put(0, Lists.newArrayList(new CustomIteratorSourceSplit(0, 150, 4, 1)));
        expectState.put(1, Lists.newArrayList(new CustomIteratorSourceSplit(0, 151, 4, 2)));
        expectState.put(2, Lists.newArrayList(new CustomIteratorSourceSplit(0, 152, 4, 3)));
        expectState.put(3, Lists.newArrayList(new CustomIteratorSourceSplit(0, 149, 4, 0)));
        assertEquals(expectState, actualState);
    }
}