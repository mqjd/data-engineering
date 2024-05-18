package org.mqjd.flink.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcUtil {

    public static List<Map<String, Object>> query(Connection connection, String sql) {
        try (Statement statement = connection.createStatement(); connection) {
            ResultSet resultSet = statement.executeQuery(sql);
            List<Map<String, Object>> result = new ArrayList<>();
            Map<String, Object> row;
            while (resultSet.next()) {
                row = new HashMap<>();
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), resultSet.getObject(i));
                }
                result.add(row);
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(STR."error when execute sql: \{sql}", e);
        }

    }

}
