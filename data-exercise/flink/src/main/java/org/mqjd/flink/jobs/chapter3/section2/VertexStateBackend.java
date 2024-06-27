package org.mqjd.flink.jobs.chapter3.section2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.StateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;

public class VertexStateBackend {

    private final VertexDescription vertex;
    private final Map<Integer, SubtaskStateBackend> stateBackend = new HashMap<>();
    private final Set<String> registeredOperatorStateNames = new HashSet<>();
    private final Set<String> registeredStates = new HashSet<>();

    public VertexStateBackend(VertexDescription vertex) {
        this.vertex = vertex;
    }

    public void addSubtaskStateBackend(SubtaskStateBackend stateBackend) {
        this.stateBackend.put(stateBackend.getSubtaskIndex(), stateBackend);
        this.registeredOperatorStateNames.addAll(stateBackend.getRegisteredOperatorStateNames());
        Set<String> states = stateBackend.getRegisteredStates();
        this.registeredStates.addAll(states);
    }

    public <T> Map<Integer, List<T>> readOperatorState(String name) {
        Optional<StateDescriptor<?, ?>> stateDescriptor = vertex.findStateDescriptor(name);
        if (stateDescriptor.isEmpty() || !registeredOperatorStateNames.contains(name)) {
            return null;
        }
        StateDescriptor<?, ?> descriptor = stateDescriptor.get();
        if (!(descriptor instanceof ListStateDescriptor<?>)) {
            return null;
        }

        return stateBackend.entrySet().stream().collect(Collectors.toMap(Entry::getKey, v -> {
            SubtaskStateBackend subtaskStateBackend = v.getValue();
            return subtaskStateBackend.readOperatorState((ListStateDescriptor<?>) descriptor);
        }));

    }

    public VertexDescription getVertex() {
        return vertex;
    }

    public <T> Map<Integer, Map<Tuple2<?, ?>, T>> readKeyedState(String name) {
        Optional<StateDescriptor<?, ?>> stateDescriptor = vertex.findStateDescriptor(name);
        if (stateDescriptor.isEmpty() || !registeredStates.contains(name)) {
            return null;
        }
        return stateBackend.entrySet().stream().collect(Collectors.toMap(Entry::getKey, v -> {
            SubtaskStateBackend subtaskStateBackend = v.getValue();
            Map<Tuple2<?, ?>, T> value = new HashMap<>();

            for (Tuple2<?, ?> keysAndNamespace : subtaskStateBackend.getKeysAndNamespaces(name)) {
                T stateValue = subtaskStateBackend.readKeyedState(keysAndNamespace.f0,
                    keysAndNamespace.f1, vertex.getWindowSerializer(), stateDescriptor.get());
                value.put(keysAndNamespace, stateValue);
            }
            return value;
        }));
    }
}
