package org.mqjd.flink.streamming;

import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.state.ReducingState;
import org.apache.flink.api.common.state.ReducingStateDescriptor;
import org.apache.flink.api.common.typeutils.base.LongSerializer;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.Window;


public class CountWithMaxTimeTrigger<W extends Window> extends Trigger<Object, W> {

    private static final long serialVersionUID = 1L;

    private final long maxCount;

    private final long maxWaitingTime;

    private final ReducingStateDescriptor<Long> stateDesc =
        new ReducingStateDescriptor<>("count", new Sum(), LongSerializer.INSTANCE);

    private final ReducingStateDescriptor<Long> fireTimeStateDesc =
        new ReducingStateDescriptor<>("fire-time", new Min(), LongSerializer.INSTANCE);

    private CountWithMaxTimeTrigger(long maxCount, long maxWaitingTime) {
        this.maxCount = maxCount;
        this.maxWaitingTime = maxWaitingTime;
    }

    public static <W extends Window> CountWithMaxTimeTrigger<W> of(long maxCount,
        long maxWaitingTime) {
        return new CountWithMaxTimeTrigger<>(maxCount, maxWaitingTime);
    }

    @Override
    public TriggerResult onElement(Object element, long timestamp, W window, TriggerContext ctx)
        throws Exception {
        ReducingState<Long> count = ctx.getPartitionedState(stateDesc);
        ReducingState<Long> fireTimestamp = ctx.getPartitionedState(fireTimeStateDesc);

        count.add(1L);
        if (count.get() >= maxCount) {
            count.clear();
            fireTimestamp.clear();
            return TriggerResult.FIRE;
        }

        if (fireTimestamp.get() == null) {
            long triggerTime = System.currentTimeMillis() + maxWaitingTime;
            ctx.registerProcessingTimeTimer(triggerTime);
            fireTimestamp.add(triggerTime);
        }

        return TriggerResult.CONTINUE;
    }

    @Override
    public TriggerResult onEventTime(long time, W window, TriggerContext ctx) {
        return TriggerResult.CONTINUE;
    }

    @Override
    public TriggerResult onProcessingTime(long time, W window, TriggerContext ctx)
        throws Exception {
        ReducingState<Long> fireTimestamp = ctx.getPartitionedState(fireTimeStateDesc);

        if (fireTimestamp.get() != null && fireTimestamp.get().equals(time)) {
            ctx.getPartitionedState(stateDesc).clear();
            fireTimestamp.clear();
            return TriggerResult.FIRE;
        }

        return TriggerResult.CONTINUE;
    }

    @Override
    public void clear(W window, TriggerContext ctx) {
        ctx.getPartitionedState(stateDesc).clear();
    }

    @Override
    public boolean canMerge() {
        return true;
    }

    @Override
    public void onMerge(W window, OnMergeContext ctx) {
        ctx.mergePartitionedState(stateDesc);
    }


    private static class Sum implements ReduceFunction<Long> {

        private static final long serialVersionUID = 1L;

        @Override
        public Long reduce(Long value1, Long value2) {
            return value1 + value2;
        }

    }

    private static class Min implements ReduceFunction<Long> {

        private static final long serialVersionUID = 1L;

        @Override
        public Long reduce(Long value1, Long value2) {
            return Math.min(value1, value2);
        }
    }
}
