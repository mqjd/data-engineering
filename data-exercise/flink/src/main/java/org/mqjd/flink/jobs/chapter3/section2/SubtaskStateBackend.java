package org.mqjd.flink.jobs.chapter3.section2;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.flink.api.common.state.AppendingState;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.State;
import org.apache.flink.api.common.state.StateDescriptor;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.runtime.state.KeyedStateBackend;
import org.apache.flink.runtime.state.OperatorStateBackend;
import org.apache.flink.runtime.state.heap.HeapKeyedStateBackend;
import org.apache.flink.runtime.state.heap.StateTable;
import org.apache.flink.streaming.api.operators.util.SimpleVersionedListState;
import org.apache.flink.util.IterableUtils;
import org.mqjd.flink.jobs.chapter3.section2.core.States;
import org.mqjd.flink.util.ReflectionUtil;

public class SubtaskStateBackend {

    private final VertexDescription vertex;
    private final Integer subtaskIndex;
    private final OperatorStateBackend operatorStateBackend;

    @SuppressWarnings("rawtypes")
    private final KeyedStateBackend keyedStateBackend;

    public SubtaskStateBackend(VertexDescription description, Integer subtaskIndex,
        OperatorStateBackend operatorStateBackend, KeyedStateBackend<?> keyedStateBackend) {
        this.vertex = description;
        this.subtaskIndex = subtaskIndex;
        this.operatorStateBackend = operatorStateBackend;
        this.keyedStateBackend = keyedStateBackend;
    }

    public Integer getSubtaskIndex() {
        return subtaskIndex;
    }

    public Set<String> getRegisteredOperatorStateNames() {
        return operatorStateBackend.getRegisteredStateNames();
    }

    public Set<String> getRegisteredStates() {
        if (keyedStateBackend == null) {
            return Collections.emptySet();
        }
        if (keyedStateBackend instanceof HeapKeyedStateBackend) {
            return getRegisteredStates((HeapKeyedStateBackend<?>) keyedStateBackend);
        }
        return Collections.emptySet();
    }

    public Set<Tuple2<?, ?>> getKeysAndNamespaces(String state) {
        if (keyedStateBackend == null) {
            return Collections.emptySet();
        }
        if (keyedStateBackend instanceof HeapKeyedStateBackend) {
            return getKeysAndNamespaces(state, (HeapKeyedStateBackend<?>) keyedStateBackend);
        }
        return Collections.emptySet();
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> readOperatorState(ListStateDescriptor<?> state) {
        try {
            ListState<?> listState = operatorStateBackend.getListState(state);
            if (state == States.SPLITS_STATE_DESC) {
                listState = new SimpleVersionedListState<>((ListState<byte[]>) listState,
                    vertex.getSplitSerializer());
            }
            return (List<T>) IterableUtils.toStream(listState.get()).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T readKeyedState(Object key, Object namespace,
        TypeSerializer<?> namespaceSerializer, StateDescriptor<?, ?> state) {
        if (keyedStateBackend == null) {
            return null;
        }
        try {
            keyedStateBackend.setCurrentKey(key);
            State partitionedState = keyedStateBackend.getPartitionedState(namespace,
                namespaceSerializer, state);
            if (partitionedState instanceof AppendingState) {
                return (T) ((AppendingState<?, ?>) partitionedState).get();
            }
            return (T) partitionedState;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Set<String> getRegisteredStates(HeapKeyedStateBackend<?> stateBackend) {
        Map<String, StateTable<?, ?, ?>> states = ReflectionUtil.read(stateBackend,
            "registeredKVStates");
        return Optional.ofNullable(states).map(Map::keySet).orElse(Collections.emptySet());
    }

    private Set<Tuple2<?, ?>> getKeysAndNamespaces(String state,
        HeapKeyedStateBackend<?> stateBackend) {
        Map<String, StateTable<?, ?, ?>> states = ReflectionUtil.read(stateBackend,
            "registeredKVStates");
        if (states == null || !states.containsKey(state)) {
            return Collections.emptySet();
        }
        return states.get(state).getKeysAndNamespaces().collect(Collectors.toSet());
    }
}
