package com.starer.website_navigation_server.pojo;

import java.sql.Timestamp;

public class AdminLoginLog {

    private String logId;
    private Character status;
    private AdminIp adminIp;
    private Timestamp dateTime;
    private Admin admin;

    public AdminLoginLog() {
    }

    public AdminLoginLog(String logId, Character status, AdminIp adminIp, Timestamp dateTime, Admin admin) {
        this.logId = logId;
        this.status = status;
        this.adminIp = adminIp;
        this.dateTime = dateTime;
        this.admin = admin;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public Character getStatus() {
        return status;
    }

    public void setStatus(Character status) {
        this.status = status;
    }

    public AdminIp getAdminIp() {
        return adminIp;
    }

    public void setAdminIp(AdminIp adminIp) {
        this.adminIp = adminIp;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    @Override
    public String toString() {
        return "AdminLoginLog{" +
                "logId='" + logId + '\'' +
                ", status=" + status +
                ", adminIp=" + adminIp +
                ", dateTime=" + dateTime +
                ", admin=" + admin +
                '}';
    }
}
