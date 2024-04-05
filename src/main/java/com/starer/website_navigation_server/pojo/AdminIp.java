package com.starer.website_navigation_server.pojo;


import java.util.Set;

public class AdminIp {

    private String adminIpId;
    private String type;
    private String adminIpValue;
    private String address;
    private Set<Admin> adminSet;

    public AdminIp() {
    }

    public AdminIp(String adminIpId, String type, String adminIpValue, String address) {
        this.adminIpId = adminIpId;
        this.type = type;
        this.adminIpValue = adminIpValue;
        this.address = address;
    }

    public AdminIp(String adminIpId, String type, String adminIpValue, String address, Set<Admin> adminSet) {
        this.adminIpId = adminIpId;
        this.type = type;
        this.adminIpValue = adminIpValue;
        this.address = address;
        this.adminSet = adminSet;
    }

    public String getAdminIpId() {
        return adminIpId;
    }

    public void setAdminIpId(String adminIpId) {
        this.adminIpId = adminIpId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAdminIpValue() {
        return adminIpValue;
    }

    public void setAdminIpValue(String adminIpValue) {
        this.adminIpValue = adminIpValue;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Set<Admin> getAdminSet() {
        return adminSet;
    }

    public void setAdminSet(Set<Admin> adminSet) {
        this.adminSet = adminSet;
    }

    @Override
    public String toString() {
        return "AdminIp{" +
                "adminIpId='" + adminIpId + '\'' +
                ", type='" + type + '\'' +
                ", adminIpValue='" + adminIpValue + '\'' +
                ", address='" + address + '\'' +
                ", adminSet=" + adminSet +
                '}';
    }
}
