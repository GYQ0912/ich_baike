package com.efeiyi.website.entity;

import java.util.Date;

/**
 * Created by Administrator on 2016/11/25.
 */
public class IchItem extends Entity {
    private String id;
    private String ichCategoryId;
    private String resourceId;
    private String lastEditorId;
    private String series;
    private String cnName;
    private String enName;
    private String pinyin;
    private String declareBatch;
    private Integer ichRank;
    private Date lastEditDate;
    private Integer editRank;
    private String introduction;
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

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getLastEditorId() {
        return lastEditorId;
    }

    public void setLastEditorId(String lastEditorId) {
        this.lastEditorId = lastEditorId;
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getDeclareBatch() {
        return declareBatch;
    }

    public void setDeclareBatch(String declareBatch) {
        this.declareBatch = declareBatch;
    }

    public Integer getIchRank() {
        return ichRank;
    }

    public void setIchRank(Integer ichRank) {
        this.ichRank = ichRank;
    }

    public Date getLastEditDate() {
        return lastEditDate;
    }

    public void setLastEditDate(Date lastEditDate) {
        this.lastEditDate = lastEditDate;
    }

    public Integer getEditRank() {
        return editRank;
    }

    public void setEditRank(Integer editRank) {
        this.editRank = editRank;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }
}
