package com.huiju.weixin.WeixinServer.model;

import java.util.Date;

public class LockTb {
    private Integer id;

    private String lockId;

    private String state;

    private Date createTime;

    private String creator;

    private String version;
    
    private String lockName;
    
    private String note;
   
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLockId() {
        return lockId;
    }

    public LockTb() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LockTb(Integer id, String lockId, String state, Date createTime, String creator, String version,
			String lockName, String note) {
		super();
		this.id = id;
		this.lockId = lockId;
		this.state = state;
		this.createTime = createTime;
		this.creator = creator;
		this.version = version;
		this.lockName = lockName;
		this.note = note;
	}

	public void setLockId(String lockId) {
        this.lockId = lockId == null ? null : lockId.trim();
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state == null ? null : state.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator == null ? null : creator.trim();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version == null ? null : version.trim();
    }

	public String getLockName() {
		return lockName;
	}

	public void setLockName(String lockName) {
		this.lockName = lockName;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public void preInsert() {
		this.createTime = new Date();
		this.state  = "1";
	}
}