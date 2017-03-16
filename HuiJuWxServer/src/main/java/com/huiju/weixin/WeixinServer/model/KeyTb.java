package com.huiju.weixin.WeixinServer.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public class KeyTb implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer kid;

    private String keyId;

    private String deviceId;

    private String keyName;

    private Integer type;

    private Integer position;

    private Date createTime;

    private String creator;

    private String state;

    private Date bindTime;

    private String toothCode;

    private String qrcode;

    private Date time;  

    private String note;

    private String version;

    private String ticketId;

    private String qrcodeSerial;

    private String mac;
    
    private int authtime;

	private String authOpenId;
	private String authState;

    public Integer getKid() {
        return kid;
    }

    public void setKid(Integer kid) {
        this.kid = kid;
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

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName == null ? null : keyName.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

   public KeyTb(){
	   
   }

	public KeyTb(Integer kid, String keyId, String deviceId, String keyName, Integer type, Integer position,
			Date createTime, String creator, String state, Date bindTime, String toothCode, String qrcode,
			Date time, String note, String version, String ticketId, String qrcodeSerial, String mac) {
		super();
		this.kid = kid;
		this.keyId = keyId;
		this.deviceId = deviceId;
		this.keyName = keyName;
		this.type = type;
		this.position = position;
		this.createTime = createTime;
		this.creator = creator;
		this.state = state;
		this.bindTime = bindTime;
		this.toothCode = toothCode;
		this.qrcode = qrcode;
		this.time = time;
		this.note = note;
		this.version = version;
		this.ticketId = ticketId;
		this.qrcodeSerial = qrcodeSerial;
		this.mac = mac;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state == null ? null : state.trim();
    }

    public Date getBindTime() {
        return bindTime;
    }

    public void setBindTime(Date bindTime) {
        this.bindTime = bindTime;
    }

    public String getToothCode() {
        return toothCode;
    }

    public void setToothCode(String toothCode) {
        this.toothCode = toothCode == null ? null : toothCode.trim();
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode == null ? null : qrcode.trim();
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note == null ? null : note.trim();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version == null ? null : version.trim();
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId == null ? null : ticketId.trim();
    }

    public String getQrcodeSerial() {
        return qrcodeSerial;
    }

    public void setQrcodeSerial(String qrcodeSerial) {
        this.qrcodeSerial = qrcodeSerial == null ? null : qrcodeSerial.trim();
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac == null ? null : mac.trim();
    }

	public void preInsert() {
		 this.createTime = new Date();
	}

	public int getAuthtime() {
		return authtime;
	}

	public void setAuthtime(int authtime) {
		this.authtime = authtime;
	}

	public String getAuthOpenId() {
		return this.authOpenId;
	}

	public void setAuthOpenId(String authOpenId) {
		this.authOpenId = authOpenId;
	}

	public String getAuthState() {
		return authState;
	}

	public void setAuthState(String authState) {
		this.authState = authState;
	}
	
}