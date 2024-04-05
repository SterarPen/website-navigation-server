package com.starer.website_navigation_server.pojo;

import java.util.Set;

public class Admin {

    private String adminId;
    private String adminName;
    private String password;
    private String phone;
    private String email;
    private Set<AdminIp> adminIpSet;
    private Character expire;

    /**
     * USE THIS METHOD TO QUERY'S CONDITION
     */
    public Admin() {
    }

    /**
     * USE THIS METHOD TO UPDATE A ADMIN IN DATABASE
     * @param adminId
     */
    public Admin(String adminId) {
        this.adminId = adminId;
    }

    /**
     * USE THIS METHOD TO CREATE A NEW ADMIN AND INSERT THE ADMIN TO DATABASE
     * @param adminId
     * @param adminName
     * @param password
     * @param phone
     */
    public Admin(String adminId, String adminName, String password, String phone) {
        this.adminId = adminId;
        this.adminName = adminName;
        this.password = password;
        this.phone = phone;
    }

    public Admin(String adminId, String adminName, String password, String phone, String email, Set<AdminIp> adminIpSet, Character expire) {
        this.adminId = adminId;
        this.adminName = adminName;
        this.password = password;
        this.phone = phone;
        this.email = email;
        this.adminIpSet = adminIpSet;
        this.expire = expire;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<AdminIp> getAdminIpSet() {
        return adminIpSet;
    }

    public void setAdminIpSet(Set<AdminIp> adminIpSet) {
        this.adminIpSet = adminIpSet;
    }

    public Character getExpire() {
        return expire;
    }

    public void setExpire(char expire) {
        this.expire = expire;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "adminId='" + adminId + '\'' +
                ", adminName='" + adminName + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", adminIpSet=" + adminIpSet +
                ", expire=" + expire +
                '}';
    }
}
