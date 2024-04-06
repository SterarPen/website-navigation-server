package com.starer.website_navigation_server.dao;

import com.starer.website_navigation_server.pojo.Tag;

public interface ITagDao {

    int insert(Tag tag);
    int update(Tag tag);
    int delete(String tagId);

    Tag selectById(String tagId);
    Tag[] select(Tag tag);
    Tag[] selectByWebsiteId(String websiteId);

}
