package org.mqjd.flink.jobs.chapter3.section2;

import java.util.List;

import org.apache.flink.api.common.state.StateDescriptor;
import org.apache.flink.core.io.SimpleVersionedSerializer;
import org.apache.flink.runtime.OperatorIDPair;

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

    public SimpleVersionedSerializer<?> getCheckpointSerializer() {
        return checkpointSerializer;
    }

    public void setCheckpointSerializer(SimpleVersionedSerializer<?> checkpointSerializer) {
        this.checkpointSerializer = checkpointSerializer;
    }

    public String getName() {
        return name;
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
