package com.starer.website_navigation_server.pojo.dto;

public class RegisterInformation {

    private final String phone;
    private final String code;
    private final String password;

    public RegisterInformation(String phone, String code, String password) {
        this.phone = phone;
        this.code = code;
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public String getCode() {
        return code;
    }

    public String getPassword() {
        return password;
    }
}
