package com.huiju.weixin.WeixinServer.util;

import java.util.UUID;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy(false)
public class IdGen {

	/**
	 * 封装JDK自带的UUID, 通过Random数字生成, 中间无-分割.
	 */
	public static String uuid() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	/**
	 * 获取二维码序号
	 * @param id
	 * @return
	 */
	public static String createQrSerial(int id) {
		String idStr = String.valueOf(id);
		String tempStr = ""; 
		for(int i = 0;i<8-idStr.length();i++){
			 tempStr+="0";
		 } 
		tempStr+=idStr;
		return tempStr;
	}
	
}
