package org.mqjd.flink.source;

import org.apache.flink.util.SplittableIterator;

public class CustomSplittableIterator extends SplittableIterator<Long> {

    private static final long serialVersionUID = 1756116767452321974L;

    private final long messageCount;
    private final int numPartitions;
    private long current;

    CustomSplittableIterator(long messageCount, long current, int numPartitions) {
        this.messageCount = messageCount;
        this.numPartitions = numPartitions;
        this.current = current;
    }

    public long getCurrent() {
        return current;
    }

    public int getNumPartitions() {
        return numPartitions;
    }

    public long getMessageCount() {
        return messageCount;
    }

    @Override
    public CustomSplittableIterator[] split(int numPartitions) {
        if (numPartitions < 1) {
            throw new IllegalArgumentException("The number of partitions must be at least 1.");
        }

        if (numPartitions == 1) {
            return new CustomSplittableIterator[]{
                new CustomSplittableIterator(messageCount, current, numPartitions)};
        }

        CustomSplittableIterator[] splits = new CustomSplittableIterator[numPartitions];
        for (int i = 0; i < numPartitions; i++) {
            splits[i] = new CustomSplittableIterator(messageCount, i - numPartitions + 1,
                numPartitions);
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
