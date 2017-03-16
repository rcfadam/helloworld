package com.huiju.weixin.WeixinServer.conf;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.WxMenu;
import me.chanjar.weixin.common.bean.WxMenu.WxMenuButton;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutTextMessage;

@Component
public class StartupConfig implements ApplicationListener<ContextRefreshedEvent>{

	private static final Logger logger = Logger.getLogger(StartupConfig.class);
	private WxMpConfigStorage wxMpConfigStorage;
	private WxMpService wxMpService;
	private WxMpMessageRouter wxMpMessageRouter;
	public WxMpConfigStorage getWxMpConfigStorage() {
		return wxMpConfigStorage;
	}

	public WxMpService getWxMpService() {
		return wxMpService;
	}

	public WxMpMessageRouter getWxMpMessageRouter() {
		return wxMpMessageRouter;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		InputStream is1 = ClassLoader.getSystemResourceAsStream("config.xml");
	    HJWxMpInMemoryConfigStorage config = HJWxMpInMemoryConfigStorage.fromXml(is1);
	    wxMpConfigStorage = config;
	    wxMpService = new WxMpServiceImpl();
	    wxMpService.setWxMpConfigStorage(config);
	    wxMpMessageRouter = new WxMpMessageRouter(wxMpService);
	    WxMpMessageHandler handler1 = new WxMpMessageHandler() {
			@Override
			public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context,
					WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
				logger.info("openid: " + wxMessage.getFromUserName());
				Calendar cal = Calendar.getInstance();
				int month = cal.get(Calendar.MONTH) + 1;
				int date = cal.get(Calendar.DATE);
				  WxMpXmlOutTextMessage m = WxMpXmlOutMessage.TEXT().content("******************************************************\n绑定锁\n"+month+"月"+date+"日\n请点击扫一扫扫描管理钥匙的二维码，绑定锁芯。\n<a href=\"http://live-smart.com.cn/index.html?openid="+wxMessage.getFromUserName()+"#/addlock\">扫一扫</a>\n******************************************************").fromUser(wxMessage.getToUserName())
	              .toUser(wxMessage.getFromUserName()).build();
	          return m;
			}

	      };
	      
	      WxMpMessageHandler handler2 = new WxMpMessageHandler() {
				@Override
				public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context,
						WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
					logger.info("openid: " + wxMessage.getFromUserName());
					Calendar cal = Calendar.getInstance();
					int month = cal.get(Calendar.MONTH) + 1;
					int date = cal.get(Calendar.DATE);
					  WxMpXmlOutTextMessage m = WxMpXmlOutMessage.TEXT().content("******************************************************\n锁列表\n"+month+"月"+date+"日\n请点击获取锁列表。\n<a href=\"http://live-smart.com.cn/index.html?openid="+wxMessage.getFromUserName()+"#/locklist\">锁列表</a>\n******************************************************").fromUser(wxMessage.getToUserName())
		              .toUser(wxMessage.getFromUserName()).build();
		          return m;
				}

		      };
		      
	      WxMpMessageHandler handler3 = new WxMpMessageHandler() {
				@Override
				public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context,
						WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
					logger.info("openid: " + wxMessage.getFromUserName());
					Calendar cal = Calendar.getInstance();
					int month = cal.get(Calendar.MONTH) + 1;
					int date = cal.get(Calendar.DATE);
					  WxMpXmlOutTextMessage m = WxMpXmlOutMessage.TEXT().content("******************************************************\n绑定钥匙\n"+month+"月"+date+"日\n请点击扫一扫扫描管理钥匙的二维码，绑定钥匙。\n<a href=\"http://live-smart.com.cn/index.html?openid="+wxMessage.getFromUserName()+"#/addkey\">扫一扫</a>\n******************************************************").fromUser(wxMessage.getToUserName())
		              .toUser(wxMessage.getFromUserName()).build();
		          return m;
				}

		      };
	
	    wxMpMessageRouter.rule().async(false).msgType(WxConsts.XML_MSG_EVENT).event(WxConsts.BUTTON_CLICK).eventKey("W1").handler(handler1).end()
	    .rule().async(false).msgType(WxConsts.XML_MSG_EVENT).event(WxConsts.BUTTON_CLICK).eventKey("W2").handler(handler2).end()
	    .rule().async(false).msgType(WxConsts.XML_MSG_EVENT).event(WxConsts.BUTTON_CLICK).eventKey("W3").handler(handler3).end();
//	    createMenu();
	}
	
	public void createMenu(){
		WxMenu wxMenu = new WxMenu();
		List<WxMenuButton> buttons = createButtons();
		wxMenu.setButtons(buttons);
		try {
			wxMpService.menuCreate(wxMenu);
		} catch (WxErrorException e) {
			e.printStackTrace();
		}
	}

	public List<WxMenuButton> createButtons(){
		List<WxMenuButton> buttons = new ArrayList<WxMenuButton>();
		WxMenuButton oneLevel1 = new WxMenuButton();
		oneLevel1.setName("我的门锁");
		List<WxMenuButton> subButtons1 = new ArrayList<WxMenuButton>();
		WxMenuButton twoLevel1 = new WxMenuButton();
		twoLevel1.setType("click");
		twoLevel1.setName("添加锁");
		twoLevel1.setKey("W1");
		WxMenuButton twoLevel2 = new WxMenuButton();
		twoLevel2.setType("click");
		twoLevel2.setName("锁列表");
		twoLevel2.setKey("W2");
		subButtons1.add(twoLevel1);
		subButtons1.add(twoLevel2);
		oneLevel1.setSubButtons(subButtons1);
		
		WxMenuButton oneLevel2 = new WxMenuButton();
		oneLevel2.setName("我的钥匙");
		List<WxMenuButton> subButtons2 = new ArrayList<WxMenuButton>();
		WxMenuButton twoLevel3 = new WxMenuButton();
		twoLevel3.setType("click");
		twoLevel3.setName("添加钥匙");
		twoLevel3.setKey("W3");
		WxMenuButton twoLevel4 = new WxMenuButton();
		twoLevel4.setType("click");
		twoLevel4.setName("删除钥匙");
		twoLevel4.setKey("W4");
		WxMenuButton twoLevel5 = new WxMenuButton();
		twoLevel5.setType("click");
		twoLevel5.setName("启用/停用钥匙");
		twoLevel5.setKey("W5");
		subButtons2.add(twoLevel3);
		subButtons2.add(twoLevel4);
		subButtons2.add(twoLevel5);
		oneLevel2.setSubButtons(subButtons2);
		
		WxMenuButton oneLevel3 = new WxMenuButton();
		oneLevel3.setName("我的账户");
		List<WxMenuButton> subButtons3 = new ArrayList<WxMenuButton>();
		WxMenuButton twoLevel6 = new WxMenuButton();
		twoLevel6.setType("click");
		twoLevel6.setName("紧急联系方式");
		twoLevel6.setKey("W6");
		subButtons3.add(twoLevel6);
		oneLevel3.setSubButtons(subButtons3);
		
		buttons.add(oneLevel1);
		buttons.add(oneLevel2);
		buttons.add(oneLevel3);
		
		return buttons;
	}
}
