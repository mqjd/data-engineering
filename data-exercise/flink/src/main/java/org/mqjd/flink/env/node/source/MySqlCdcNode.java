package org.mqjd.flink.env.node.source;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;
import org.mqjd.flink.env.node.core.BaseNode;
import org.mqjd.flink.env.node.core.SourceNode;
import org.mqjd.flink.util.ReflectionUtil;

public class MySqlCdcNode extends BaseNode implements SourceNode {

    private String hostname;
    private Integer port;
    private String username;
    private String password;
    @JsonProperty("database-name")
    private String databaseName;
    @JsonProperty("table-name")
    private String tableName;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void merge(MySqlCdcNode source) {
        if (source != null) {
            super.merge(source);
            ReflectionUtil.copyProperties(source, this);
        }
    }
}
