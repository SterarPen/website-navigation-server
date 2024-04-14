package com.starer.website_navigation_server.pojo.dto;

public class RegisterInformation {

    private String userName;
    private String platform;
    private String account;
    private String code;
    private String password;

    public RegisterInformation(String userName, String platform, String account, String code, String password) {
        this.userName = userName;
        this.platform = platform;
        this.account = account;
        this.code = code;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
