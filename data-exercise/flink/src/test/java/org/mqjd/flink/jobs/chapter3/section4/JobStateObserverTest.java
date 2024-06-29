package org.mqjd.flink.jobs.chapter3.section4;

import java.util.concurrent.CompletableFuture;
import org.apache.flink.api.common.JobStatus;
import org.apache.flink.client.deployment.executors.PipelineExecutorUtils;
import org.apache.flink.runtime.executiongraph.Execution;
import org.apache.flink.runtime.jobmaster.JobMaster;
import org.apache.flink.runtime.jobmaster.JobMasterServiceLeadershipRunner;
import org.apache.flink.runtime.jobmaster.RpcTaskManagerGateway;
import org.apache.flink.runtime.minicluster.MiniCluster;
import org.apache.flink.runtime.scheduler.DefaultExecutionDeployer;
import org.apache.flink.runtime.scheduler.DefaultScheduler;
import org.apache.flink.runtime.taskmanager.Task;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.mqjd.flink.containers.ContainerBaseTest;
import org.mqjd.flink.util.TimerUtil;
import org.mqjd.flink.utils.ByteKitUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobStateObserverTest extends ContainerBaseTest {

    private static final Logger LOG = LoggerFactory.getLogger(JobStateObserverTest.class);

    @ClassRule
    public static ExternalResource resource = newLogConfiguration(
        "/jobs/chapter3/section4/log4j2.properties");

    @Test
    public void observeJobState() throws Exception {
        interceptWithLog();
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        executeJobAsync(() -> {
            try {
                String[] params = new String[0];
                JobStateObserver.main(params);
            } catch (Exception e) {
                LOG.error("Error execute JobWithState", e);
            }
        }, (client, jobStatus) -> {
            if (jobStatus.equals(JobStatus.RUNNING)) {
                TimerUtil.timeout(client::cancel, 1_000);
            } else if (jobStatus.isTerminalState()) {
                result.complete(true);
            }
        });
        result.get();
    }

    private void interceptWithLog() throws Exception {
        ByteKitUtil.interceptWithLog(StreamExecutionEnvironment.class,
            "StreamGraph getStreamGraph()");
        ByteKitUtil.interceptWithLog(PipelineExecutorUtils.class,
            "JobGraph getJobGraph(Pipeline, Configuration, ClassLoader)");
        ByteKitUtil.interceptWithLog(MiniCluster.class,
            "CompletableFuture<JobSubmissionResult> submitJob(JobGraph)");
        ByteKitUtil.interceptWithLog(JobMasterServiceLeadershipRunner.class, "void start()");
        ByteKitUtil.interceptWithLog(JobMaster.class, "void onStart()", "void startJobExecution()");
        ByteKitUtil.interceptWithLog(DefaultScheduler.class, "void startSchedulingInternal()",
            "void allocateSlotsAndDeploy(List<ExecutionVertexID>)");
        ByteKitUtil.interceptWithLog(DefaultExecutionDeployer.class,
            "void allocateSlotsAndDeploy(List<Execution>,Map<ExecutionVertexID,ExecutionVertexVersion>)");
        ByteKitUtil.interceptWithLog(Execution.class, "void deploy()");
        ByteKitUtil.interceptWithLog(RpcTaskManagerGateway.class,
            "CompletableFuture<Acknowledge> submitTask(TaskDeploymentDescriptor, Time)");
        ByteKitUtil.interceptWithLog(Task.class, "void doRun()",
            "boolean transitionState(ExecutionState, ExecutionState)");
    }

}
