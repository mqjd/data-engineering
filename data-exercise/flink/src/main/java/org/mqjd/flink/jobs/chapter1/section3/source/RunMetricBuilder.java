package org.mqjd.flink.jobs.chapter1.section3.source;

public final class RunMetricBuilder {
    private String userId;
    private String userName;
    private Sex sex;
    private Integer age;
    private Double distance;
    private Integer pace;
    private Long timestamp;

    private RunMetricBuilder() {
    }

    public static RunMetricBuilder builder() {
        return new RunMetricBuilder();
    }

    public RunMetricBuilder from(RunMetric runMetric) {
        this.userId = runMetric.getUserId();
        this.userName = runMetric.getUserName();
        this.sex = runMetric.getSex();
        this.age = runMetric.getAge();
        this.distance = runMetric.getDistance();
        this.pace = runMetric.getPace();
        this.timestamp = runMetric.getTimestamp();
        return this;
    }

    public RunMetricBuilder withUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public RunMetricBuilder withUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public RunMetricBuilder withSex(Sex sex) {
        this.sex = sex;
        return this;
    }

    public RunMetricBuilder withAge(Integer age) {
        this.age = age;
        return this;
    }

    public RunMetricBuilder withDistance(Double distance) {
        this.distance = distance;
        return this;
    }

    public RunMetricBuilder withPace(Integer pace) {
        this.pace = pace;
        return this;
    }

    public RunMetricBuilder withTimestamp(Long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public static RunMetric buildDefault(int index) {
        RunMetric runMetric = new RunMetric();
        runMetric.setUserId(String.format("%03d", index));
        runMetric.setUserName(String.format("user-%03d", index));
        runMetric.setSex(Sex.MALE);
        runMetric.setAge(22);
        runMetric.setDistance(0D);
        runMetric.setPace(300);
        runMetric.setTimestamp(System.currentTimeMillis());
        return runMetric;
    }

    public RunMetric build() {
        RunMetric runMetric = new RunMetric();
        runMetric.setUserId(userId);
        runMetric.setUserName(userName);
        runMetric.setSex(sex);
        runMetric.setAge(age);
        runMetric.setDistance(distance);
        runMetric.setPace(pace);
        runMetric.setTimestamp(timestamp);
        return runMetric;
    }
}
