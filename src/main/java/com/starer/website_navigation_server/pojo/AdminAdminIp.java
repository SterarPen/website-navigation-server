package com.starer.website_navigation_server.pojo;

public class AdminAdminIp {

    private String adminId;
    private String adminIpId;

    public AdminAdminIp(String adminId, String adminIpId) {
        this.adminId = adminId;
        this.adminIpId = adminIpId;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getAdminIpId() {
        return adminIpId;
    }

    public void setAdminIpId(String adminIpId) {
        this.adminIpId = adminIpId;
    }

    @Override
    public String toString() {
        return "AdminAdminIp{" +
                "adminId='" + adminId + '\'' +
                ", adminIpId='" + adminIpId + '\'' +
                '}';
    }
}
