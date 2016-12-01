package com.efeiyi.website.entity;

/**
 * Created by Administrator on 2016/11/29.
 */
public class AttributeOfCategory extends Entity {
    private String id;
    private String ichCategoryId;
    private String title;
    private Integer status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIchCategoryId() {
        return ichCategoryId;
    }

    public void setIchCategoryId(String ichCategoryId) {
        this.ichCategoryId = ichCategoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
