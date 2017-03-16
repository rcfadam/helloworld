package com.huiju.weixin.WeixinServer.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huiju.weixin.WeixinServer.bean.ResponseCode;
import com.huiju.weixin.WeixinServer.bean.RestResponse;
import com.huiju.weixin.WeixinServer.bean.WxMpDeviceJsonMessage;
import com.huiju.weixin.WeixinServer.conf.StartupConfig;
import com.huiju.weixin.WeixinServer.model.KeyTb;
import com.huiju.weixin.WeixinServer.model.LockKeyTb;
import com.huiju.weixin.WeixinServer.model.LockTb;
import com.huiju.weixin.WeixinServer.model.TempTb;
import com.huiju.weixin.WeixinServer.model.UserLockTb;
import com.huiju.weixin.WeixinServer.model.UserTb;
import com.huiju.weixin.WeixinServer.service.LockKeyService;
import com.huiju.weixin.WeixinServer.service.UserLockService;
import com.huiju.weixin.WeixinServer.service.UserService;
import com.huiju.weixin.WeixinServer.service.UserService.WxBindDevice.Device;
import com.huiju.weixin.WeixinServer.util.GetDateUtils;
import com.huiju.weixin.WeixinServer.util.WxSendTemplateMessage;
import com.huiju.weixin.WeixinServer.util.WxSendTemplateMessage.TemplateData;

import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.util.StringUtils;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpTemplateMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutNewsMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutNewsMessage.Item;
import me.chanjar.weixin.mp.bean.WxMpXmlOutTextMessage;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import me.chanjar.weixin.mp.util.json.WxMpGsonBuilder;

@Controller
public class BusinessController {
	private static final Logger logger = Logger.getLogger(BusinessController.class);
	
	@Autowired
	private StartupConfig startupConfig;
	
	@Autowired
	private UserLockService userLockService;

	@Autowired
	private LockKeyService lockKeyService;

	@Autowired
	private UserService userService;
	
	@RequestMapping("/")
	@ResponseBody
	public void handleAll(HttpServletRequest request,HttpServletResponse response) throws IOException, WxErrorException {
		    WxMpService wxMpService = startupConfig.getWxMpService();
			String signature = request.getParameter("signature");
		    String nonce = request.getParameter("nonce");
		    String timestamp = request.getParameter("timestamp");
		    String echostr = request.getParameter("echostr");
		    if (!wxMpService.checkSignature(timestamp, nonce, signature)) {
		      // 消息签名不正确，说明不是公众平台发过来的消息
		    	logger.info("check false.");
		      return;
		    }
		    if (StringUtils.isNotBlank(echostr)) {
		      // 说明是一个仅仅用来验证的请求，回显echostr
		    	logger.info("check true.");
		      return;
		    }
		    String encryptType = StringUtils.isBlank(request.getParameter("encrypt_type")) ?
		            "raw" :request.getParameter("encrypt_type");
		        if ("raw".equals(encryptType)) {
		        	  this.wxRawMessage(request, response);
		            return;
		        }
		        if ("aes".equals(encryptType)) {
		          //是aes加密的消息
		          return;
		        }
		        response.getWriter().println("不可识别的加密类型");
		        return;
	}
	
	
	private void wxRawMessage(HttpServletRequest request, HttpServletResponse response) throws IOException {
		 String body = IOUtils.toString(request.getInputStream(), "UTF-8");
		 logger.debug("------body="+body);
		 WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(body);
		 Item item = new Item();
		 item.setTitle("你好，慧居欢迎你");
		 item.setPicUrl("http://live-smart.com.cn/image/xiaowang1.png");
		 item.setUrl("http://live-smart.com.cn/help.txt");
		 if("event".equals(inMessage.getMsgType())){
			 if("subscribe".equalsIgnoreCase(inMessage.getEvent())){
				 //订阅
				 String eventKey  = inMessage.getEventKey();
				 String qrcodeSerial = eventKey.substring(eventKey.indexOf("qrscene_")+8, eventKey.length());
				 if(qrcodeSerial.length()>0){
					 this.remoteAuthorized(qrcodeSerial, inMessage.getFromUserName());
				 }
				 //关注推送图文消息
				 WxMpXmlOutNewsMessage  outNewsMessage = new WxMpXmlOutNewsMessage();
				 outNewsMessage.addArticle(item);
				 outNewsMessage.setFromUserName(inMessage.getToUserName());
				 outNewsMessage.setToUserName(inMessage.getFromUserName());
				 outNewsMessage.setCreateTime(inMessage.getCreateTime());
		         response.getWriter().write(outNewsMessage.toXml());
			 }else if("unsubscribe".equalsIgnoreCase(inMessage.getEvent())){
				 //取消订阅
				List<Device> deviceilst =  userService.getWxBindDevice(inMessage.getToUserName(), "");
				for(Device device :deviceilst){
					userLockService.compelUnbindDevice(inMessage.getToUserName(), device.getDevice_id());
				}
			 }else if("SCAN".equalsIgnoreCase(inMessage.getEvent())){
				 //已订阅
				this.remoteAuthorized(inMessage.getEventKey(), inMessage.getFromUserName());
			 }
		 }else{
			 WxMpXmlOutTextMessage outTextMessage = new WxMpXmlOutTextMessage();
			 outTextMessage.setFromUserName(inMessage.getToUserName());
			 outTextMessage.setToUserName(inMessage.getFromUserName());
			 outTextMessage.setContent("你好");
			 outTextMessage.setCreateTime(inMessage.getCreateTime());
			 response.getWriter().write(outTextMessage.toXml());
		 }
	}


	@RequestMapping("/device")
	@ResponseBody
	public void handleAllDevice(HttpServletRequest request,HttpServletResponse response) throws IOException, WxErrorException {
		    WxMpService wxMpService = startupConfig.getWxMpService();
		    String signature = request.getParameter("signature");
		    String nonce = request.getParameter("nonce");
		    String timestamp = request.getParameter("timestamp");
		    String echostr = request.getParameter("echostr");
		    if (!wxMpService.checkSignature(timestamp, nonce, signature)) {
		      // 消息签名不正确，说明不是公众平台发过来的消息
		    	logger.info("check false.");
		        response.getWriter().print("非法请求");
		        return;
		    }
		    if (StringUtils.isNotBlank(echostr)) {
		      // 说明是一个仅仅用来验证的请求，回显echostr
		    	logger.info("check true.");
		        response.getWriter().print(echostr);
		        return;
		    }
		    String encryptType = StringUtils.isBlank(request.getParameter("encrypt_type")) ?
		            "raw" : request.getParameter("encrypt_type");
		        if ("raw".equals(encryptType)) {
		          // 明文传输的消息
		          this.wxDeviceRawMessage(request, response);
		          return;
		        }
		        if ("aes".equals(encryptType)) {
		          // 是aes加密的消息
		          return;
		        }
		        response.getWriter().println("不可识别的加密类型");
		        return;
	}
	
	@GetMapping("/getSysInfo")
	@ResponseBody
	public Map<String, Object> getSysInfo(HttpServletRequest request) throws WxErrorException{
		WxMpService wxMpService = startupConfig.getWxMpService();
		String openId = request.getParameter("openid");
		logger.info("openid from request: " + openId);
		WxJsapiSignature wxSignature = wxMpService.createJsapiSignature("http://live-smart.com.cn/mui/index.html?openid="+openId);
		Map<String, Object> data = new HashMap<String,Object>();
		if(wxSignature != null){
			long timestamp = wxSignature.getTimestamp();
			String noncestr = wxSignature.getNoncestr();
			String signature = wxSignature.getSignature();
			logger.info("timestamp: " + timestamp + ", noncestr: " + noncestr + ", signature: " + signature);
			data.put("timestamp", timestamp);
			data.put("noncestr", noncestr);
			data.put("signature", signature);
		}
		return data;
	}
	
	@GetMapping("/getSysInfoByUrl")
	@ResponseBody
	public Map<String,Object> getSysInfoByUrl(HttpServletRequest request) throws WxErrorException{
		WxMpService wxMpService = startupConfig.getWxMpService();
		String url = request.getParameter("url");
		logger.info("url from request: " + url);
		WxJsapiSignature wxSignature = wxMpService.createJsapiSignature(url);
		Map<String, Object> data = new HashMap<String,Object>();
		if(wxSignature != null){
			long timestamp = wxSignature.getTimestamp();
			String noncestr = wxSignature.getNoncestr();
			String signature = wxSignature.getSignature();
			logger.info("timestamp: " + timestamp + ", noncestr: " + noncestr + ", signature: " + signature);
			data.put("timestamp", timestamp);
			data.put("noncestr", noncestr);
			data.put("signature", signature);
		}
		return data;
	}
	
	
	
	@GetMapping("/getOpenId")
	@ResponseBody
	public Map<String, Object> getOpenId(HttpServletRequest request) throws WxErrorException{
		WxMpService wxMpService = startupConfig.getWxMpService();
		String code = request.getParameter("code");
		logger.info("code from request: " + code);
		WxMpOAuth2AccessToken wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
		Map<String, Object> data = new HashMap<String,Object>();
		if(wxMpOAuth2AccessToken != null){
			WxMpUser user = new WxMpUser();
			UserTb  usertb = userService.checkUser(wxMpOAuth2AccessToken.getOpenId());
			if(usertb!=null){
				if(usertb.getNickname()==null||usertb.getNickname().equals("")){
					user = wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken,null);
					usertb.setNickname(user.getNickname());
					usertb.setUserId(user.getOpenId());
					usertb.setOpenId(user.getOpenId());
					usertb.setHeadImgUrl(user.getHeadImgUrl());
					userService.updateUser(usertb);
				}
			}else{
				user = wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken,null);
				usertb = new UserTb();
				usertb.setNickname(user.getNickname());
				usertb.setUserId(user.getOpenId());
				usertb.setOpenId(user.getOpenId());
				usertb.setHeadImgUrl(user.getHeadImgUrl());
				userService.addUser(usertb);
			}
			data.put("user", usertb);
		}
		return data;
	}
	
	
	@RequestMapping("/devicelist")
	@ResponseBody
	public RestResponse getLockList(@RequestParam(required = true) String openId){
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		List<UserLockTb> locks = userLockService.getLockByOpenId(openId);
		if(!CollectionUtils.isEmpty(locks)){
			for(int index = 0;index<locks.size();index++){
				UserLockTb lock  = locks.get(index);
				logger.debug("userlock:"+JSONObject.toJSONString(lock));
				Map<String, Object> data = new HashMap<String,Object>();
			    List<LockKeyTb> keyList = lockKeyService.findlockkeyByLockId(lock.getLockId(),lock.getOpenId());
				data.put("keyList", keyList);
				data.put("lockId", lock.getLockId());
				data.put("roleId",lock.getRoleId());
				data.put("locktb", lock.getLockTb());
				data.put("usertb", lock.getUserTb());
				result.add(data);
			}
		}
	    return RestResponse.build(ResponseCode.SUCCESS, result);
	}
	
	@RequestMapping("/unbindLock")
	@ResponseBody
	public RestResponse unbindLock(@RequestParam(required = true)String ticket,@RequestParam(required = true) String lockId,@RequestParam(required = true) String openId,@RequestParam(required=true) String deviceId){
		if(userLockService.deleteByOpenIdAndLockId(openId, lockId)){
			List<KeyTb> keytbs = lockKeyService.findKeyListByLockId(lockId, 1);
			for (KeyTb keyTb : keytbs) {
				if(keyTb.getType()==1&&keyTb.getDeviceId()!=null){
					int count = lockKeyService.findLockKeyRelationCount(keyTb.getKeyId(),openId);
					if(count==0){
						userLockService.compelUnbindDevice(openId, keyTb.getDeviceId());
					}
				}
			}
		}
		return RestResponse.build(ResponseCode.SUCCESS);
	}
	
	@RequestMapping("/getkey")
	@ResponseBody
	public RestResponse getKey(@RequestParam String keyId){
		KeyTb key = lockKeyService.getKeyByKeyId(keyId);
		return RestResponse.build(ResponseCode.SUCCESS, key);
	}
	@RequestMapping("/getKeyByqrcodeserial")
	@ResponseBody
	public RestResponse getKeyByqrcodeserial(@RequestParam String qrcodeSerial){
		KeyTb key = lockKeyService.findKeyTbByQrcodeSerial(qrcodeSerial);
		return RestResponse.build(ResponseCode.SUCCESS, key);
	}
	
	@RequestMapping("/getKeyBydeviceId")
	@ResponseBody
	public RestResponse getKeyByDeviceId(@RequestParam String deviceId){
		KeyTb key = lockKeyService.getKeyTbByDeviceId(deviceId);
		return RestResponse.build(ResponseCode.SUCCESS, key);
	}
	
	/**
	 * 获取钥匙的有效时间
	 * @param deviceId
	 * @return
	 */
	@RequestMapping("/validtime")
	@ResponseBody
	public RestResponse validTime(@RequestParam String deviceId){
		KeyTb key = lockKeyService.getKeyTbByDeviceId(deviceId);
		if(key.getTime().getTime()>new Date().getTime()){
			return RestResponse.build(ResponseCode.SUCCESS, key.getAuthtime());
		}else{
			return RestResponse.build(ResponseCode.SUCCESS, -1);
		}
	}
	
	@RequestMapping("/addKey")
	@ResponseBody
	public RestResponse addKey(@RequestParam(required = true)String position,@RequestParam(required = true)String keyId,@RequestParam(required = true)String lockId,@RequestParam String openId,@RequestParam String ticket){
		LockKeyTb lockkey = lockKeyService.findLockKeyByLockIdAndKeyId(lockId, keyId); 
		 if(lockkey==null){
			 lockkey  = new LockKeyTb();
		 }
		KeyTb key = lockKeyService.getKeyByKeyId(keyId);
		if(key.getKeyId()==null){
			//设备不存在  数据表中未添加
			return RestResponse.build(ResponseCode.DEVICE_NOT_EXIST);
		}
		key.setPosition(Integer.parseInt(position));
		key.setState("1");//1启动 2禁止 首次添加 默认启动
		lockkey.setKeyId(keyId);
		lockkey.setLockId(lockId);
		lockkey.setIsFlag("1");
		lockkey.setState("1");
		lockkey.setPosition(Integer.parseInt(position));
		if(lockKeyService.addkey(key,lockkey)){
			if(key.getType() == 1){
				//如果是蓝牙钥匙绑定钥匙
			  if(!userService.checkWxDeviceByOpenId(openId, key.getDeviceId())){
				  userLockService.bindDevice(openId,key.getDeviceId(),ticket);
			  }
			}
			lockKeyService.deleteByOpenIdAndKeyId(keyId,openId);
			
		};
		return RestResponse.build(ResponseCode.SUCCESS);
	}
	
	@RequestMapping("/removeKey")
	@ResponseBody
	public RestResponse removeKey(@RequestParam(required = true)String lockId,@RequestParam(required = true)String keyId,@RequestParam String openId){
		List<LockKeyTb> lockKeyTbs = lockKeyService.findLockKeyByKeyId(keyId,openId,lockId);
		 LockKeyTb lockKeyTb = lockKeyTbs.get(0); 
		if(lockKeyTb.getKeyTb() == null){
			return RestResponse.build(ResponseCode.DEVICE_NOT_EXIST);
		}
		lockKeyTb.setIsFlag("0");//0 为已删除状态  1为可用状态
		if(lockKeyService.updateLockKey(lockKeyTb)>0){
			KeyTb key = lockKeyTb.getKeyTb();
			int count = lockKeyService.findLockKeyRelationCount(keyId,openId);
			if(key!=null&&key.getType()!=null&&key.getType()==1&&count==0){
				userLockService.compelUnbindDevice(openId, key.getDeviceId());
			}
		}
		return RestResponse.build(ResponseCode.SUCCESS);
	}
	
	@RequestMapping("/getKeyInfo")
	@ResponseBody
	public RestResponse getKeyInfo(@RequestParam(required = true)String lockId){
		List<Map<String, String>> data = new ArrayList<Map<String,String>>();
		List<KeyTb> list = lockKeyService.findKeyListByLockId(lockId,0);
		if(!CollectionUtils.isEmpty(list)){
			for(KeyTb key:list){
				Map<String, String> info = new HashMap<String,String>();
				info.put("keyID", key.getKeyId());
				if(key.getDeviceId() != null){
					info.put("device_id", key.getDeviceId());
				}
				info.put("position", String.valueOf(key.getPosition()));
				info.put("type", String.valueOf(key.getType()));
				info.put("status", key.getState());
				data.add(info);
			}
		}
		return RestResponse.build(ResponseCode.SUCCESS, data);
	}
	
	@RequestMapping("/changeKeyStatus")
	@ResponseBody
	public RestResponse changeKeyStatus(@RequestParam(required = true)String keyId,@RequestParam(required = true)String deviceId,@RequestParam(required = true)String keyState,boolean flag,String lockId,String openId){
		List<LockKeyTb> lockKeyTbs = lockKeyService.findLockKeyByKeyId(keyId,openId,lockId);
		KeyTb keyTable = new KeyTb();
		LockKeyTb lockkey = new LockKeyTb();
		if(lockKeyTbs!=null&&lockKeyTbs.get(0)!=null){
			lockkey = lockKeyTbs.get(0);
		}
		if(flag){//禁用或启用自己
			keyTable = lockKeyService.getKeyTbByKeyId(keyId);
			keyTable.setState(keyState);
			lockkey.setState(keyState);
			lockKeyService.updateKey(keyTable);
			lockKeyService.updateLockKey(lockkey);
		}else{
			lockkey.setState(keyState);
			lockKeyService.updateLockKey(lockkey);
		}
		return RestResponse.build(ResponseCode.SUCCESS);
	}
	@RequestMapping("/deviceauth")
	public RestResponse deviceAuth(HttpServletRequest request){
		String postData = request.getParameter("postData");
		JSONObject jsonObject = JSONObject.parseObject(postData);
		System.out.println(jsonObject.getString("deviceId"));
			boolean isok =  lockKeyService.deviceAuth(jsonObject);
			if(isok){
				Map<String,Object> data = new HashMap<String,Object>();
				data.put("device_id",jsonObject.getString("deviceId"));
				return  RestResponse.build(ResponseCode.SUCCESS,data );
			}
			return RestResponse.build(ResponseCode.CONFIG_NOT_EXIST, null);
	}
	
	/**
	 * 微信硬件事件推送处理      绑定事件 bind   解绑事件unbind
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws WxErrorException 
	 */
	private void wxDeviceRawMessage(HttpServletRequest request,HttpServletResponse response) throws IOException, WxErrorException{
		  String body = IOUtils.toString(request.getInputStream(), "UTF-8");
	      logger.debug("------devicebody="+body);
		  WxMpDeviceJsonMessage inMessage = WxMpGsonBuilder.INSTANCE.create().fromJson(body, WxMpDeviceJsonMessage.class);
	        String msgType = inMessage.getMsgType();
	        if("bind".equals(msgType)){
	        	UserTb user=null;
	        	if((user = userService.checkUser(inMessage.getOpenId()))==null){
	        		//检查用户不存在时添加用户
	        		user = new UserTb();
	        		user.setOpenId(inMessage.getOpenId());
	        		user.setUserId(inMessage.getOpenId());
	        		userService.addUser(user);
	        	}
	        	String suffixData = inMessage.getQrcodeSuffixData();
	        	if(suffixData!=null&&suffixData.startsWith("lock")){
	        		//添加锁芯
	        		String lockId = suffixData.substring(suffixData.indexOf("lock")+4, suffixData.length());
	        		userLockService.compelUnbindDevice(inMessage.getOpenId(),inMessage.getDeviceId());
	        		this.bindLock(inMessage.getOpenId(), lockId);
	        	}else{
	        		if(suffixData.endsWith("user")){
	        			 suffixData = suffixData.substring(0, suffixData.length()-4);
	        			//this.remoteAuthorized(suffixData,inMessage.getOpenId());
        		 } 
        			//添加钥匙
        			KeyTb  keytb = lockKeyService.findKeyTbByQrcodeSerial(suffixData);
        			TempTb temptb = new TempTb();
        			temptb.setKeyid(keytb.getKeyId());
        			//由于只知道一个keyID 和openid  锁芯id和keyID没有直接关系，可以先把openid代替lockid间接建立联系。然后在？？？
        			temptb.setOpenid(inMessage.getOpenId());
        			lockKeyService.insertSelective(temptb);
        			if(keytb.getType()!=1){
        				userLockService.compelUnbindDevice(inMessage.getOpenId(),inMessage.getDeviceId());
        		    }
	        	}
				response.getWriter().write(body);
	        } 
	        if("unbind".equals(msgType)){
	        	//用户点击微信设置里的【删除设备】来删除设备
	        	String suffixData = inMessage.getQrcodeSuffixData();
	        	if(suffixData!=null&&suffixData.startsWith("lock")){
	        		return ;
	        	}
	        	KeyTb  keytb = lockKeyService.getKeyTbByDeviceId(inMessage.getDeviceId());
	        	if(keytb.getType()!=1){
	        		//解绑电子钥匙
	        		return;
	        	}
	        	List<UserLockTb> locks = userLockService.getLockByOpenIdAndDeviceId(inMessage.getOpenId(), inMessage.getDeviceId());
	        	if(locks.isEmpty()){
	        		return;
	        	}
	        	 userLockService.unbindAllLockKey(inMessage.getOpenId(), locks); 
	        	 response.getWriter().write(body);
	        }
	}
	
	/**
	 * 远程被授权调用方法
	 */
	private void remoteAuthorized(String qrcodeSerial,String openId){
		KeyTb  keytb = lockKeyService.findKeyTbByQrcodeSerial(qrcodeSerial);
		 WxMpService wxMpService = startupConfig.getWxMpService();
		 TemplateData temlateData = new TemplateData();
		 temlateData.setFirst("远程授权");
		 temlateData.setColor("#000000");
		 temlateData.setRemark("对钥匙进行授权，该钥匙有效时间截止到"+keytb.getTime());
		 UserTb owner = userService.checkUser(keytb.getAuthOpenId());
		 UserTb newUser = userService.checkUser(openId);
		 String[] keyword = {owner.getNickname(),newUser.getNickname(),"钥匙使用已获得业主许可"};
		 temlateData.setTemplateId("Nf1VrLhsISZC_Z1qLKHb57-lWGRo3geved0595w60T8");
		 temlateData.setKeyword(keyword);
		 temlateData.setToUser(openId);
		if(keytb.getTime().getTime()>new Date().getTime()){
			 temlateData.setUrl("http://live-smart.com.cn/mui/confirm_authkey.html?deviceId="+keytb.getDeviceId()+"&openId="+newUser.getOpenId());
		}else{
			//用户没有权限使用钥匙
			 String[] keyword2 = {owner.getNickname(),newUser.getNickname(),"授权时间失效"};
			 temlateData.setKeyword(keyword2);
			 temlateData.setRemark("请重新联系业主 授权钥匙");
			 temlateData.setUrl("");
			 userLockService.compelUnbindDevice(newUser.getOpenId(),keytb.getDeviceId());
		}
		WxMpTemplateMessage templateMessage = WxSendTemplateMessage.buildTemplateMessage(temlateData );
		WxSendTemplateMessage.sendTemplatemessage(wxMpService, templateMessage);
	}
	
	private void bindLock(String openId,String lockId) throws WxErrorException{
		List<KeyTb> manageKeys = lockKeyService.findKeyListByLockId(lockId, 1);
	    for(KeyTb key:manageKeys){
		   //如果该用户未绑定 就绑定设备
		   if(key.getType()==1&&!userService.checkWxDeviceByOpenId(openId,key.getDeviceId())){
		       userLockService.compelbindDevice(openId,key.getDeviceId());
	       }
	    }
	    if(userLockService.checkUserLock(openId, lockId)==null){
	    	UserLockTb userlocktb = new UserLockTb();
	    	userlocktb.setLockId(lockId);
	    	userlocktb.setOpenId(openId);
	    	UserLockTb user = userLockService.getLockByLockIdAndRoleId(lockId,1);
	    	if(user!=null){
	    		//是否要向主人推一条确认消息
	    		//userlocktb.setRoleId(2);
	    		this.pushWxMessage(user.getOpenId(), openId, lockId);
	    	}else{
	    		userlocktb.setRoleId(1);
	    		userLockService.bindDevice(userlocktb);
	    	}
	    }
	}
	
	@RequestMapping("/lockCountByLockId")
	@ResponseBody
	public RestResponse getLockCountByLockId(@RequestParam String lockId){
		LockTb lock = userLockService.getLockTbByLockId(lockId);
		if(lock==null){
			return RestResponse.build(ResponseCode.SUCCESS, 0);
		}else{
			return RestResponse.build(ResponseCode.SUCCESS, 1);
		}
	}
	
	@RequestMapping("/updateKey")
	@ResponseBody
	public RestResponse updateKey(@RequestParam String key){
		logger.debug("key="+key);
		JSONObject newKey = JSONObject.parseObject(key);
		KeyTb keytb = new KeyTb(newKey.getInteger("kid"),newKey.getString("keyId"),
								newKey.getString("deviceId"),newKey.getString("keyName"),
								newKey.getInteger("type"),newKey.getInteger("position"),
								GetDateUtils.getDateByStr(newKey.getString("createTime")),newKey.getString("creator"),
								newKey.getString("state"),GetDateUtils.getDateByStr(newKey.getString("bindTime")),
								newKey.getString("toothCode"),newKey.getString("qrcode"),
								GetDateUtils.getDateByStr(newKey.getString("time")),newKey.getString("note"),
								newKey.getString("version"),newKey.getString("ticketId"),
								newKey.getString("qrcodeSerial"),newKey.getString("mac")
					 );
		
		int i = lockKeyService.updateKeyByKeyId(keytb);
		if(i>0){
			return RestResponse.build(ResponseCode.SUCCESS);
		}
		return null;
	}
	 
	@RequestMapping("/updateLock")
	@ResponseBody
	public RestResponse updateLock(@RequestParam String lock){
		logger.debug("lock="+lock);
		JSONObject 	 lockObj = JSONObject.parseObject(lock);
		LockTb  locktb = new LockTb();
		locktb.setLockId(lockObj.getString("lockId"));
		locktb.setCreateTime(GetDateUtils.getDateByStr(lockObj.getString("createTime")));
		locktb.setLockName(lockObj.getString("lockName"));
		int i = userLockService.updateLock(locktb);
		if(i>0){
			return RestResponse.build(ResponseCode.SUCCESS);
		}
		return null;
	}
	
	@RequestMapping("/initLock")
	@ResponseBody
	public RestResponse initLock(@RequestParam String openId,@RequestParam String lockId){
		List<LockKeyTb> keytbs = lockKeyService.findlockkeyByLockId(lockId, openId);
		for (LockKeyTb lockkeyTb : keytbs) {
			KeyTb keyTb = lockkeyTb.getKeyTb();
			lockkeyTb.setIsFlag("0");//关闭锁芯和钥匙关系
			int i= lockKeyService.updateLockKey(lockkeyTb);
			if(i>0&&keyTb!=null&&keyTb.getType()==1&&keyTb.getDeviceId()!=null){
				int count = lockKeyService.findLockKeyRelationCount(keyTb.getKeyId(),openId);
				if(count==0){
					userLockService.compelUnbindDevice(openId, keyTb.getDeviceId());
				}
			}
		}
		return RestResponse.build(ResponseCode.SUCCESS);
	}
 
	/**
	 * 推送模板消息
	 * @param openId  锁主人的微信号
	 * @param bopenId 推送者的微信号
	 * @param response 
	 */
	private void pushWxMessage(String openId,String bopenId,String lockId){
		 WxMpService wxMpService = startupConfig.getWxMpService();
		 TemplateData temlateData = new TemplateData();
		 temlateData.setFirst("申请添加锁芯");
		 temlateData.setColor("#000000");
		 temlateData.setRemark("添加锁芯授权");
		 UserLockTb owner = userLockService.getLockByLockIdAndRoleId(lockId, 1);
		 UserTb newUser = userService.checkUser(bopenId);
		 String[] keyword = {owner.getUserTb().getNickname(),newUser.getNickname(),"待确认添加授权"};
		 temlateData.setTemplateId("Nf1VrLhsISZC_Z1qLKHb57-lWGRo3geved0595w60T8");
		 temlateData.setUrl("http://live-smart.com.cn/mui/confirm_lock.html?lockId="+lockId+"&openId="+bopenId);
		 temlateData.setKeyword(keyword);
		 //向主人发送消息
		 temlateData.setToUser(owner.getUserTb().getOpenId());
		WxMpTemplateMessage templateMessage = WxSendTemplateMessage.buildTemplateMessage(temlateData );
		WxSendTemplateMessage.sendTemplatemessage(wxMpService, templateMessage);
		//向推送者发送消息
		temlateData.setToUser(newUser.getOpenId());
		temlateData.setUrl("");
		templateMessage = WxSendTemplateMessage.buildTemplateMessage(temlateData );
		WxSendTemplateMessage.sendTemplatemessage(wxMpService, templateMessage);
	}
	
	@RequestMapping("/confirmlock")
	@ResponseBody
	public RestResponse confirmlock(@RequestParam String lockId,@RequestParam String openId){
		if(userService.checkUser(openId)==null){
			return RestResponse.build(ResponseCode.USER_NOT_EXIST);
		}
		if(userLockService.getLockTbByLockId(lockId)==null){
			return RestResponse.build(ResponseCode.DEVICE_NOT_EXIST);
		}
		 if(userLockService.checkUserLock(openId, lockId)==null){
		    	UserLockTb userlocktb = new UserLockTb();
		    	userlocktb.setLockId(lockId);
		    	userlocktb.setOpenId(openId);
		    	userlocktb.setRoleId(2);
		        if(userLockService.bindDevice(userlocktb)>0){
		        	List<KeyTb> manageKeys = lockKeyService.findKeyListByLockId(lockId, 1);
		    	    for(KeyTb key:manageKeys){
		    		   //如果该用户未绑定 就绑定设备
		    		   if(key.getType()==1&&!userService.checkWxDeviceByOpenId(openId,key.getDeviceId())){
		    		       userLockService.compelbindDevice(openId,key.getDeviceId());
		    	       }
		    	    }
		    	    WxMpService wxMpService = startupConfig.getWxMpService();
	    		       TemplateData temlateData = new TemplateData();
	    				 temlateData.setFirst("添加锁芯消息请求");
	    				 temlateData.setColor("#000000");
	    				 temlateData.setRemark("添加锁芯");
	    				 UserLockTb owner = userLockService.getLockByLockIdAndRoleId(lockId, 1);
	    				 UserTb newUser = userService.checkUser(openId);
	    				 String[] keyword = {owner.getUserTb().getNickname(),newUser.getNickname(),"已授权"};
	    				 temlateData.setToUser(openId);
	    				 temlateData.setTemplateId("Nf1VrLhsISZC_Z1qLKHb57-lWGRo3geved0595w60T8");
	    				temlateData.setKeyword(keyword);
	    				WxMpTemplateMessage templateMessage = WxSendTemplateMessage.buildTemplateMessage(temlateData );
	    				WxSendTemplateMessage.sendTemplatemessage(wxMpService, templateMessage);
		        }
		 }
		return RestResponse.build(ResponseCode.SUCCESS);
	}
	
	@RequestMapping("/cancelAuth")
	@ResponseBody
	public RestResponse cancelAuth(@RequestParam String openId,@RequestParam String lockId){
		 WxMpService wxMpService = startupConfig.getWxMpService();
		 if(userService.checkUser(openId)==null){
				return RestResponse.build(ResponseCode.USER_NOT_EXIST);
		 }
		 if(userLockService.getLockTbByLockId(lockId)==null){
			return RestResponse.build(ResponseCode.DEVICE_NOT_EXIST);
		 }
		 UserLockTb user = userLockService.getLockByLockIdAndRoleId(lockId, 1);
		 if(user==null){
			 //没有主人  一般情况下不会出现这个问题
			 return RestResponse.build(ResponseCode.USER_NOT_EXIST);
		 }
		 TemplateData temlateData = new TemplateData();
		 temlateData.setFirst("主人回复添加锁芯消息");
		 temlateData.setColor("#000000");
		 temlateData.setRemark("取消添加锁芯");
		 UserLockTb owner = userLockService.getLockByLockIdAndRoleId(lockId, 1);
		 UserTb newUser = userService.checkUser(openId);
		 String[] keyword = {owner.getUserTb().getNickname(),newUser.getNickname(),"已取消授权"};
		 temlateData.setToUser(openId);
		 temlateData.setTemplateId("Nf1VrLhsISZC_Z1qLKHb57-lWGRo3geved0595w60T8");
		temlateData.setKeyword(keyword);
		WxMpTemplateMessage templateMessage = WxSendTemplateMessage.buildTemplateMessage(temlateData );
		WxSendTemplateMessage.sendTemplatemessage(wxMpService, templateMessage);
		return RestResponse.build(ResponseCode.SUCCESS);
	}
	
	@RequestMapping("/findKey")
	@ResponseBody
	public RestResponse findKeyByOpenId(@RequestParam String openId){
		Map<String,Object> data = new HashMap<String ,Object>();
		 List<TempTb> waitAddKey = lockKeyService.findTempTbByOpenId(openId);
		 KeyTb key = waitAddKey!=null&&!waitAddKey.isEmpty()&&waitAddKey.get(0)!=null?waitAddKey.get(0).getKeyTb():null;
		 data.put("key",key);
		return RestResponse.build(ResponseCode.SUCCESS, data);
	}
	
	
	
	@RequestMapping("/syncDevice")
	public void syncDevice(HttpServletRequest request){
		//String paramsStr = request.getParameter("params");
		//JSONObject obj = JSONObject.parseObject(paramsStr);
		
	}
	
	/**
	   绑定设备
	 * @param openId
	 * @param deviceId
	 * @param ticket
	 */
	@RequestMapping("/bindkey")
	@ResponseBody
	public RestResponse bindBlueKey(@RequestParam String openId,@RequestParam String deviceId,@RequestParam String ticket){
		if(!userService.checkWxDeviceByOpenId(openId, deviceId)){
			if(userLockService.bindDevice(openId, deviceId, ticket)){
				return RestResponse.build(ResponseCode.SUCCESS);
			}else{
				return RestResponse.build(ResponseCode.DEVICE_NOT_BIND);
			}
		}
		return RestResponse.build(ResponseCode.SUCCESS);
	}
	/**
	   解绑绑定设备
	 * @param openId
	 * @param deviceId
	 * @param ticket
	 */
	@RequestMapping("/unbindkey")
	@ResponseBody
	public RestResponse unbindBlueKey(@RequestParam String openId,@RequestParam String deviceId,@RequestParam String ticket){
		if(userLockService.unbindDevice(openId, deviceId, ticket)){
			return RestResponse.build(ResponseCode.SUCCESS);
		}
		return RestResponse.build(ResponseCode.DEVICE_NOT_BIND);
	}
	
	/**
	 * 主人远程授权
	 * @return
	 */
	@RequestMapping("/remoteAuth")
	@ResponseBody
	public RestResponse ownerRemoteAuth(@RequestParam String keyId,@RequestParam String authOpenId,@RequestParam int time){
		KeyTb keyTb = lockKeyService.getKeyByKeyId(keyId);
		if(keyTb.getKid()==null){
			return RestResponse.build(ResponseCode.DEVICE_NOT_EXIST);
		}
			
		keyTb.setTime(GetDateUtils.getTimestamp(time));
		keyTb.setAuthtime(time);
		keyTb.setAuthOpenId(authOpenId);
		lockKeyService.updateKey(keyTb);
		return RestResponse.build(ResponseCode.SUCCESS);
	}
	
}
