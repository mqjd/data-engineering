package org.mqjd.flink.source;

import static org.apache.flink.util.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.flink.api.connector.source.SplitEnumerator;
import org.apache.flink.api.connector.source.SplitEnumeratorContext;
import org.apache.flink.api.connector.source.SplitsAssignment;
import org.apache.flink.api.connector.source.lib.util.IteratorSourceSplit;

public class CustomSourceEnumerator<SplitT extends IteratorSourceSplit<?, ?>>
    implements SplitEnumerator<SplitT, Collection<SplitT>> {

    private final Map<Integer, Set<SplitT>> pendingPartitionSplitAssignment;
    private final SplitEnumeratorContext<SplitT> context;
    private final Collection<SplitT> allSplits;

    public CustomSourceEnumerator(SplitEnumeratorContext<SplitT> context, Collection<SplitT> splits) {
        this.allSplits = splits;
        this.pendingPartitionSplitAssignment = new HashMap<>();
        this.context = checkNotNull(context);
        addSplitChangeToPendingAssignments(splits);
    }

    @Override
    public void start() {

    }

    @Override
    public void close() {

    }

    @Override
    public void handleSplitRequest(int subtaskId, @Nullable String requesterHostname) {
    }

    @Override
    public void addReader(int subtaskId) {
        assignPendingSplits(Collections.singleton(subtaskId));
    }

    @Override
    public Collection<SplitT> snapshotState(long checkpointId) {
        return allSplits;
    }

    @Override
    public void addSplitsBack(List<SplitT> splits, int subtaskId) {
        addSplitChangeToPendingAssignments(splits);
        if (context.registeredReaders().containsKey(subtaskId)) {
            assignPendingSplits(Collections.singleton(subtaskId));
        }
    }

    private void assignPendingSplits(Set<Integer> pendingReaders) {
        for (int pendingReader : pendingReaders) {
            checkReaderRegistered(pendingReader);
            Set<SplitT> splits = pendingPartitionSplitAssignment.remove(pendingReader);
            if (splits != null) {
                splits.forEach(split -> context.assignSplits(new SplitsAssignment<>(split, pendingReader)));
            }
        }
    }

    private void checkReaderRegistered(int readerId) {
        if (!context.registeredReaders().containsKey(readerId)) {
            throw new IllegalStateException(
                String.format("Reader %d is not registered to source coordinator", readerId));
        }
    }

    private void addSplitChangeToPendingAssignments(Collection<SplitT> newPartitionSplits) {
        int numReaders = context.currentParallelism();
        for (SplitT split : newPartitionSplits) {
            int ownerReader = getSplitOwner(split, numReaders);
            pendingPartitionSplitAssignment.computeIfAbsent(ownerReader, v -> new HashSet<>()).add(split);
        }
    }

    int getSplitOwner(SplitT splitT, int numReaders) {
        return ((splitT.splitId().hashCode() * 31) & 0x7FFFFFFF) % numReaders;
    }
}
