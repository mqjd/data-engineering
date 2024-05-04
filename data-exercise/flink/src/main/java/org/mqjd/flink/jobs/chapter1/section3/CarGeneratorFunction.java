package org.mqjd.flink.jobs.chapter1.section3;

import java.io.Serial;
import java.util.Arrays;
import java.util.Random;

import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.connector.datagen.source.GeneratorFunction;

public class CarGeneratorFunction implements GeneratorFunction<Long, Tuple4<Integer, Integer, Double, Long>> {

    @Serial
    private static final long serialVersionUID = -964021412259980951L;
    // in kilometers per hour
    private final int[] speeds;
    // in meters
    private final double[] distances;
    // in milliseconds
    private final long[] lastUpdate;
    private int nextCar;

    private static final int MILLIS_IN_HOUR = 1000 * 60 * 60;
    private static final double HOURS_IN_MILLI = 1d / MILLIS_IN_HOUR;
    private static final int METERS_IN_KILOMETER = 1000;

    private final Random rand = new Random();

    // Previous version (CarSource) was overestimating the speed. This factor is used to preserve
    // the original behaviour of the example.
    private static final int COMPAT_FACTOR = 10;

    public CarGeneratorFunction(int numOfCars) {
        speeds = new int[numOfCars];
        distances = new double[numOfCars];
        lastUpdate = new long[numOfCars];
        Arrays.fill(speeds, 50);
        Arrays.fill(distances, 0d);
        Arrays.fill(lastUpdate, 0);
    }

    @Override
    public Tuple4<Integer, Integer, Double, Long> map(Long ignoredIndex) throws Exception {
        if (rand.nextBoolean()) {
            speeds[nextCar] = Math.min(100, speeds[nextCar] + 5);
        } else {
            speeds[nextCar] = Math.max(0, speeds[nextCar] - 5);
        }
        long now = System.currentTimeMillis();
        long timeDiffMillis = lastUpdate[nextCar] == 0 ? 0 : now - lastUpdate[nextCar];
        lastUpdate[nextCar] = now;
        distances[nextCar] += speeds[nextCar] * (timeDiffMillis * HOURS_IN_MILLI) * METERS_IN_KILOMETER * COMPAT_FACTOR;
        nextCar = (++nextCar) % speeds.length;
        return new Tuple4<>(nextCar, speeds[nextCar], distances[nextCar], now);
    }

}
