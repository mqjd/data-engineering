package org.mqjd.flink.jobs.chapter3.section2.core;

import java.util.Map;
import java.util.concurrent.Future;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.TaskInfo;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.Path;
import org.apache.flink.runtime.accumulators.AccumulatorRegistry;
import org.apache.flink.runtime.broadcast.BroadcastVariableManager;
import org.apache.flink.runtime.checkpoint.CheckpointException;
import org.apache.flink.runtime.checkpoint.CheckpointMetrics;
import org.apache.flink.runtime.checkpoint.PrioritizedOperatorSubtaskState;
import org.apache.flink.runtime.checkpoint.TaskStateSnapshot;
import org.apache.flink.runtime.checkpoint.channel.ChannelStateWriteRequestExecutorFactory;
import org.apache.flink.runtime.execution.Environment;
import org.apache.flink.runtime.executiongraph.ExecutionAttemptID;
import org.apache.flink.runtime.externalresource.ExternalResourceInfoProvider;
import org.apache.flink.runtime.io.disk.iomanager.IOManager;
import org.apache.flink.runtime.io.network.TaskEventDispatcher;
import org.apache.flink.runtime.io.network.api.writer.ResultPartitionWriter;
import org.apache.flink.runtime.io.network.partition.consumer.IndexedInputGate;
import org.apache.flink.runtime.jobgraph.JobVertexID;
import org.apache.flink.runtime.jobgraph.tasks.InputSplitProvider;
import org.apache.flink.runtime.jobgraph.tasks.TaskOperatorEventGateway;
import org.apache.flink.runtime.memory.MemoryManager;
import org.apache.flink.runtime.memory.SharedResources;
import org.apache.flink.runtime.metrics.groups.TaskMetricGroup;
import org.apache.flink.runtime.query.TaskKvStateRegistry;
import org.apache.flink.runtime.state.TaskStateManager;
import org.apache.flink.runtime.taskexecutor.GlobalAggregateManager;
import org.apache.flink.runtime.taskmanager.TaskManagerActions;
import org.apache.flink.runtime.taskmanager.TaskManagerRuntimeInfo;
import org.apache.flink.util.SimpleUserCodeClassLoader;
import org.apache.flink.util.UserCodeClassLoader;

public class StateEnvironment implements Environment {

    @Override
    public ExecutionConfig getExecutionConfig() {
        return new ExecutionConfig();
    }

    @Override
    public JobID getJobID() {
        return null;
    }

    @Override
    public JobVertexID getJobVertexId() {
        return null;
    }

    @Override
    public ExecutionAttemptID getExecutionId() {
        return null;
    }

    @Override
    public Configuration getTaskConfiguration() {
        return null;
    }

    @Override
    public TaskManagerRuntimeInfo getTaskManagerInfo() {
        return null;
    }

    @Override
    public TaskMetricGroup getMetricGroup() {
        return null;
    }

    @Override
    public Configuration getJobConfiguration() {
        return null;
    }

    @Override
    public TaskInfo getTaskInfo() {
        return null;
    }

    @Override
    public InputSplitProvider getInputSplitProvider() {
        return null;
    }

    @Override
    public TaskOperatorEventGateway getOperatorCoordinatorEventGateway() {
        return null;
    }

    @Override
    public IOManager getIOManager() {
        return null;
    }

    @Override
    public MemoryManager getMemoryManager() {
        return null;
    }

    @Override
    public SharedResources getSharedResources() {
        return null;
    }

    @Override
    public UserCodeClassLoader getUserCodeClassLoader() {
        return SimpleUserCodeClassLoader.create(this.getClass().getClassLoader());
    }

    @Override
    public Map<String, Future<Path>> getDistributedCacheEntries() {
        return Map.of();
    }

    @Override
    public BroadcastVariableManager getBroadcastVariableManager() {
        return null;
    }

    @Override
    public TaskStateManager getTaskStateManager() {
        return new SavepointTaskStateManager(PrioritizedOperatorSubtaskState.emptyNotRestored());
    }

    @Override
    public GlobalAggregateManager getGlobalAggregateManager() {
        return null;
    }

    @Override
    public ExternalResourceInfoProvider getExternalResourceInfoProvider() {
        return null;
    }

    @Override
    public AccumulatorRegistry getAccumulatorRegistry() {
        return null;
    }

    @Override
    public TaskKvStateRegistry getTaskKvStateRegistry() {
        return null;
    }

    @Override
    public void acknowledgeCheckpoint(long checkpointId, CheckpointMetrics checkpointMetrics) {

    }

    @Override
    public void acknowledgeCheckpoint(long checkpointId, CheckpointMetrics checkpointMetrics,
        TaskStateSnapshot subtaskState) {

    }

    @Override
    public void declineCheckpoint(long checkpointId, CheckpointException checkpointException) {

    }

    @Override
    public void failExternally(Throwable cause) {

    }

    @Override
    public ResultPartitionWriter getWriter(int index) {
        return null;
    }

    @Override
    public ResultPartitionWriter[] getAllWriters() {
        return new ResultPartitionWriter[0];
    }

    @Override
    public IndexedInputGate getInputGate(int index) {
        return null;
    }

    @Override
    public IndexedInputGate[] getAllInputGates() {
        return new IndexedInputGate[0];
    }

    @Override
    public TaskEventDispatcher getTaskEventDispatcher() {
        return null;
    }

    @Override
    public TaskManagerActions getTaskManagerActions() {
        return null;
    }

    @Override
    public ChannelStateWriteRequestExecutorFactory getChannelStateExecutorFactory() {
        return null;
    }
}
