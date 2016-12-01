package com.efeiyi.website.entity;

/**
 * Created by Administrator on 2016/11/25.
 */
public class IchCategory extends Entity {
    private String id;
    private String parentId;
    private String name;
    private Integer gbCategory;
    private Integer status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Integer getGbCategory() {
        return gbCategory;
    }

    public void setGbCategory(Integer gbCategory) {
        this.gbCategory = gbCategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
