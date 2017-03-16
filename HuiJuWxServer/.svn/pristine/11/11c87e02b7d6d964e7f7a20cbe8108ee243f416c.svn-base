package com.huiju.weixin.WeixinServer.model;

import java.util.Date;

public class LockKeyTb {
    private Integer id;

    private String lockId;

    private String keyId;

    private Date bindTime;
    
    private int position;
    
    private String isFlag;//0 为已删除状态  1为可用状态
    
    private LockTb lockTb;
    
    private KeyTb keyTb;
    
    private String state; 
    
    public LockTb getLockTb() {
		return lockTb;
	}

	public void setLockTb(LockTb lockTb) {
		this.lockTb = lockTb;
	}

	public KeyTb getKeyTb() {
		return keyTb;
	}

	public void setKeyTb(KeyTb keyTb) {
		this.keyTb = keyTb;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLockId() {
        return lockId;
    }

    public void setLockId(String lockId) {
        this.lockId = lockId == null ? null : lockId.trim();
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId == null ? null : keyId.trim();
    }

    public Date getBindTime() {
        return bindTime;
    }

    public void setBindTime(Date bindTime) {
        this.bindTime = bindTime;
    }
    
    public void preInsert(){
    	this.bindTime = new Date();
    }

	public String getIsFlag() {
		return isFlag;
	}

	public void setIsFlag(String isFlag) {
		this.isFlag = isFlag;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
}