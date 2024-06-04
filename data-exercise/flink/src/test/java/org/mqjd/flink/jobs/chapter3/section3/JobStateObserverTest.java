package org.mqjd.flink.jobs.chapter3.section3;

import java.util.concurrent.CompletableFuture;
import org.apache.flink.api.common.JobStatus;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.junit.Test;
import org.mqjd.flink.containers.ContainerBaseTest;
import org.mqjd.flink.function.TroubleMaker;
import org.mqjd.flink.jobs.chapter3.section1.JobWithState;
import org.mqjd.flink.util.TimerUtil;
import org.mqjd.flink.utils.ByteKitUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobStateObserverTest extends ContainerBaseTest {

    private static final Logger LOG = LoggerFactory.getLogger(JobStateObserverTest.class);

    @Test
    public void observeJobState() throws Exception {
        ByteKitUtil.interceptWithLog(StreamExecutionEnvironment.class, "getStreamGraph");
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        executeJobAsync(() -> {
            try {
                JobWithState.main(new String[0]);
            } catch (Exception e) {
                LOG.error("Error execute JobWithState", e);
            }
        }, jobStatus -> {
            if (jobStatus.equals(JobStatus.RUNNING)) {
                TimerUtil.timeout(
                    () -> TroubleMaker.makeTrouble(new RuntimeException("interrupted by test")),
                    10_000);
            } else if (jobStatus.isTerminalState()) {
                result.complete(true);
            }
        });
        result.get();
    }

}
