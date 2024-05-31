package org.mqjd.flink.jobs.chapter3.section2;

import java.util.ArrayList;
import java.util.List;

public class JobState {

    private final List<VertexState> states = new ArrayList<>();

    public void addVertexState(VertexState state) {
        states.add(state);
    }

    public VertexState getState(int index) {
        return states.get(index);
    }

    public VertexState getState(String name) {
        return states.stream().filter(v -> v.getVertex().getName().contains(name)).findAny()
            .orElse(null);
    }
}
