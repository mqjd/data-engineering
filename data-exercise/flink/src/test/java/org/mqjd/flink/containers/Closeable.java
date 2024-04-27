package org.mqjd.flink.containers;

import org.testcontainers.lifecycle.Startable;

public class Closeable implements Startable {

    private final AutoCloseable closeable;

    public Closeable(AutoCloseable closeable) {
        this.closeable = closeable;
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
        try {
            closeable.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
