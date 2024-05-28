package org.mqjd.flink.jobs.chapter3.section1.source;

import org.apache.flink.api.connector.source.lib.util.IteratorSourceSplit;

public class CustomIteratorSourceSplit implements IteratorSourceSplit<Long, CustomSplittableIterator> {

    private final long messageCount;
    private final long current;
    private final int splitId;
    private final int numSplits;

    public CustomIteratorSourceSplit(long messageCount, long current, int numSplits, int splitId) {
        this.messageCount = messageCount;
        this.current = current;
        this.numSplits = numSplits;
        this.splitId = splitId;
    }

    public long getCurrent() {
        return current;
    }

    public long getMessageCount() {
        return messageCount;
    }

    public int getSplitId() {
        return splitId;
    }

    public int getNumSplits() {
        return numSplits;
    }

    @Override
    public CustomSplittableIterator getIterator() {
        return new CustomSplittableIterator(messageCount, current, numSplits);
    }

    @Override
    public IteratorSourceSplit<Long, CustomSplittableIterator> getUpdatedSplitForIterator(
        CustomSplittableIterator iterator) {
        return new CustomIteratorSourceSplit(iterator.getMessageCount(), iterator.getCurrent(),
            iterator.getNumPartitions(), splitId);
    }

    @Override
    public String splitId() {
        return String.valueOf(splitId);
    }
}
