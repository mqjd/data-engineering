package org.mqjd.flink.jobs.chapter3.section2;

import java.util.ArrayList;
import java.util.List;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.StateDescriptor;
import org.apache.flink.core.io.SimpleVersionedSerializer;
import org.apache.flink.runtime.OperatorIDPair;
import org.apache.flink.runtime.state.OperatorStateBackend;
import org.apache.flink.streaming.api.operators.util.SimpleVersionedListState;

public class VertexDescription {

    private String name;
    private OperatorIDPair operatorID;
    private Class<?> operatorClass;
    private SimpleVersionedSerializer<?> checkpointSerializer;
    private List<StateDescriptor<?, ?>> stateDescriptors;

    public List<StateDescriptor<?, ?>> getStateDescriptors() {
        return stateDescriptors;
    }

    public void setStateDescriptors(List<StateDescriptor<?, ?>> stateDescriptors) {
        this.stateDescriptors = stateDescriptors;
    }

    public void addStateDescriptors(StateDescriptor<?, ?> stateDescriptor) {
        if (this.stateDescriptors == null) {
            this.stateDescriptors = new ArrayList<>();
        }
        this.stateDescriptors.add(stateDescriptor);
    }

    public SimpleVersionedSerializer<?> getCheckpointSerializer() {
        return checkpointSerializer;
    }

    public void setCheckpointSerializer(SimpleVersionedSerializer<?> checkpointSerializer) {
        this.checkpointSerializer = checkpointSerializer;
    }

    public String getName() {
        return name;
    }

    public VertexState readState(OperatorStateBackend stateBackend) throws Exception {
        VertexState vertexState = new VertexState(this);
        if (stateDescriptors == null || stateDescriptors.isEmpty()) {
            return vertexState;
        }

        for (StateDescriptor<?, ?> v : stateDescriptors) {
            List<Object> states = new ArrayList<>();
            if (v instanceof ListStateDescriptor<?> listStateDescriptor) {
                ListState<?> listState = stateBackend.getListState(listStateDescriptor);
                Iterable<?> iterable;
                if (checkpointSerializer != null) {
                    // noinspection unchecked
                    iterable = new SimpleVersionedListState<>((ListState<byte[]>) listState,
                        checkpointSerializer).get();
                } else {
                    iterable = listState.get();
                }
                for (Object object : iterable) {
                    states.add(object);
                }
            }
            vertexState.addState(v, states);
        }
        return vertexState;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OperatorIDPair getOperatorID() {
        return operatorID;
    }

    public void setOperatorID(OperatorIDPair operatorID) {
        this.operatorID = operatorID;
    }

    public Class<?> getOperatorClass() {
        return operatorClass;
    }

    public void setOperatorClass(Class<?> operatorClass) {
        this.operatorClass = operatorClass;
    }
}
