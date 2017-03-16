package com.huiju.weixin.WeixinServer.util;

import java.io.ByteArrayOutputStream;


/***
 * 处理微信设备消息的工具类
 * @author rcfad
 *
 */
public class WxDeviceMessageUtil {
  
	/**
	 * 接收蓝牙设备消息的消息类型
	 */
	 public static String REQ_DEVICE_MSG_TYPE = "device_text";
	
	 /**
	  *蓝牙设备绑定解绑事件的消息类型 
	  */
	 public static String REQ_DEVICE_MSG_EVENT = "device_event";
	 
	 /**	
	  * 蓝牙设备绑定事件
	  */
	 public static String REQ_DEVICE_EVENT_BIND = "bind";
	 
	 /**
	  * 蓝牙设备解绑事件
	  */
	 public static String REQ_DEVICE_EVENT_UNBIND = "unbind";
	 
	/**
	 * byte数组转换成16进制
	 * @param b
	 * @return
	 */
	 public static String BytesToHexString(byte[] b) { 
		 String ret = ""; 
		 for (int i = 0; i < b.length; i++) { 
		 String hex = Integer.toHexString(b[i] & 0xFF); 
		 if (hex.length() == 1) { 
		 hex = '0' + hex; 
		 } 
		 ret += hex.toUpperCase(); 
		 } 
		 return ret; 
	 } 
	 /** 
	 * 将16进制数字解码成字符串,适用于所有字符（包括中文） 
	 */ 
	 public static String decode(String bytes,String hexString) 
	 { 
	 ByteArrayOutputStream baos=new ByteArrayOutputStream(bytes.length()/2); 
	 //将每2位16进制整数组装成一个字节 
	 for(int i=0;i<bytes.length();i+=2) 
	 baos.write((hexString.indexOf(bytes.charAt(i))<<4 |hexString.indexOf(bytes.charAt(i+1)))); 
	 return new String(baos.toByteArray()); 
	 } 
}
