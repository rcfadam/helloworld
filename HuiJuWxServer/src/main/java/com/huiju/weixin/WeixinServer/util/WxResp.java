package com.huiju.weixin.WeixinServer.util;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class WxResp implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@SerializedName("base_resp")
	private BaseResp baseResp;
	
	public BaseResp getBaseResp() {
		return baseResp;
	}

	public void setBaseResp(BaseResp baseResp) {
		this.baseResp = baseResp;
	}

	public static class BaseResp{
		private int errcode;
		
		private String errmsg;

		public int getErrcode() {
			return errcode;
		}

		public void setErrcode(int errcode) {
			this.errcode = errcode;
		}

		public String getErrmsg() {
			return errmsg;
		}

		public void setErrmsg(String errmsg) {
			this.errmsg = errmsg;
		}
	}

}
