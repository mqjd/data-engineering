package org.mqjd.flink.jobs.chapter3.section1.source;

import org.apache.flink.util.SplittableIterator;

public class CustomSplittableIterator extends SplittableIterator<Long> {

    private final long messageCount;
    private final int numPartitions;
    private long current;

    CustomSplittableIterator(long messageCount, int numPartitions, int currentPartition) {
        this.messageCount = messageCount;
        this.numPartitions = numPartitions;
        this.current = currentPartition - numPartitions;
    }

    public long getMessageCount() {
        return messageCount;
    }

    @Override
    public CustomSplittableIterator[] split(int numPartitions) {
        if (numPartitions < 1) {
            throw new IllegalArgumentException("The number of partitions must be at least 1.");
        }
        CustomSplittableIterator[] splits = new CustomSplittableIterator[numPartitions];
        for (int i = 0; i < numPartitions; i++) {
            splits[i] = new CustomSplittableIterator(messageCount, numPartitions, i);
        }
        return splits;
    }

    @Override
    public int getMaximumNumberOfSplits() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean hasNext() {
        return messageCount == 0 || current + numPartitions <= messageCount;
    }

    @Override
    public Long next() {
        current += numPartitions;
        return current;
    }
}
