package com.huiju.weixin.WeixinServer.bean;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class WxMpDeviceJsonMessage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@SerializedName("device_id")
	private String deviceId;
	
	@SerializedName("device_type")
	private String deviceType;
	
	@SerializedName("msg_id")
	private long msgId;
	
	@SerializedName("msg_type")
	private String msgType;
	
	@SerializedName("create_time")
	private long createTime;

	@SerializedName("open_id")
	private String openId;
	
	@SerializedName("session_id")
	private long sessionId;
	
	@SerializedName("content")
	private String content;
	
	@SerializedName("qrcode_suffix_data")
	private String qrcodeSuffixData;

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public long getMsgId() {
		return msgId;
	}

	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public long getSessionId() {
		return sessionId;
	}

	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getQrcodeSuffixData() {
		return qrcodeSuffixData;
	}

	public void setQrcodeSuffixData(String qrcodeSuffixData) {
		this.qrcodeSuffixData = qrcodeSuffixData;
	}
}
