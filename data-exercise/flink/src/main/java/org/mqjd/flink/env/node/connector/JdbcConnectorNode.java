package org.mqjd.flink.env.node.connector;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;
import org.mqjd.flink.env.node.core.BaseNode;
import org.mqjd.flink.env.node.core.ConnectorNode;
import org.mqjd.flink.util.ReflectionUtil;

public class JdbcConnectorNode extends BaseNode implements ConnectorNode {

    private String url;
    private String username;
    private String password;
    @JsonProperty("table-name")
    private String tableName;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void merge(JdbcConnectorNode jdbcConnectorNode) {
        if (jdbcConnectorNode != null) {
            super.merge(jdbcConnectorNode);
            ReflectionUtil.copyProperties(jdbcConnectorNode, this);
        }
    }
}
