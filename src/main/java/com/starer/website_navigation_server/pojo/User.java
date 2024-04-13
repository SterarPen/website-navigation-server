package com.starer.website_navigation_server.pojo;

import java.sql.Date;

public class User {

    private String userId;
    private String userName;
    private String password;
    private Role role;
    private Character sex;
    private String phone;
    private String email;
    private Date birthday;
    private Character expire;
    private Short age;

    public User() {
    }

    public User(String userId) {
        this.userId = userId;
    }

    public User(String userId, String userName, String password, Role role, String phone) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.phone = phone;
    }



    public User(String userId, String userName, String password, Role role, Character sex, String phone, String email, Date birthday, Character expire) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.sex = sex;
        this.phone = phone;
        this.email = email;
        this.birthday = birthday;
        this.expire = expire;
    }

    public User(String userId, String userName, String password, Role role, Character sex, String phone, String email, Date birthday, Character expire, Short age) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.sex = sex;
        this.phone = phone;
        this.email = email;
        this.birthday = birthday;
        this.expire = expire;
        this.age = age;
    }

    public void setSex(Character sex) {
        this.sex = sex;
    }

    public void setExpire(Character expire) {
        this.expire = expire;
    }

    public Short getAge() {
        return age;
    }

    public void setAge(Short age) {
        this.age = age;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Character getSex() {
        return sex;
    }

    public void setSex(char sex) {
        this.sex = sex;
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

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Character getExpire() {
        return expire;
    }

    public void setExpire(char expire) {
        this.expire = expire;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", sex=" + sex +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", birthday=" + birthday +
                ", expire=" + expire +
                '}';
    }
}
