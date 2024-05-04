package org.mqjd.flink.jobs.chapter1.section3.windowing;

import java.io.Serial;
import java.util.Iterator;

import org.apache.flink.streaming.api.windowing.evictors.Evictor;
import org.apache.flink.streaming.api.windowing.windows.Window;
import org.apache.flink.streaming.runtime.operators.windowing.TimestampedValue;

public class RangeEvictor<W extends Window> implements Evictor<Object, W> {
    @Serial
    private static final long serialVersionUID = 2685659698075882148L;

    @Override
    public void evictBefore(Iterable<TimestampedValue<Object>> iterable, int size, W w, EvictorContext evictorContext) {

    }

    @Override
    public void evictAfter(Iterable<TimestampedValue<Object>> elements, int size, W w, EvictorContext evictorContext) {
        int evictedCount = size;
        for (Iterator<TimestampedValue<Object>> iterator = elements.iterator(); iterator.hasNext();) {
            iterator.next();
            evictedCount--;
            if (evictedCount == 0) {
                break;
            } else {
                iterator.remove();
            }
        }
    }
}
