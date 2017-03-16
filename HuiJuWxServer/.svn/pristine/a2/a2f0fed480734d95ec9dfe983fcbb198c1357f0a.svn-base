package com.huiju.weixin.WeixinServer.model;

import java.io.Serializable;
import java.util.Date;

import com.huiju.weixin.WeixinServer.util.IdGen;


public class KeyTable implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;

    private String keyId;

    private String deviceId;

    private Integer type;

    private String openId;

    private String lockId;

    private Integer position;

    private String bindDeviceId;

    private String keyState;
    
    private String ticketId;

	private Date createdAt;

    private Date updatedAt;

    private String createdBy;

    private String updatedBy;

    private String delFlag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId == null ? null : keyId.trim();
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId == null ? null : deviceId.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId == null ? null : openId.trim();
    }

    public String getLockId() {
        return lockId;
    }

    public void setLockId(String lockId) {
        this.lockId = lockId == null ? null : lockId.trim();
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getBindDeviceId() {
        return bindDeviceId;
    }

    public void setBindDeviceId(String bindDeviceId) {
        this.bindDeviceId = bindDeviceId == null ? null : bindDeviceId.trim();
    }

    public String getKeyState() {
        return keyState;
    }

    public void setKeyState(String keyState) {
        this.keyState = keyState == null ? null : keyState.trim();
    }


    public String getTicketId() {
		return ticketId;
	}

	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy == null ? null : createdBy.trim();
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy == null ? null : updatedBy.trim();
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag == null ? null : delFlag.trim();
    }
    
    public void preInsert(){
    	id = IdGen.uuid();
    	Date now = new Date();
    	createdAt = now;
    	updatedAt = now;
    	createdBy = id;
    	updatedBy = id;
    	delFlag = "0";
    }
    
    public void preUpdate(String updatedBy){
    	Date now = new Date();
    	updatedAt = now;
    	this.updatedBy = updatedBy;
    }
}