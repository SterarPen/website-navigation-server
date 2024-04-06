package com.starer.website_navigation_server.dao;

public interface IWebsiteTagDao {

    int insert(String websiteId, String tagId);
    int delete(String websiteId, String tagId);
}
