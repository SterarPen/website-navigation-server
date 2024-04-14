package com.starer.website_navigation_server.pojo.dto;

import java.util.concurrent.TimeUnit;

public class CacheInformation {

    private String key;
    private Object value;
    private Class<?> valueType;
    private Long expireTime;
    private TimeUnit timeUnit;

    public CacheInformation() {
    }

    public CacheInformation(String key, Object value, Class<?> valueType, Long expireTime, TimeUnit timeUnit) {
        this.key = key;
        this.value = value;
        this.valueType = valueType;
        this.expireTime = expireTime;
        this.timeUnit = timeUnit;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Class<?> getValueType() {
        return valueType;
    }

    public void setValueType(Class<?> valueType) {
        this.valueType = valueType;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    @Override
    public String toString() {
        return "CacheInformation{" +
                "key='" + key + '\'' +
                ", value=" + value +
                ", valueType=" + valueType +
                ", expireTime=" + expireTime +
                ", timeUnit=" + timeUnit +
                '}';
    }
}
