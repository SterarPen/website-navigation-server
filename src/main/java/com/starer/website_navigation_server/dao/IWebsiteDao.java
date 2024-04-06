package com.starer.website_navigation_server.dao;

import com.starer.website_navigation_server.pojo.Website;

public interface IWebsiteDao {

    int insert(Website website);
    int delete(String id);
    int update(Website website);

    Website selectById(String id);
    Website[] selectAll();
    Website[] select(Website website);

    Website[] selectByTypeId(String typeId);

}
