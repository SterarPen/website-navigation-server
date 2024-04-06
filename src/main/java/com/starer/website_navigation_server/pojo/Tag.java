package com.starer.website_navigation_server.pojo;

public class Tag {

    private String tagId;
    private String tagName;

    public Tag(String tagId, String tagName) {
        this.tagId = tagId;
        this.tagName = tagName;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "tagId='" + tagId + '\'' +
                ", tagName='" + tagName + '\'' +
                '}';
    }
}
