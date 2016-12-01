package com.efeiyi.website.entity;

/**
 * Created by Administrator on 2016/11/25.
 */
public class EditDetail extends Entity {
    private String id;
    private String editRecordId;
    private String fieldName;
    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEditRecordId() {
        return editRecordId;
    }

    public void setEditRecordId(String editRecordId) {
        this.editRecordId = editRecordId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
