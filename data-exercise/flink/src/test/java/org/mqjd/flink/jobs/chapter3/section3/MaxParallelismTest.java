package org.mqjd.flink.jobs.chapter3.section3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.apache.flink.api.common.JobStatus;
import org.apache.flink.api.java.tuple.Tuple2;
import org.junit.Test;
import org.mqjd.flink.containers.ContainerBaseTest;
import org.mqjd.flink.jobs.chapter3.section2.JobStateBackend;
import org.mqjd.flink.jobs.chapter3.section2.JobStateReader;
import org.mqjd.flink.jobs.chapter3.section2.VertexStateBackend;
import org.mqjd.flink.jobs.chapter3.section2.core.States;
import org.mqjd.flink.util.TimerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MaxParallelismTest extends ContainerBaseTest {

    private static final Logger LOG = LoggerFactory.getLogger(MaxParallelismTest.class);
    private static final String JOB_YAML = "jobs/chapter3/section3/job.yaml";

    @Test
    public void given_correct_input_and_output_when_execute_then_success() throws Exception {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        executeJobAsync(() -> {
            try {
                String[] params = {"-D", "job-config.parallelism.default=4",};
                MaxParallelism.main(params);
            } catch (Exception e) {
                LOG.error("Error execute MaxParallelism", e);
            }
        }, (client, jobStatus) -> {
            if (jobStatus.equals(JobStatus.RUNNING)) {
                TimerUtil.timeout(client::cancel, 10L * 1000);
            } else if (jobStatus.isTerminalState()) {
                result.complete(true);
            }
        });
        result.get();
    }

    @Test
    public void testReadKeyedState() throws Exception {
        String[] args = {"-c", MaxParallelism.class.getName()};
        JobStateBackend jobStateBackend = new JobStateReader().read(JOB_YAML, args);
        VertexStateBackend vertexStateBackend = jobStateBackend.getVertexStateBackend(1);
        assertTrue(vertexStateBackend.getVertex().getName().contains("GlobalWindows"));
        Map<Integer, Map<Tuple2<?, ?>, Long>> state = vertexStateBackend.readKeyedState(
            States.COUNT_TRIGGER_STATE_DESC);
        assertEquals(2, state.size());
        assertEquals(2, state.get(0).size());
        assertEquals(8, state.get(1).size());
    }
}