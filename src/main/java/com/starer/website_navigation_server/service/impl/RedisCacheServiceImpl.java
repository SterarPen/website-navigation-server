package com.starer.website_navigation_server.service.impl;

import com.starer.website_navigation_server.pojo.dto.CacheInformation;
import com.starer.website_navigation_server.service.ICacheService;
import com.starer.website_navigation_server.util.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Service
public class RedisCacheServiceImpl implements ICacheService {

    private final RedisTemplate<String, String> redisTemplate;
    private final Properties properties;

    @Autowired
    public RedisCacheServiceImpl(RedisTemplate<String, String> redisTemplate, Properties properties) {
        this.redisTemplate = redisTemplate;
        this.properties = properties;
    }

    @Override
    public ServiceResult<CacheInformation> setString(CacheInformation cacheInformation) {
        if(cacheInformation == null ||
                cacheInformation.getKey() == null ||
                cacheInformation.getValueType() != String.class ||
                cacheInformation.getValue() == null) {
            return ServiceResult.createFactory(
                    Integer.parseInt(properties.getProperty("FAILURE.CACHE")),
                    properties.getProperty("FAILURE.CACHE.KEY_OR_VALUE_IS_NULL")
            );
        }

        redisTemplate.opsForValue().set(cacheInformation.getKey(), (String) cacheInformation.getValue());

        return ServiceResult.createFactory(
                Integer.parseInt(properties.getProperty("SUCCESS.CACHE")),
                properties.getProperty("SUCCESS.CACHE.SET_STRING"),
                cacheInformation);
    }

    @Override
    public ServiceResult<CacheInformation> setStringAndExpire(CacheInformation cacheInformation) {
        if(cacheInformation == null ||
                cacheInformation.getKey() == null ||
                cacheInformation.getValueType() != String.class ||
                cacheInformation.getValue() == null ||
                cacheInformation.getExpireTime() == null ||
                cacheInformation.getTimeUnit() == null) {
            return ServiceResult.createFactory(
                    Integer.parseInt(properties.getProperty("FAILURE.CACHE")),
                    properties.getProperty("FAILURE.CACHE.KEY_OR_VALUE_IS_NULL")
            );
        }

        redisTemplate.opsForValue().set(
                cacheInformation.getKey(),
                (String) cacheInformation.getValue(),
                cacheInformation.getExpireTime(),
                cacheInformation.getTimeUnit()
        );

        return ServiceResult.createFactory(
                Integer.parseInt(properties.getProperty("SUCCESS.CACHE")),
                properties.getProperty("SUCCESS.CACHE.SET_STRING"),
                cacheInformation);
    }

    @Override
    public ServiceResult<CacheInformation> setExpire(CacheInformation cacheInformation) {
        if(cacheInformation == null ||
            cacheInformation.getKey() == null ||
            cacheInformation.getExpireTime() == null ||
            cacheInformation.getTimeUnit() == null) {
            return ServiceResult.createFactory(
                    Integer.parseInt(properties.getProperty("FAILURE.CACHE")),
                    properties.getProperty("FAILURE.CACHE.KEY_OR_VALUE_IS_NULL")
            );
        }

        redisTemplate.expire(cacheInformation.getKey(),
                cacheInformation.getExpireTime(),
                cacheInformation.getTimeUnit()
        );

        return ServiceResult.createFactory(
                Integer.parseInt(properties.getProperty("SUCCESS.CACHE")),
                properties.getProperty("SUCCESS.CACHE.SET_STRING"),
                cacheInformation);
    }

    @Override
    public ServiceResult<CacheInformation> getString(String key) {
        if(key == null) {
            return ServiceResult.createFactory(
                    Integer.parseInt(properties.getProperty("FAILURE.CACHE")),
                    properties.getProperty("FAILURE.CACHE.KEY_IS_NULL")
            );
        }

        String value = redisTemplate.opsForValue().get(key);
        Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);

        if(value == null) {
            return ServiceResult.createFactory(
                    Integer.parseInt(properties.getProperty("FAILURE.CACHE")),
                    properties.getProperty("FAILURE.CACHE.VALUE_IS_NULL")
            );
        }

        CacheInformation cacheInformation = new CacheInformation(
                key, value, String.class, expire, TimeUnit.SECONDS
        );
        return ServiceResult.createFactory(
                Integer.parseInt(properties.getProperty("SUCCESS.CACHE")),
                properties.getProperty("SUCCESS.CACHE.GET_STRING"),
                cacheInformation
        );
    }

    @Override
    public ServiceResult<Long> getExpire(String key) {
        if(key == null) {
            return ServiceResult.createFactory(
                    Integer.parseInt(properties.getProperty("FAILURE.CACHE")),
                    properties.getProperty("FAILURE.CACHE.KEY_IS_NULL")
            );
        }

        Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);

        if(expire == null) {
            return ServiceResult.createFactory(
                    Integer.parseInt(properties.getProperty("FAILURE.CACHE")),
                    properties.getProperty("FAILURE.CACHE.VALUE_IS_NULL")
            );
        }

        return ServiceResult.createFactory(
                Integer.parseInt(properties.getProperty("SUCCESS.CACHE")),
                properties.getProperty("SUCCESS.CACHE.GET_STRING"),
                expire
        );
    }
}
