package org.mqjd.flink.jobs.chapter3.section2;

import java.util.ArrayList;
import java.util.List;

public class JobStateBackend {

    private final List<VertexStateBackend> vertexStateBackends = new ArrayList<>();

    public void addVertexStateBackend(VertexStateBackend vertexStateBackend) {
        vertexStateBackends.add(vertexStateBackend);
    }

    public VertexStateBackend getVertexStateBackend(int index) {
        return vertexStateBackends.get(index);
    }
}
