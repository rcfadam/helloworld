package com.huiju.weixin.WeixinServer.model;

public class TempTb {
    private String openid;

    private String keyid;

    private String lockid;

    private KeyTb keyTb;
    
    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid == null ? null : openid.trim();
    }

    public String getKeyid() {
        return keyid;
    }

    public void setKeyid(String keyid) {
        this.keyid = keyid == null ? null : keyid.trim();
    }

    public String getLockid() {
        return lockid;
    }

    public void setLockid(String lockid) {
        this.lockid = lockid == null ? null : lockid.trim();
    }

	public KeyTb getKeyTb() {
		return keyTb;
	}

	public void setKeyTb(KeyTb keyTb) {
		this.keyTb = keyTb;
	}
    
}