/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mqjd.flink.jobs.chapter3.section2.core;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.checkpoint.CheckpointMetaData;
import org.apache.flink.runtime.checkpoint.CheckpointMetrics;
import org.apache.flink.runtime.checkpoint.InflightDataRescalingDescriptor;
import org.apache.flink.runtime.checkpoint.PrioritizedOperatorSubtaskState;
import org.apache.flink.runtime.checkpoint.TaskStateSnapshot;
import org.apache.flink.runtime.checkpoint.channel.SequentialChannelStateReader;
import org.apache.flink.runtime.jobgraph.OperatorID;
import org.apache.flink.runtime.state.LocalRecoveryConfig;
import org.apache.flink.runtime.state.TaskStateManager;
import org.apache.flink.runtime.state.changelog.ChangelogStateHandle;
import org.apache.flink.runtime.state.changelog.StateChangelogStorage;
import org.apache.flink.runtime.state.changelog.StateChangelogStorageView;
import org.apache.flink.util.Preconditions;

/**
 * @see org.apache.flink.state.api.runtime.SavepointTaskStateManager
 */
public final class SavepointTaskStateManager implements TaskStateManager {
    private static final String MSG = "This method should never be called";

    @Nonnull private final PrioritizedOperatorSubtaskState prioritizedOperatorSubtaskState;

    public SavepointTaskStateManager(PrioritizedOperatorSubtaskState prioritizedOperatorSubtaskState) {
        Preconditions.checkNotNull(
                prioritizedOperatorSubtaskState, "Operator subtask state must not be null");
        this.prioritizedOperatorSubtaskState = prioritizedOperatorSubtaskState;
    }

    @Override
    public void reportTaskStateSnapshots(
            @Nonnull CheckpointMetaData checkpointMetaData,
            @Nonnull CheckpointMetrics checkpointMetrics,
            @Nullable TaskStateSnapshot acknowledgedState,
            @Nullable TaskStateSnapshot localState) {}

    @Override
    public void reportIncompleteTaskStateSnapshots(
            CheckpointMetaData checkpointMetaData, CheckpointMetrics checkpointMetrics) {}

    @Override
    public boolean isTaskDeployedAsFinished() {
        return false;
    }

    @Override
    public Optional<Long> getRestoreCheckpointId() {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public PrioritizedOperatorSubtaskState prioritizedOperatorState(OperatorID operatorID) {
        return prioritizedOperatorSubtaskState;
    }

    @Nonnull
    @Override
    public LocalRecoveryConfig createLocalRecoveryConfig() {
        return new LocalRecoveryConfig(null);
    }

    @Override
    public SequentialChannelStateReader getSequentialChannelStateReader() {
        return SequentialChannelStateReader.NO_OP;
    }

    @Override
    public InflightDataRescalingDescriptor getInputRescalingDescriptor() {
        return InflightDataRescalingDescriptor.NO_RESCALE;
    }

    @Override
    public InflightDataRescalingDescriptor getOutputRescalingDescriptor() {
        return InflightDataRescalingDescriptor.NO_RESCALE;
    }

    @Nullable
    @Override
    public StateChangelogStorage<?> getStateChangelogStorage() {
        return null;
    }

    @Nullable
    @Override
    public StateChangelogStorageView<?> getStateChangelogStorageView(
            Configuration configuration, ChangelogStateHandle changelogStateHandle) {
        return null;
    }

    @Override
    public void notifyCheckpointComplete(long checkpointId) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public void notifyCheckpointAborted(long checkpointId) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public void close() {}
}