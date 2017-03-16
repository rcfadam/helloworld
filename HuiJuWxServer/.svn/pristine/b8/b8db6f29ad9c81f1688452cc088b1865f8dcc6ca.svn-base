package com.huiju.weixin.WeixinServer.bean;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/** 
 * @author zhuolin(zl@nbicc.com) 
 * @date 2015年10月29日
 * restful service response
 */
public class RestResponse implements Serializable{

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RestResponse(){}
	
	public RestResponse(int resultCode){
		this.result_code = resultCode;
	}

	public static RestResponse build(ResponseCode code){
		return build(code,null);
	}
	
	public static RestResponse build(ResponseCode code,Object data){
		RestResponse response = new RestResponse();
		response.setResult_code(code.ordinal());
		response.setData(data);
		return response;
	}
	
	@SerializedName("result_code")
	private int result_code;
	
	public int getResult_code() {
		return result_code;
	}

	public void setResult_code(int result_code) {
		this.result_code = result_code;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@SerializedName("data")
	private Object data;
	


	public RestResponse(int resultCode,Object resultObj){
		this.result_code = resultCode;
		this.data = resultObj;
	}
}
