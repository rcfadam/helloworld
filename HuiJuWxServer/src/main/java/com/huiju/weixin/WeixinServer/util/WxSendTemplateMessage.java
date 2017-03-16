package com.huiju.weixin.WeixinServer.util;

import java.util.ArrayList;
import java.util.List;


import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.WxMpTemplateMessage;

/**
 * 微信发送模板消息
 * @author rcfad
 *
 */
public class WxSendTemplateMessage {

	/**
	 * 创建模板消息  
	 * @param temlateData  模板消息中的参数值
	 * @return
	 */
	public  static WxMpTemplateMessage buildTemplateMessage(TemplateData temlateData){
		 WxMpTemplateMessage templateMessage = new WxMpTemplateMessage();
		 templateMessage.setTemplateId(temlateData.getTemplateId());
		 templateMessage.setTopColor(temlateData.getColor());
		 templateMessage.setToUser(temlateData.getToUser());
		 templateMessage.setUrl(temlateData.getUrl());
		 List<WxMpTemplateData> datas = new ArrayList<WxMpTemplateData>();
		 WxMpTemplateData first = new WxMpTemplateData();
		 first.setName("first");
		 first.setValue(temlateData.getFirst());
		 first.setColor(temlateData.getColor());
		 datas.add(first);
		 for(int i=0;i<temlateData.getKeyword().length;i++){
			 WxMpTemplateData name = new WxMpTemplateData();
			 name.setName("keyword"+(i+1));
			 name.setValue(temlateData.getKeyword()[i]);
			 name.setColor(temlateData.getColor());
			 datas.add(name);
		 }
		 WxMpTemplateData remark = new WxMpTemplateData();
		 remark.setName("remark");
		 remark.setValue(temlateData.getRemark());
		 remark.setColor(temlateData.getColor());
		 datas.add(remark);
		templateMessage.setDatas(datas);
		
		return templateMessage;
	}
	
	/**
	 * 发送模板消息
	 * @param wxMpService 微信公众号业务类对象 
	 * @param templateMessage  模板消息
	 */
	public static void sendTemplatemessage(WxMpService wxMpService,WxMpTemplateMessage templateMessage){
		try {
			wxMpService.templateSend(templateMessage);
		} catch (WxErrorException e) {
			e.printStackTrace();
		}
	}
	
	public static class TemplateData{
		/**
		 * 第一行
		 */
		private String first;
		/**
		 * 具体的业务data列表   不如 keyword1,keyword2.....
		 */
		private String[] keyword;
		
		/**
		 * 消息标记行
		 */
		private  String remark;
		/**
		 * 消息文本颜色值   
		 */
		private String color;
		private String toUser;
		private String templateId;
		private String url;
		public String getFirst() {
			return first;
		}
		public String getColor() {
			return this.color;
		}
		public void setFirst(String first) {
			this.first = first;
		}
		public String[] getKeyword() {
			return keyword;
		}
		public void setKeyword(String[] keyword) {
			this.keyword = keyword;
		}
		public String getRemark() {
			return remark;
		}
		public void setRemark(String remark) {
			this.remark = remark;
		}
		public void setColor(String color) {
			this.color = color;
		}
		public void setToUser(String toUser) {
			 this.toUser = toUser;
		}
		public String getToUser() {
			return toUser;
		}
		public void setTemplateId(String templateId) {
			 this.templateId = templateId;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public String getTemplateId() {
			return templateId;
		}
		public String getUrl() {
			return url;
		} 
		
		
	}
}
