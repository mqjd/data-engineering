package org.mqjd.flink.containers;

public enum ContainerType {
    KAFKA("confluentinc/cp-kafka:7.6.0"), MYSQL("mysql:8.3.0");

    private final String dockerImageName;

    ContainerType(String dockerImageName) {
        this.dockerImageName = dockerImageName;
    }

    public String getDockerImageName() {
        return dockerImageName;
    }
}
