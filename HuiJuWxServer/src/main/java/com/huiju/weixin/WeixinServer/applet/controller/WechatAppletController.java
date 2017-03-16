package com.huiju.weixin.WeixinServer.applet.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.huiju.weixin.WeixinServer.conf.StartupConfig;

import me.chanjar.weixin.common.util.StringUtils;
import me.chanjar.weixin.mp.api.WxMpService;


/**
 * 处理微信小程序的控制层接口
 * @author rencf
 *
 */
@Controller
public class WechatAppletController {

	@Autowired
	private StartupConfig startupConfig;
	
	private Logger logger = Logger.getLogger(WechatAppletController.class);
	
	@RequestMapping("/wechatApplet")
	public void execute(HttpServletRequest request,HttpServletResponse response) throws IOException{
		    WxMpService wxMpService = startupConfig.getWxMpService();
			String signature = request.getParameter("signature");
		    String nonce = request.getParameter("nonce");
		    String timestamp = request.getParameter("timestamp");
		    String echostr = request.getParameter("echostr");
		    if(wxMpService.checkSignature(timestamp, nonce, signature)){
		    	if(!StringUtils.isNotBlank(echostr)){
		    		 String encryptType = StringUtils.isBlank(request.getParameter("encrypt_type")) ?
		 		            "raw" :request.getParameter("encrypt_type");
		 		        if ("raw".equals(encryptType)) {
		 		        	this.wxAppletRawMessage(request,response);
		 		            return;
		 		        }
		 		        if ("aes".equals(encryptType)) {
		 		          //是aes加密的消息
		 		        	this.wxAppletAesMessage(request,response);
		 		          return;
		 		        }
		 		        response.getWriter().println("不可识别的加密类型");
		 		        return;
		    	}else{
		    		response.getWriter().print(echostr);
		    	}
		    }else{
		    	logger.error("check error");
		    }
	}

	private void wxAppletAesMessage(HttpServletRequest request, HttpServletResponse response) {
		 
		
	}

	/**
	 * 处理微信小程序明文消息
	 * @param request
	 * @param response
	 */
	private void wxAppletRawMessage(HttpServletRequest request, HttpServletResponse response) {
		 
		
	}
	
}
