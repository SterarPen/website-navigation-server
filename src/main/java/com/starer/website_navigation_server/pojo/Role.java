package com.starer.website_navigation_server.pojo;

import java.util.Arrays;

public class Role {

    private String roleId;
    private String roleName;
    private Byte permission;

    public Role(String roleId, String roleName, Byte permission) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.permission = permission;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Byte getPermission() {
        return permission;
    }

    public void setPermission(Byte permission) {
        this.permission = permission;
    }

    @Override
    public String toString() {
        return "Role{" +
                "roleId='" + roleId + '\'' +
                ", roleName='" + roleName + '\'' +
                ", permission=" + permission +
                '}';
    }
}
