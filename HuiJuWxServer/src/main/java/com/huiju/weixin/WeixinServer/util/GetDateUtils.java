package com.huiju.weixin.WeixinServer.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.springframework.beans.propertyeditors.LocaleEditor;

public class GetDateUtils {

	public static String getDateStr(){
		return "";
	}
	
	public static Date getDateByStr(String str) {
		if("".equals(str)||str==null){
			return new Date();
		}
		SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aaa",Locale.US);
		 try {
			return format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		 return new Date();
	}
	
	public static Timestamp StrTransferTimestamp(String string){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Timestamp timestamp = null;
		 try {
			Date date =  format.parse(string);
		    timestamp = Timestamp.valueOf(format.format(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		 
		return timestamp;
	}
	
	/**
	 * 获取时间片
	 * @param time  秒数
	 * @return
	 */
	public static Timestamp getTimestamp(int time){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		long today = date.getTime();
		long ss = today+time*1000;
		Date date2 = new Date(ss);
		return StrTransferTimestamp(format.format(date2));	
	}

	/**
	 * timestamp 转换成string  yyyy-MM-dd HH:mm:ss
	 * @param time
	 * @return
	 */
	public static String timestampToStr(Timestamp time) {
		return timestampToStr(time, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * timestamp 转换成string  yyyy-MM-dd HH:mm
	 * @param time
	 * @param pattern
	 * @return
	 */
	public static String timestampToStr(Timestamp time, String pattern) {
		 SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(new Date(time.getTime()));
	}
	
	 
}
