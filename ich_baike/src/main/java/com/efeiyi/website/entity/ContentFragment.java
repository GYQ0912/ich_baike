package com.efeiyi.website.entity;

/**
 * Created by Administrator on 2016/11/25.
 */
public class ContentFragment extends Entity{
    private String id;
    private String targetId;
    private Integer targetType;
    private String title;
    private Integer type;
    private String content;
    private Integer graghicRelation;
    private String attributeOfCategoryid;
    private Integer status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public Integer getTargetType() {
        return targetType;
    }

    public void setTargetType(Integer targetType) {
        this.targetType = targetType;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getGraghicRelation() {
        return graghicRelation;
    }

    public void setGraghicRelation(Integer graghicRelation) {
        this.graghicRelation = graghicRelation;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAttributeOfCategoryid() {
        return attributeOfCategoryid;
    }

    public void setAttributeOfCategoryid(String attributeOfCategoryid) {
        this.attributeOfCategoryid = attributeOfCategoryid;
    }
}
