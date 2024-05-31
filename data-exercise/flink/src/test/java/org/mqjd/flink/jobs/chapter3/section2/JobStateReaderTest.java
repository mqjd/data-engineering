package org.mqjd.flink.jobs.chapter3.section2;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.junit.Test;
import org.mqjd.flink.jobs.chapter3.section1.JobWithState;
import org.mqjd.flink.jobs.chapter3.section1.source.CustomIteratorSourceSplit;

public class JobStateReaderTest {

    private static final String JOB_YAML = "conf/chapter3/section2/job.yaml";

    @Test
    public void testStateRead() throws Exception {
        String[] args = {"-c", JobWithState.class.getName()};
        JobState jobState = new JobStateReader().read(JOB_YAML, args);
        List<CustomIteratorSourceSplit> sourceReaderState = jobState.getState(0)
            .getState("SourceReaderState");
        sourceReaderState.sort(Comparator.comparingInt(CustomIteratorSourceSplit::getSplitId));
        List<CustomIteratorSourceSplit> expectedSplits = Arrays.asList(
            new CustomIteratorSourceSplit(0, 149, 4, 0),
            new CustomIteratorSourceSplit(0, 150, 4, 1),
            new CustomIteratorSourceSplit(0, 151, 4, 2),
            new CustomIteratorSourceSplit(0, 152, 4, 3));
        assertEquals(expectedSplits, sourceReaderState);
    }
}