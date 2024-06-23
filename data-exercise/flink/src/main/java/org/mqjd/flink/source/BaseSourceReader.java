package org.mqjd.flink.source;

import static org.apache.flink.util.Preconditions.checkNotNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import org.apache.flink.api.connector.source.ReaderOutput;
import org.apache.flink.api.connector.source.SourceReader;
import org.apache.flink.api.connector.source.SourceReaderContext;
import org.apache.flink.api.connector.source.lib.util.IteratorSourceReaderBase;
import org.apache.flink.api.connector.source.lib.util.IteratorSourceSplit;
import org.apache.flink.core.io.InputStatus;


/**
 * @see IteratorSourceReaderBase
 */
public abstract class BaseSourceReader<
    E, O, IterT extends Iterator<E>, SplitT extends IteratorSourceSplit<E, IterT>>
    implements SourceReader<O, SplitT> {

    /**
     * The context for this reader, to communicate with the enumerator.
     */
    private final SourceReaderContext context;

    /**
     * The availability future. This reader is available as soon as a split is assigned.
     */
    private CompletableFuture<Void> availability;

    /**
     * The iterator producing data. Non-null after a split has been assigned. This field is null or
     * non-null always together with the {@link #currentSplit} field.
     */
    @Nullable
    IterT iterator;

    /**
     * The split whose data we return. Non-null after a split has been assigned. This field is null
     * or non-null always together with the {@link #iterator} field.
     */
    @Nullable
    SplitT currentSplit;

    /**
     * The remaining splits that were assigned but not yet processed.
     */
    final Queue<SplitT> remainingSplits;

    private boolean noMoreSplits;

    public BaseSourceReader(SourceReaderContext context) {
        this.context = checkNotNull(context);
        this.availability = new CompletableFuture<>();
        this.remainingSplits = new ArrayDeque<>();
    }

    // ------------------------------------------------------------------------

    @Override
    public void start() {
        // request a split if we don't have one
        if (remainingSplits.isEmpty()) {
            context.sendSplitRequest();
        }
        start(context);
    }

    protected void start(SourceReaderContext context) {
    }

    @Override
    public InputStatus pollNext(ReaderOutput<O> output) {
        if (iterator != null) {
            if (iterator.hasNext()) {
                output.collect(convert(iterator.next()));
                return InputStatus.MORE_AVAILABLE;
            } else {
                finishSplit();
            }
        }
        final InputStatus inputStatus = tryMoveToNextSplit();
        if (inputStatus == InputStatus.MORE_AVAILABLE) {
            output.collect(convert(iterator.next()));
        }
        return inputStatus;
    }

    protected abstract O convert(E value);

    private void finishSplit() {
        iterator = null;
        currentSplit = null;

        // request another split if no other is left
        // we do this only here in the finishSplit part to avoid requesting a split
        // whenever the reader is polled and doesn't currently have a split
        if (remainingSplits.isEmpty() && !noMoreSplits) {
            context.sendSplitRequest();
        }
    }

    protected InputStatus tryMoveToNextSplit() {
        currentSplit = remainingSplits.poll();
        if (currentSplit != null) {
            iterator = currentSplit.getIterator();
            return InputStatus.MORE_AVAILABLE;
        } else if (noMoreSplits) {
            return InputStatus.END_OF_INPUT;
        } else {
            // ensure we are not called in a loop by resetting the availability future
            if (availability.isDone()) {
                availability = new CompletableFuture<>();
            }

            return InputStatus.NOTHING_AVAILABLE;
        }
    }

    @Override
    public CompletableFuture<Void> isAvailable() {
        return availability;
    }

    @Override
    public void addSplits(List<SplitT> splits) {
        remainingSplits.addAll(splits);
        // set availability so that pollNext is actually called
        availability.complete(null);
    }

    @Override
    public void notifyNoMoreSplits() {
        noMoreSplits = true;
        // set availability so that pollNext is actually called
        availability.complete(null);
    }

    @Override
    public List<SplitT> snapshotState(long checkpointId) {
        if (currentSplit == null && remainingSplits.isEmpty()) {
            return Collections.emptyList();
        }

        final ArrayList<SplitT> allSplits = new ArrayList<>(1 + remainingSplits.size());
        if (iterator != null && iterator.hasNext()) {
            assert currentSplit != null;

            @SuppressWarnings("unchecked") final SplitT inProgressSplit =
                (SplitT) currentSplit.getUpdatedSplitForIterator(iterator);
            allSplits.add(inProgressSplit);
        }
        allSplits.addAll(remainingSplits);
        return allSplits;
    }

    @Override
    public void close() throws Exception {
    }
}
