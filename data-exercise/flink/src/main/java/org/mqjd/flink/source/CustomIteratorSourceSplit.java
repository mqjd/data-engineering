package org.mqjd.flink.source;

import java.util.Objects;
import org.apache.flink.api.connector.source.lib.util.IteratorSourceSplit;

public class CustomIteratorSourceSplit implements
    IteratorSourceSplit<Long, CustomSplittableIterator> {

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

    @Override
    public String toString() {
        return STR."CustomIteratorSourceSplit{messageCount=\{messageCount}, current=\{current}, splitId=\{splitId}, numSplits=\{numSplits}\{'}'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CustomIteratorSourceSplit that = (CustomIteratorSourceSplit) o;
        return messageCount == that.messageCount && current == that.current
            && splitId == that.splitId && numSplits == that.numSplits;
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageCount, current, splitId, numSplits);
    }
}
