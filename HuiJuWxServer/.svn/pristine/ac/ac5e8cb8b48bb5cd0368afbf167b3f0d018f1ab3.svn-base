package com.huiju.weixin.WeixinServer.bean;

import java.util.UUID;


/***
 * 接入jsapi 需要配置的三个参数的封装类
 * @author rcfad
 *
 */
public class JsapiConfigParam {

	private String timestamp;//时间戳
	
	private String nonce_str;//随机数
	
	private String signature; //签名
	
	private String url;//引入jsapi的页面地址
	
	private String jsapi_ticket; //引入jsapi的接口凭证
	
	
	public JsapiConfigParam() {
		 this.timestamp = Long.toString(System.currentTimeMillis());
		 this.nonce_str = UUID.randomUUID().toString();
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getNonce_str() {
		return nonce_str;
	}

	public void setNonce_str(String nonce_str) {
		this.nonce_str = nonce_str;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getJsapi_ticket() {
		return jsapi_ticket;
	}

	public void setJsapi_ticket(String jsapi_ticket) {
		this.jsapi_ticket = jsapi_ticket;
	}
	
	
	
}
