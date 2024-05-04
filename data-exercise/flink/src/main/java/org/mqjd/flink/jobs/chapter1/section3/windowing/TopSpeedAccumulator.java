package org.mqjd.flink.jobs.chapter1.section3.windowing;

import org.mqjd.flink.jobs.chapter1.section3.source.RunMetric;

import java.io.Serial;
import java.io.Serializable;

public class TopSpeedAccumulator implements Serializable {

    @Serial
    private static final long serialVersionUID = 3266155111426858813L;
    private RunMetric f0;
    private RunMetric f1;

    public void add(RunMetric value) {
        f0 = max();
        f1 = value;
    }

    private RunMetric max() {
        if (f0 == null) {
            return f1;
        } else if (f1 != null) {
            if (f0.getPace() > f1.getPace()) {
                return f0;
            } else {
                return f1;
            }
        }
        return null;
    }

    public RunMetric getResult() {
        RunMetric result = f0;
        f0 = f1 = null;
        return result;
    }
}
