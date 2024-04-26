package org.mqjd.flink.containers.mysql;

public enum MySqlVersion {
    V5_5("5.5"),
    V5_6("5.6"),
    V5_7("5.7"),
    V8_0("8.0"),
    V8_3("8.3.0");

    private final String version;

    MySqlVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "MySqlVersion{" + "version='" + version + '\'' + '}';
    }
}
