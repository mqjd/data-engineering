package org.mqjd.flink.jobs.chapter1.section3.windowing;

import java.io.Serial;
import java.io.Serializable;

import org.mqjd.flink.jobs.chapter1.section3.source.RunMetric;

public class TopSpeedAccumulator implements Serializable {

    @Serial
    private static final long serialVersionUID = 3266155111426858813L;
    private RunMetric result;
    private RunMetric last;

    public void add(RunMetric value) {
        result = min();
        last = value;

    }

    private RunMetric min() {
        if (result == null) {
            return last;
        }
        if (last == null) {
            return null;
        }

        if (result.getPace() > last.getPace()) {
            return last;
        } else {
            return result;
        }
    }

    private RunMetric max() {
        if (result == null) {
            return last;
        }
        if (last == null) {
            return null;
        }

        if (result.getPace() > last.getPace()) {
            return result;
        } else {
            return last;
        }
    }

    public RunMetric getResult() {
        RunMetric result = this.result;
        this.result = last = null;
        return result;
    }
}
