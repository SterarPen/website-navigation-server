package com.starer.website_navigation_server.pojo.dto;

public class LoginInformation {

    public static final byte ID_AND_PASSWORD = 1;
    public static final byte PHONE_AND_PASSWORD = 2;
    public static final byte EMAIL_AND_PASSWORD = 3;
    public static final byte PHONE_AND_CODE = 4;
    public static final byte EMAIL_AND_CODE = 5;
    /**
     * 或者将上面的登录方式加入到一个SortSet中或Map中
     */
    private static final byte MIN = ID_AND_PASSWORD;
    private static final byte MAX = EMAIL_AND_CODE;

    private final Byte loginType;
    private final String account;
    private final String token;
//    private String id;
//    private String phone;
//    private String email;
//    private String password;
//    private String code;


    private LoginInformation(Byte loginType, String account, String token) {
        this.loginType = loginType;
        this.account = account;
        this.token = token;
    }

    public static LoginInformation createFactory(Byte loginType, String account, String token) {
        if(loginType == null || account == null || token == null) {
            return null;
        }
        if(loginType < MIN || loginType > MAX) {
            return null;
        }
        return new LoginInformation(loginType, account, token);
    }

    public Byte getLoginType() {
        return loginType;
    }

    public String getAccount() {
        return account;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "LoginInformation{" +
                "loginType=" + loginType +
                ", account='" + account + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
