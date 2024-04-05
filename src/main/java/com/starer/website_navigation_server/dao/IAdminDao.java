package com.starer.website_navigation_server.dao;

import com.starer.website_navigation_server.pojo.Admin;

public interface IAdminDao {

    Admin[] selectAll();
    Admin selectById(String userId);

    Admin[] select(Admin admin);

    int update(Admin admin);

    int insert(Admin admin);
}
