package com.starer.website_navigation_server.service;

import com.starer.website_navigation_server.pojo.dto.CacheInformation;
import com.starer.website_navigation_server.util.ServiceResult;

import java.util.concurrent.TimeUnit;

public interface ICacheService {

    ServiceResult<CacheInformation> setString(CacheInformation cacheInformation);
    ServiceResult<CacheInformation> setStringAndExpire(CacheInformation cacheInformation);
    ServiceResult<CacheInformation> setExpire(CacheInformation cacheInformation);
    ServiceResult<CacheInformation> getString(String key);
    ServiceResult<Long> getExpire(String key);
}
