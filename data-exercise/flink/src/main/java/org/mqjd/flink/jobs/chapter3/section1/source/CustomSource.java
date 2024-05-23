package org.mqjd.flink.jobs.chapter3.section1.source;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.connector.source.Boundedness;
import org.apache.flink.api.connector.source.Source;
import org.apache.flink.api.connector.source.SourceReader;
import org.apache.flink.api.connector.source.SourceReaderContext;
import org.apache.flink.api.connector.source.SplitEnumerator;
import org.apache.flink.api.connector.source.SplitEnumeratorContext;
import org.apache.flink.api.connector.source.lib.util.IteratorSourceEnumerator;
import org.apache.flink.api.connector.source.lib.util.IteratorSourceReader;
import org.apache.flink.api.connector.source.util.ratelimit.GuavaRateLimiter;
import org.apache.flink.api.connector.source.util.ratelimit.RateLimitedSourceReader;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.apache.flink.core.io.SimpleVersionedSerializer;

public class CustomSource implements
    Source<Long, CustomIteratorSourceSplit, Collection<CustomIteratorSourceSplit>>,
    ResultTypeQueryable<Long> {

    private final long messageCount;

    public CustomSource(long count) {
        messageCount = count < 0 ? 0 : count;
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
        return new RateLimitedSourceReader<>(new IteratorSourceReader<>(readerContext),
            new GuavaRateLimiter(1));
    }

    @Override
    public SplitEnumerator<CustomIteratorSourceSplit, Collection<CustomIteratorSourceSplit>> createEnumerator(
        SplitEnumeratorContext<CustomIteratorSourceSplit> enumContext) {
        int numSplits = enumContext.currentParallelism();
        final List<CustomIteratorSourceSplit> splits = new ArrayList<>(numSplits);
        for (int i = 0; i < numSplits; i++) {
            splits.add(new CustomIteratorSourceSplit(messageCount, numSplits, i + 1));
        }
        return new IteratorSourceEnumerator<>(enumContext, splits);
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
