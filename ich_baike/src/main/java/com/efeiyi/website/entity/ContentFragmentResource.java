package com.efeiyi.website.entity;

/**
 * Created by Administrator on 2016/11/25.
 */
public class ContentFragmentResource extends Entity {
    private String id;
    private String contentFragmentId;
    private String resourceId;
    private Integer status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContentFragmentId() {
        return contentFragmentId;
    }

    public void setContentFragmentId(String contentFragmentId) {
        this.contentFragmentId = contentFragmentId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
