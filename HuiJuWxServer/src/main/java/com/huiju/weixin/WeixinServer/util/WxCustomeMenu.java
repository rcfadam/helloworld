package com.huiju.weixin.WeixinServer.util;

import java.io.IOException;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huiju.weixin.WeixinServer.conf.StartupConfig;

import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.util.http.SimplePostRequestExecutor;
import me.chanjar.weixin.mp.api.WxMpService;



/**
 * 创建自定义菜单
 * @author rcfad
 *
 */
@Component
public class WxCustomeMenu {
	@Autowired
	private StartupConfig startupConfig;
	//点击微信菜单按钮重定向到自定义对控制层来处理数据后再进入到响应的页面
	private String redirectUrl = "http://www.huiju.biz/webcallback.do";
	//Log4JLogger log = new Log4JLogger();
	
	/**
	 * 创建自定义菜单
	 * @param accessToken
	 * @throws IOException 
	 */
	public void createCustomeMenu(String accessToken) throws IOException{
		WxMpService wxMpService = startupConfig.getWxMpService();
		redirectUrl="http://live-smart.com.cn/index.html";
		redirectUrl = URLEncoder.encode(redirectUrl,"utf-8");
		//String tempUrl = WxConfig.USER_AUTHORIZE.replace("APPID", WxConfig.APPID).replace("REDIRECTURL", redirectUrl);
		String tempUrl = WxConfig.USER_AUTHORIZE.replace("APPID", "wxf410780b57e7f0ad").replace("REDIRECTURL",redirectUrl );
		//添加锁
		String addLockUrl = tempUrl.replace("STATE","addLock" );
		//锁列表
		String lockListUrl =tempUrl.replace("STATE","lockList" );
		//添加钥匙
		String addKeyUrl = tempUrl.replace("STATE","addKey" );
		//删除钥匙
		String delKeyUrl = tempUrl.replace("STATE","delKey" );
		//启动或停用钥匙
		String startOrStopKeyUrl = tempUrl.replace("STATE","startOrStopKey" );
		//联系人
        /**自定义菜单 		
         *  我的门锁 
         *     添加锁
         *     锁列表
         *  我的钥匙
         *     添加钥匙
         *     删除钥匙
         *     启动/停用钥匙
         *   我的账户
         *      紧急联系方式         
         */
		StringBuffer buffer = new StringBuffer();
		buffer.append("{" +
				"\"button\":[" +
				"{" +
				"\"name\":\"我的门锁\"," +
				"\"sub_button\":[" +
				     "{\"type\":\"view\",\"name\":\"添加锁\",\"url\":\""+addLockUrl+"\"}," +
				     "{\"type\":\"view\",\"name\":\"锁列表\",\"url\":\""+lockListUrl+"\"}" +
				  "]" +
				 "}," +
				"{" +
				  "\"name\":\"我的钥匙\"," +
				  "\"sub_button\":[" +
				     "{\"type\":\"view\",\"name\":\"添加钥匙\",\"url\":\""+addKeyUrl+"\"}," +
				     "{\"type\":\"view\",\"name\":\"删除钥匙\",\"url\":\""+delKeyUrl+"\"}," +
				     "{\"type\":\"view\",\"name\":\"启动/停用钥匙\",\"url\":\""+startOrStopKeyUrl+"\"}" +
				    "]" +
				 "}," +
				 "{\"name\":\"我的账户\"," +
				   "\"sub_button\":[" +
				      "{\"type\":\"click\",\"name\":\"紧急联系人\",\"key\":\"content\"}" +
				   "]" +
				  "}" +
				"]" +
			 "}");
		System.out.println(buffer.toString());
		String url = WxConfig.CREATE_WX_MENU.replace("ACCESSTOKEN",accessToken);
	    String result="";
		try {
			result = wxMpService.execute(new SimplePostRequestExecutor(), url,
						buffer.toString());
		} catch (WxErrorException e) {
			e.printStackTrace();
		}
	  System.out.println(result);
	}
	
	
	/**
	 * 创建自定义菜单
	 * @param accessToken
	 * @throws IOException 
	 */
	public void createCustomeMenu2(String accessToken) throws IOException{
		WxMpService wxMpService = startupConfig.getWxMpService();
		redirectUrl="http://huijuwx.com/mui/index.html";
		//redirectUrl="http://live-smart.com.cn/index.html";
		redirectUrl = URLEncoder.encode(redirectUrl,"utf-8");
		//String tempUrl = WxConfig.USER_AUTHORIZE.replace("APPID", WxConfig.APPID).replace("REDIRECTURL", redirectUrl);
		String tempUrl = WxConfig.USER_AUTHORIZE.replace("APPID", "wxb4ba14562eb9a765").replace("REDIRECTURL",redirectUrl );
		//锁列表
		String lockListUrl =tempUrl.replace("STATE","devicelist" );
		String helpurl = WxConfig.USER_AUTHORIZE.replace("APPID", "wxb4ba14562eb9a765").replace("REDIRECTURL","http://live-smart.com.cn/help.txt" ).replace("STATE","help" );
		//联系人
        /**自定义菜单 		
         *  我的门锁 
         *  用户帮助       
         */
		StringBuffer buffer = new StringBuffer();
		buffer.append("{" +
				"\"button\":[" +
				"{" +
				"\"name\":\"我的门锁\"," +
				"\"type\":\"view\"," +
				"\"url\":\""+lockListUrl+"\"}," +
				"{\"name\":\"使用手册\"," +
				"\"type\":\"view\"," +
				"\"url\":\""+helpurl+"\"}" +
				"]" +
			 "}");
		System.out.println(buffer.toString());
		String url = WxConfig.CREATE_WX_MENU.replace("ACCESSTOKEN",accessToken);
		  String result="";
			try {
				result = wxMpService.execute(new SimplePostRequestExecutor(), url,
							buffer.toString());
			} catch (WxErrorException e) {
				e.printStackTrace();
			}
	  System.out.println(result);
	}
	 
}
