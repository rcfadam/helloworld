package com.huiju.weixin.WeixinServer.bean;
/** 
 * @author zhuolin(zl@nbicc.com) 
 * @date 2014年8月27日
 * 响应码 
 */
public enum ResponseCode {
	SUCCESS,
	REQUEST_FAIL,
	LOGIN_FAIL,
	NOT_LOGIN,
	REGISTER_FAIL,
	REQUEST_ILLEGAL,
	WRONG_PSW,
	VERIFY_FAIL,
	ALREADY_BIND,
	NO_AUTHORIZATION,
	NEED_UPGRADE,
	UNNEED_UPGRADE,
	DEVICE_NOT_EXIST,
	DEVICE_NOT_BIND,
	APP_NOT_EXIST,
	CONFIG_UNNEED_UPDATE,
	CONFIG_NEED_UPDATE,
	CONFIG_NOT_EXIST,
	SERVER_INNER_ERROR,
	USER_NOT_EXIST,
	DEVICE_OFFLINE,
	FILE_NOT_EXIST,
	USER_EXIST,
	EMAIL_EXIST,
	PHONE_EXIST,
	TIME_OUT,
	MAIN_SOFT_NEED_UPGRADE,
	SUB_SOFT_NEED_UPGRADE,
	USER_NOT_BIND,
	ILLEGAL_DEVICEKEY,
	ILLEGAL_PSN,
	LICENSE_ULTRALIMIT,
	TOKEN_ERR,
	SHARECODE_MISS,
	SMS_ERR,
	PSW_FORMAT_ERR,
	DUPLICATE_NAMED
	;
	
	
	private ResponseCode(){
	}

	public static ResponseCode valueOf(int ordinal){
		if (ordinal < 0 || ordinal >= values().length) {  
            throw new IndexOutOfBoundsException("Invalid ordinal");  
        }  
        return values()[ordinal];  
       }  

}
