package org.mqjd.flink.jobs.chapter3.section1.function;

import java.util.List;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;

public class Counter<T> implements MapFunction<T, T>, CheckpointedFunction {

    private final ListStateDescriptor<Long> stateDescriptor = new ListStateDescriptor<>("counter",
        Long.class);

    private transient FunctionInitializationContext context;

    @Override
    public T map(T value) throws Exception {
        ListState<Long> listState = context.getOperatorStateStore().getListState(stateDescriptor);
        Long count = increase(listState.get().iterator().next());
        listState.update(List.of(count));
        return value;
    }

    private static long increase(Long value) {
        if (value == null) {
            return 1;
        }
        return value + 1;
    }

    @Override
    public void snapshotState(FunctionSnapshotContext context) throws Exception {
    }

    @Override
    public void initializeState(FunctionInitializationContext context) throws Exception {
        this.context = context;
        context.getOperatorStateStore().getListState(stateDescriptor).add(0L);

    }
}
