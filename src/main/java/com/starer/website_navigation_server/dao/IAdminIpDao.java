package com.starer.website_navigation_server.dao;

import com.starer.website_navigation_server.pojo.AdminIp;

public interface IAdminIpDao {

    AdminIp selectByAdminIpId(String adminIpId);
    AdminIp[] selectsByAdminIpIdArray(String[] adminIpIdArray);

    int insert(AdminIp adminIp);
}
