package org.mqjd.flink.jobs.chapter3.section2;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.flink.api.common.functions.Function;
import org.apache.flink.api.common.state.StateDescriptor;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.connector.sink2.Sink;
import org.apache.flink.api.connector.source.Source;
import org.apache.flink.core.io.SimpleVersionedSerializer;
import org.apache.flink.runtime.OperatorIDPair;
import org.apache.flink.state.api.OperatorIdentifier;
import org.apache.flink.streaming.api.graph.StreamNode;
import org.apache.flink.streaming.api.operators.SimpleUdfStreamOperatorFactory;
import org.apache.flink.streaming.api.operators.SourceOperatorFactory;
import org.apache.flink.streaming.api.operators.StreamOperator;
import org.apache.flink.streaming.api.operators.StreamOperatorFactory;
import org.apache.flink.streaming.api.operators.UdfStreamOperatorFactory;
import org.apache.flink.streaming.api.windowing.triggers.PurgingTrigger;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.runtime.operators.sink.SinkWriterOperatorFactory;
import org.apache.flink.streaming.runtime.operators.windowing.WindowOperator;
import org.mqjd.flink.jobs.chapter3.section2.core.States;
import org.mqjd.flink.util.ReflectionUtil;

public class VertexDescription {

    private final StreamNode streamNode;
    private final StreamOperatorFactory<?> operatorFactory;
    private final OperatorIDPair operatorIDPair;
    private Class<?> operatorClass;
    private SimpleVersionedSerializer<?> splitSerializer;
    private List<StateDescriptor<?, ?>> stateDescriptors;
    private TypeSerializer<?> keySerializer;
    private TypeSerializer<?> windowSerializer;

    public VertexDescription(StreamNode streamNode, OperatorIDPair operatorIDPair) {
        this.streamNode = streamNode;
        this.operatorFactory = streamNode.getOperatorFactory();
        this.operatorIDPair = operatorIDPair;
        this.init();
    }

    public Optional<StateDescriptor<?, ?>> findStateDescriptor(String name) {
        return stateDescriptors.stream().filter(v -> name.equals(v.getName())).findAny();
    }

    public StreamNode getStreamNode() {
        return streamNode;
    }

    private void init() {
        initSourceVertex();
        initOperatorVertex();
        initSinkVertex();
    }

    private void initSourceVertex() {
        if (!streamNode.getOperatorFactory().isStreamSource()) {
            return;
        }
        SourceOperatorFactory<?> sourceOperatorFactory = (SourceOperatorFactory<?>) operatorFactory;
        Source<?, ?, ?> source = Objects.requireNonNull(
            ReflectionUtil.read(sourceOperatorFactory, "source"));
        this.operatorClass = source.getClass();
        this.splitSerializer = source.getSplitSerializer();
        addStateDescriptor(States.SPLITS_STATE_DESC);
    }

    private void initOperatorVertex() {
        initUdfStreamOperatorVertex();
    }

    /**
     * @see WindowOperator#keySerializer
     * @see WindowOperator#windowStateDescriptor
     * @see WindowOperator#trigger
     */
    private void initUdfStreamOperatorVertex() {
        if (!(operatorFactory instanceof SimpleUdfStreamOperatorFactory)) {
            return;
        }

        StreamOperator<?> operator = ((SimpleUdfStreamOperatorFactory<?>) operatorFactory).getOperator();
        Function function = ((UdfStreamOperatorFactory<?>) operatorFactory).getUserFunction();
        this.operatorClass = function.getClass();
        this.stateDescriptors = ReflectionUtil.findAll(function, StateDescriptor.class);

        if (!(operator instanceof WindowOperator)) {
            return;
        }
        this.windowSerializer = ReflectionUtil.read(operator, "windowSerializer");
        this.keySerializer = ReflectionUtil.read(operator, "keySerializer");
        addStateDescriptor(ReflectionUtil.read(operator, "windowStateDescriptor"));
        Trigger<?, ?> trigger = Objects.requireNonNull(ReflectionUtil.read(operator, "trigger"));
        this.addStateDescriptors(ReflectionUtil.findAll(trigger, StateDescriptor.class));
        if (trigger instanceof PurgingTrigger) {
            this.addStateDescriptors(
                ReflectionUtil.findAll(((PurgingTrigger<?, ?>) trigger).getNestedTrigger(),
                    StateDescriptor.class));
        }
    }

    private void initSinkVertex() {
        if (!(operatorFactory instanceof SinkWriterOperatorFactory)) {
            return;
        }
        Sink<?> sink = ((SinkWriterOperatorFactory<?, ?>) operatorFactory).getSink();
        this.operatorClass = sink.getClass();
    }

    public OperatorIdentifier getOperatorIdentifier() {
        return OperatorIdentifier.forUidHash(
            getOperatorID().getGeneratedOperatorID().toHexString());
    }

    public void addStateDescriptor(StateDescriptor<?, ?> stateDescriptor) {
        if (this.stateDescriptors == null) {
            this.stateDescriptors = new ArrayList<>();
        }
        this.stateDescriptors.add(stateDescriptor);
    }

    public void addStateDescriptors(List<StateDescriptor<?, ?>> stateDescriptors) {
        if (this.stateDescriptors == null) {
            this.stateDescriptors = new ArrayList<>();
        }
        this.stateDescriptors.addAll(stateDescriptors);
    }

    public SimpleVersionedSerializer<?> getSplitSerializer() {
        return splitSerializer;
    }

    public String getName() {
        return streamNode.getOperatorName();
    }

    public OperatorIDPair getOperatorID() {
        return operatorIDPair;
    }

    public TypeSerializer<?> getKeySerializer() {
        return keySerializer;
    }

    public Integer getParallelism() {
        return streamNode.getParallelism();
    }

    public Integer getMaxParallelism() {
        return ReflectionUtil.read(streamNode, "maxParallelism");
    }

    public TypeSerializer<?> getWindowSerializer() {
        return windowSerializer;
    }
}
