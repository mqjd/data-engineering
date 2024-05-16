package org.mqjd.flink.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TimerUtil {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(8);

    public static ScheduledFuture<?> interval(Runnable command, long period) {
        return scheduler.scheduleAtFixedRate(command, 0L, period, TimeUnit.MILLISECONDS);
    }

    public static ScheduledFuture<?> timeout(Runnable command, long period) {
        return scheduler.schedule(command, period, TimeUnit.MILLISECONDS);
    }
}
