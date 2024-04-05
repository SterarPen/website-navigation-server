package com.starer.website_navigation_server.pojo.dto;

import java.util.Date;

public class TokenInformation {

    private String id;
    private String token;
    private String ip;
    private String device;
    private Date date;
    private Long expiration;

    public TokenInformation(String id, String token, String ip, String device, Date date, Long expiration) {
        this.id = id;
        this.token = token;
        this.ip = ip;
        this.device = device;
        this.date = date;
        this.expiration = expiration;
    }

    public String getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public String getIp() {
        return ip;
    }

    public String getDevice() {
        return device;
    }

    public Date getDate() {
        return date;
    }

    public Long getExpiration() {
        return expiration;
    }
}
