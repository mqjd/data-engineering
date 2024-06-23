package org.mqjd.flink.source;

import java.util.Iterator;
import org.apache.flink.api.connector.source.ReaderOutput;
import org.apache.flink.api.connector.source.SourceReaderContext;
import org.apache.flink.api.connector.source.lib.util.IteratorSourceSplit;
import org.apache.flink.connector.datagen.source.GeneratorFunction;
import org.apache.flink.core.io.InputStatus;

public class CustomSourceReader<E, O, IterT extends Iterator<E>, SplitT extends IteratorSourceSplit<E, IterT>> extends
    BaseSourceReader<E, O, IterT, SplitT> {

    private final GeneratorFunction<E, O> generatorFunction;

    public CustomSourceReader(SourceReaderContext context,
        GeneratorFunction<E, O> generatorFunction) {
        super(context);
        this.generatorFunction = generatorFunction;
    }

    @Override
    public InputStatus pollNext(ReaderOutput<O> output) {
        InputStatus inputStatus = super.pollNext(output);
        if (InputStatus.MORE_AVAILABLE == inputStatus && currentSplit != null) {
            //noinspection unchecked
            final SplitT inProgressSplit = (SplitT) currentSplit.getUpdatedSplitForIterator(
                iterator);
            remainingSplits.add(inProgressSplit);
            tryMoveToNextSplit();
        }
        return inputStatus;
    }

    @Override
    protected O convert(E value) {
        try {
            return generatorFunction.map(value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
