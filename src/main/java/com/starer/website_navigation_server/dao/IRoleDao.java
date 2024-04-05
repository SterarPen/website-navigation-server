package com.starer.website_navigation_server.dao;

import com.starer.website_navigation_server.pojo.Role;

public interface IRoleDao {

    Role selectById(String id);
}
