package org.mqjd.flink.jobs.chapter1.section3.source;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.mqjd.flink.util.JsonUtil;

@JsonPropertyOrder({ "userId", "userName", "sex", "age", "distance", "pace", "timestamp" })
public class RunMetric implements Serializable {
    private static final long serialVersionUID = -3032216750258115148L;
    private static final DateTimeFormatter dateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    private String userId;
    private String userName;
    private Sex sex;
    private Integer age;
    private Double distance;
    private Integer pace;
    private Long timestamp;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Integer getPace() {
        return pace;
    }

    public void setPace(Integer pace) {
        this.pace = pace;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    @JsonIgnore
    public String getFormatedTimestamp() {
        return dateTimeFormatter.format(Instant.ofEpochMilli(timestamp));
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp =
            LocalDateTime.parse(timestamp, dateTimeFormatter).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }
}
