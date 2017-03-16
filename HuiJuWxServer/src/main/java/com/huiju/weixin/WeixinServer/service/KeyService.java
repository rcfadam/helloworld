package com.huiju.weixin.WeixinServer.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.huiju.weixin.WeixinServer.conf.StartupConfig;
import com.huiju.weixin.WeixinServer.mapper.KeyTableMapper;
import com.huiju.weixin.WeixinServer.model.KeyTable;
import com.huiju.weixin.WeixinServer.util.WxResp;

import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.util.http.SimplePostRequestExecutor;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.util.json.WxMpGsonBuilder;


@Service
public class KeyService {

	private static final Logger logger = Logger.getLogger(KeyService.class);
	
	@Autowired
	private KeyTableMapper keyTableMapper;
	
	@Autowired
	private StartupConfig startupConfig;
	
	public void bindDevice(String openId,KeyTable key){
		key.preUpdate(openId);
		key.setOpenId(openId);
		updateKey(key);
	}
	
	public boolean compelUnbindDevice(String deviceId,String openId){
		WxMpService wxMpService = startupConfig.getWxMpService();
		try {
		String url = "https://api.weixin.qq.com/device/compel_unbind";
		Map<String, String> data = new HashMap<String,String>();
		data.put("device_id", deviceId);
		data.put("openid", openId);
	      String resultContent = wxMpService.execute(new SimplePostRequestExecutor(), url, WxMpGsonBuilder.INSTANCE.create().toJson(data));
	      logger.info("result: " + resultContent);
	      WxResp resp = WxMpGsonBuilder.INSTANCE.create().fromJson(resultContent, WxResp.class);
	      if(resp.getBaseResp().getErrcode() == 0){
	    	  //强制解绑成功
	    	  logger.info("unbind success.");
	    	  return true;
	      }else{
	    	  logger.info("unbind fail.");
	    	  return false;
	      }
	    } catch (WxErrorException e) {
	      return false;
	    }
	}
	
	public boolean unbindDevice(String ticket,String deviceId,String openId){
		WxMpService wxMpService = startupConfig.getWxMpService();
		try {
		String url = "https://api.weixin.qq.com/device/unbind";
		Map<String, String> data = new HashMap<String,String>();
		data.put("device_id", deviceId);
		data.put("openid", openId);
		data.put("ticket", ticket);
	      String resultContent = wxMpService.execute(new SimplePostRequestExecutor(), url, WxMpGsonBuilder.INSTANCE.create().toJson(data));
	      logger.info("result: " + resultContent);
	      WxResp resp = WxMpGsonBuilder.INSTANCE.create().fromJson(resultContent, WxResp.class);
	      if(resp.getBaseResp().getErrcode() == 0){
	    	  //解绑成功
	    	  logger.info("unbind success.");
	    	  return true;
	      }else{
	    	  logger.info("unbind fail.");
	    	  return false;
	      }
	    } catch (WxErrorException e) {
	      return false;
	    }
	}
	
	public List<KeyTable> getLockList(String openId){
		return keyTableMapper.findKeyTablesByOpenId(openId);
	}
	
	public KeyTable getKeyByDeviceId(String deviceId){
		return keyTableMapper.getByDeviceId(deviceId);
	}
	
	@Transactional
	public void unbindAllLockKey(String ticket,String openId,KeyTable key){
		List<KeyTable> keys = keyTableMapper.findKeyTablesByOpenIdAndLockId(openId, key.getLockId());
		if(!CollectionUtils.isEmpty(keys)){
			for(KeyTable selectKey:keys){
				if(compelUnbindDevice(selectKey.getDeviceId(), openId)){
					selectKey.preUpdate(openId);
					selectKey.setOpenId(null);
					keyTableMapper.updateByPrimaryKey(selectKey);
				}
			}
		}
	}
	
	@Transactional
	public void unbindKey(String ticket,String openId,KeyTable key){
		if(unbindDevice(ticket, key.getDeviceId(), openId)){
			key.preUpdate(openId);
			key.setOpenId(null);
			keyTableMapper.updateByPrimaryKey(key);
		}
	}
	
	public KeyTable getKeyByTicketId(String ticketId){
		return keyTableMapper.getByTicketId(ticketId);
	}
	
	public KeyTable getKeyByKeyId(String keyId){
		return keyTableMapper.getByKeyId(keyId);
	}
	
	public List<KeyTable> findKeyListByLockId(String lockId){
		return keyTableMapper.findKeyTablesByLockId(lockId);
	}
	
	@Transactional
	public void updateKey(KeyTable keyTable){
		keyTableMapper.updateByPrimaryKey(keyTable);
	}
	
}
