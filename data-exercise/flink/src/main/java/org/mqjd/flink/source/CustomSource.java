package org.mqjd.flink.source;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.connector.source.Boundedness;
import org.apache.flink.api.connector.source.Source;
import org.apache.flink.api.connector.source.SourceReader;
import org.apache.flink.api.connector.source.SourceReaderContext;
import org.apache.flink.api.connector.source.SplitEnumerator;
import org.apache.flink.api.connector.source.SplitEnumeratorContext;
import org.apache.flink.api.connector.source.lib.util.IteratorSourceEnumerator;
import org.apache.flink.api.connector.source.util.ratelimit.GuavaRateLimiter;
import org.apache.flink.api.connector.source.util.ratelimit.RateLimitedSourceReader;
import org.apache.flink.api.connector.source.util.ratelimit.RateLimiterStrategy;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.apache.flink.core.io.SimpleVersionedSerializer;

public class CustomSource implements
    Source<Long, CustomIteratorSourceSplit, Collection<CustomIteratorSourceSplit>>,
    ResultTypeQueryable<Long> {

    @Serial
    private static final long serialVersionUID = -1962636063339778994L;
    private final long messageCount;
    private final RateLimiterStrategy rateLimiterStrategy;
    private final Integer maxParallelism;

    public CustomSource(long count) {
        this(count, null);
    }

    public CustomSource(long count, Integer maxParallelism) {
        this.messageCount = count < 0 ? 0 : count;
        this.rateLimiterStrategy = _ -> new GuavaRateLimiter(4);
        this.maxParallelism = maxParallelism;
    }

    public CustomSource(double rate, Integer maxParallelism) {
        this.messageCount = 0;
        this.rateLimiterStrategy = _ -> new GuavaRateLimiter(rate);
        this.maxParallelism = maxParallelism;
    }

    public CustomSource() {
        this(0);
    }

    @Override
    public TypeInformation<Long> getProducedType() {
        return Types.LONG;
    }

    @Override
    public Boundedness getBoundedness() {
        return messageCount == 0 ? Boundedness.BOUNDED : Boundedness.CONTINUOUS_UNBOUNDED;
    }

    @Override
    public SourceReader<Long, CustomIteratorSourceSplit> createReader(
        SourceReaderContext readerContext) {
        int numSplits = readerContext.currentParallelism();
        return new RateLimitedSourceReader<>(new CustomSourceReader<>(readerContext, v -> v),
            rateLimiterStrategy.createRateLimiter(
                Optional.ofNullable(maxParallelism).orElse(numSplits)));
    }

    @Override
    public SplitEnumerator<CustomIteratorSourceSplit, Collection<CustomIteratorSourceSplit>> createEnumerator(
        SplitEnumeratorContext<CustomIteratorSourceSplit> enumContext) {
        int numSplits = enumContext.currentParallelism();
        final List<CustomIteratorSourceSplit> splits = split(messageCount,
            Optional.ofNullable(maxParallelism).orElse(numSplits));
        return new CustomSourceEnumerator<>(enumContext, splits);
    }

    private List<CustomIteratorSourceSplit> split(long count, int numSplits) {
        final CustomSplittableIterator[] subSequences = new CustomSplittableIterator(count, 0,
            numSplits).split(numSplits);
        final List<CustomIteratorSourceSplit> splits = new ArrayList<>(subSequences.length);
        int splitId = 0;
        for (CustomSplittableIterator seq : subSequences) {
            if (seq.hasNext()) {
                splits.add(new CustomIteratorSourceSplit(seq.getMessageCount(), seq.getCurrent(),
                    numSplits, splitId++));
            }
        }
        return splits;
    }

    @Override
    public SplitEnumerator<CustomIteratorSourceSplit, Collection<CustomIteratorSourceSplit>> restoreEnumerator(
        SplitEnumeratorContext<CustomIteratorSourceSplit> enumContext,
        Collection<CustomIteratorSourceSplit> checkpoint) {
        return new IteratorSourceEnumerator<>(enumContext, checkpoint);
    }

    @Override
    public SimpleVersionedSerializer<CustomIteratorSourceSplit> getSplitSerializer() {
        return CustomIteratorSourceSplitSerializer.singleSerializer();
    }

    @Override
    public SimpleVersionedSerializer<Collection<CustomIteratorSourceSplit>> getEnumeratorCheckpointSerializer() {
        return CustomIteratorSourceSplitSerializer.collectionSerializer();
    }

}
