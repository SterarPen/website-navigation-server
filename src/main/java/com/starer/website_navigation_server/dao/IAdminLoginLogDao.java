package com.starer.website_navigation_server.dao;

import com.starer.website_navigation_server.pojo.AdminLoginLog;

import java.sql.Timestamp;

public interface IAdminLoginLogDao {

    AdminLoginLog selectByLogId(String logId);
    AdminLoginLog[] select(AdminLoginLog adminLoginLog);
    AdminLoginLog[] selectBetweenDateTime(Timestamp start, Timestamp end);

    int insert(AdminLoginLog adminLoginLog);
}
