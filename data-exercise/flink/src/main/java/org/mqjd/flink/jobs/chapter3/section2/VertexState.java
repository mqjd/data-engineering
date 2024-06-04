package org.mqjd.flink.jobs.chapter3.section2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.flink.api.common.state.StateDescriptor;

public class VertexState {

    private final Map<StateDescriptor<?, ?>, List<?>> states = new HashMap<>();
    private final Map<String, StateDescriptor<?, ?>> descriptors = new HashMap<>();
    private final VertexDescription vertex;

    public VertexState(VertexDescription vertex) {
        this.vertex = vertex;
    }

    public void addState(StateDescriptor<?, ?> stateDescriptor, List<?> state) {
        states.put(stateDescriptor, state);
        descriptors.put(stateDescriptor.getName(), stateDescriptor);
    }

    public VertexDescription getVertex() {
        return vertex;
    }

    public <T> List<T> getState(String name) {
        if (!descriptors.containsKey(name)) {
            return null;
        }
        return getState(descriptors.get(name));
    }

    public <T> List<T> getState(StateDescriptor<?, ?> state) {
        // noinspection unchecked
        return (List<T>) states.get(state);
    }
}
