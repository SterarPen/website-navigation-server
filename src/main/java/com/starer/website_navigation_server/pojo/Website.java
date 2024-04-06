package com.starer.website_navigation_server.pojo;

public class Website {

    private String id;
    private String name;
    private String typeId;
    private String url;
    private String favicon;
    private String description;
    private Character access;

    public Website(String id, String name, String typeId, String url, String favicon, String description, Character access) {
        this.id = id;
        this.name = name;
        this.typeId = typeId;
        this.url = url;
        this.favicon = favicon;
        this.description = description;
        this.access = access;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFavicon() {
        return favicon;
    }

    public void setFavicon(String favicon) {
        this.favicon = favicon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Character getAccess() {
        return access;
    }

    public void setAccess(Character access) {
        this.access = access;
    }

    @Override
    public String toString() {
        return "Website{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", typeId='" + typeId + '\'' +
                ", url='" + url + '\'' +
                ", favicon='" + favicon + '\'' +
                ", description='" + description + '\'' +
                ", access=" + access +
                '}';
    }
}
