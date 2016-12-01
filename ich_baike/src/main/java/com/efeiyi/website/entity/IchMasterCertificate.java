package com.efeiyi.website.entity;

/**
 * Created by Administrator on 2016/11/25.
 */
public class IchMasterCertificate extends Entity {
    private String id;
    private String ichMasterId;
    private String certificateId;
    private Integer status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIchMasterId() {
        return ichMasterId;
    }

    public void setIchMasterId(String ichMasterId) {
        this.ichMasterId = ichMasterId;
    }

    public String getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(String certificateId) {
        this.certificateId = certificateId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
