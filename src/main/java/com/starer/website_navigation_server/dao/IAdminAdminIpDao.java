package com.starer.website_navigation_server.dao;

import com.starer.website_navigation_server.pojo.AdminAdminIp;

public interface IAdminAdminIpDao {

    AdminAdminIp[] selectsByAdminId(String adminId);
    AdminAdminIp[] selectByAdminIpId(String adminIpId);
    int insert(AdminAdminIp adminAdminIp);;
}
