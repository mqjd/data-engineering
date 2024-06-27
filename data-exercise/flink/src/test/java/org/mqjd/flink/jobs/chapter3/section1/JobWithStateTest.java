package org.mqjd.flink.jobs.chapter3.section1;

import java.util.concurrent.CompletableFuture;

import org.apache.flink.api.common.JobStatus;
import org.junit.Test;
import org.mqjd.flink.containers.ContainerBaseTest;
import org.mqjd.flink.function.TroubleMaker;
import org.mqjd.flink.util.TimerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobWithStateTest extends ContainerBaseTest {

    private static final Logger LOG = LoggerFactory.getLogger(JobWithStateTest.class);

    @Test
    public void given_correct_input_and_output_when_execute_then_success() throws Exception {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        executeJobAsync(() -> {
            try {
                JobWithState.main(new String[0]);
            } catch (Exception e) {
                LOG.error("Error execute JobWithState", e);
            }
        }, jobStatus -> {
            if (jobStatus.equals(JobStatus.RUNNING)) {
                TimerUtil.timeout(() -> TroubleMaker.makeTrouble(new RuntimeException("interrupted by test")), 10_000L);
            } else if (jobStatus.isTerminalState()) {
                result.complete(true);
            }
        });
        result.get();
    }
}