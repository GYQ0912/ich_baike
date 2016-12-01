package com.efeiyi.website.entity;

import java.util.Date;

/**
 * Created by Administrator on 2016/11/25.
 */
public class IchMaster extends Entity {
    private String id;
    private String ichCategoryId;
    private String resourceId;
    private String ichItemId;
    private String lastEditorId;
    private String series;
    private String cnName;
    private String enName;
    private String pinyin;
    private Integer gender;
    private Integer nation;
    private Date birthDate;
    private Integer birthPlace;
    private Date deathDate;
    private Integer presentPlace;
    private Integer selectedYear;
    private Integer declareCity;
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

    public String getIchItemId() {
        return ichItemId;
    }

    public void setIchItemId(String ichItemId) {
        this.ichItemId = ichItemId;
    }

    public String getLastEditorId() {
        return lastEditorId;
    }

    public void setLastEditorId(String lastEditorId) {
        this.lastEditorId = lastEditorId;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
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

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Integer getNation() {
        return nation;
    }

    public void setNation(Integer nation) {
        this.nation = nation;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Integer getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(Integer birthPlace) {
        this.birthPlace = birthPlace;
    }

    public Date getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(Date deathDate) {
        this.deathDate = deathDate;
    }

    public Integer getPresentPlace() {
        return presentPlace;
    }

    public void setPresentPlace(Integer presentPlace) {
        this.presentPlace = presentPlace;
    }

    public Integer getSelectedYear() {
        return selectedYear;
    }

    public void setSelectedYear(Integer selectedYear) {
        this.selectedYear = selectedYear;
    }

    public Integer getDeclareCity() {
        return declareCity;
    }

    public void setDeclareCity(Integer declareCity) {
        this.declareCity = declareCity;
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
}
