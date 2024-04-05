package com.starer.website_navigation_server.dao;

import com.starer.website_navigation_server.pojo.User;

public interface IUserDao {

    User[] selectAll();
    User selectById(String userId);

    User[] select(User user);

    int update(User user);

    int insert(User user);
}
