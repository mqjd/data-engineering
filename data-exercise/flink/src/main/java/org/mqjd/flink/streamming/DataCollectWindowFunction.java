package org.mqjd.flink.streamming;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.util.Collector;
import org.apache.flink.util.IterableUtils;

public class DataCollectWindowFunction<T> implements AllWindowFunction<T, List<T>, GlobalWindow> {

    private static final long serialVersionUID = 7696917697534289921L;

    @Override
    public void apply(GlobalWindow window, Iterable<T> values, Collector<List<T>> out) {
        out.collect(IterableUtils.toStream(values).collect(Collectors.toList()));
    }
}
