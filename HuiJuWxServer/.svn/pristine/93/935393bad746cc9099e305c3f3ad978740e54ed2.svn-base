package com.huiju.weixin.WeixinServer.model;

import java.util.Date;

public class UserLockTb {
    private Integer id;

    private String openId;

    private String lockId;

    private Date bindTime;

    private LockTb lockTb;
    
    private UserTb userTb;

	private Integer roleId;
    
	public LockTb getLockTb() {
		return lockTb;
	}

	public void setLockTb(LockTb lockTb) {
		this.lockTb = lockTb;
	}

	public UserTb getUserTb() {
		return userTb;
	}

	public void setUserTb(UserTb userTb) {
		this.userTb = userTb;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Date getBindTime() {
        return bindTime;
    }

    public void setBindTime(Date bindTime) {
        this.bindTime = bindTime;
    }

    
    
    public void preInsert(){
    	this.bindTime = new Date();
    }


	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
	
	public Integer getRoleId(){
		return this.roleId;
	}
    
}