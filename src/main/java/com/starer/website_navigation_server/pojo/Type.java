package com.starer.website_navigation_server.pojo;

public class Type {

    private String typeId;
    private String parentTypeId;
    private String name;

    public Type(String typeId, String parentTypeId, String name) {
        this.typeId = typeId;
        this.parentTypeId = parentTypeId;
        this.name = name;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getParentTypeId() {
        return parentTypeId;
    }

    public void setParentTypeId(String parentTypeId) {
        this.parentTypeId = parentTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Type{" +
                "typeId='" + typeId + '\'' +
                ", parentTypeId='" + parentTypeId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
