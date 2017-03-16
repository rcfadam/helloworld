package com.huiju.weixin.WeixinServer.service;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.huiju.weixin.WeixinServer.conf.StartupConfig;
import com.huiju.weixin.WeixinServer.mapper.UserTbMapper;
import com.huiju.weixin.WeixinServer.model.UserTb;
import com.huiju.weixin.WeixinServer.service.UserService.WxBindDevice.Device;

import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.util.http.SimpleGetRequestExecutor;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.util.json.WxMpGsonBuilder;

/**
 * 处理用户信息的业务逻辑
 * @author rencf
 * 2016年11月10日13:17:00
 */
@Service
public class UserService {

	@Autowired
	private UserTbMapper userTbMapper;
	@Autowired
	private StartupConfig startupConfig;
	
	@Transactional
	public int addUser(UserTb user){
		user.preInsert();
		return userTbMapper.insert(user);
	}
	
	/**
	 * 删除用户信息
	 * @param user
	 * @return
	 */
	@Transactional
	public int updateUser(UserTb user){
		return userTbMapper.updateByPrimaryKey(user);
	}
	
	/**
	 * 检查用户是否存在
	 * @param openId
	 * @return
	 */
	public UserTb checkUser(String  openId) {
		List<UserTb> users = userTbMapper.getUserByOpenId(openId);
		return users!=null&&!users.isEmpty()&&users.get(0)!=null?users.get(0):null;
	}
	/**
	 * 
	 * @param deviceType 公众账号的原始id
	 * @param deviceId
	 * @return
	 */
	public String[] getWxOpenId(String deviceType, String deviceId) {
		WxMpService wxMpService = startupConfig.getWxMpService();
		String[] openid ={};
		try {
			String url = "https://api.weixin.qq.com/device/get_openid?device_type="+deviceType+"&device_id="+deviceId;
			String resultContent = wxMpService.execute(new SimpleGetRequestExecutor(), url,null );
			WxBindDevice resp = WxMpGsonBuilder.INSTANCE.create().fromJson(resultContent, WxBindDevice.class);
			if (resp.getResp_msg().getRet_code().equals("0")) {
				openid = resp.getOpen_id();
			}
		} catch (WxErrorException e) {
		}
		return openid;
	}
	public List<Device> getWxBindDevice(String openId, String deviceId) {
		WxMpService wxMpService = startupConfig.getWxMpService();
		List<Device> deviceList = new ArrayList<Device>();
		try {
			String url = "https://api.weixin.qq.com/device/get_bind_device?openid="+openId;
			  
			String resultContent = wxMpService.execute(new SimpleGetRequestExecutor(), url,null );
			WxBindDevice resp = WxMpGsonBuilder.INSTANCE.create().fromJson(resultContent, WxBindDevice.class);
			if (resp.getResp_msg().getRet_code().equals("0")) {
				deviceList = resp.getDevice_list();
			}
		} catch (WxErrorException e) {
		}
		return deviceList;
	}
	public class WxBindDevice{
		private RespMsg resp_msg;
		private String openid;
		private List<Device> device_list;
		private String[] open_id;
		public String[] getOpen_id() {
			return open_id;
		}
		public void setOpen_id(String[] open_id) {
			this.open_id = open_id;
		}
		public RespMsg getResp_msg() {
			return resp_msg;
		}
		public void setResp_msg(RespMsg resp_msg) {
			this.resp_msg = resp_msg;
		}
		public String getOpenid() {
			return openid;
		}
		public void setOpenid(String openid) {
			this.openid = openid;
		}
		public List<Device> getDevice_list() {
			return device_list;
		}
		public void setDevice_list(List<Device> device_list) {
			this.device_list = device_list;
		}
		
		protected class RespMsg{
			 private String ret_code;
		     private String error_info;
			public String getRet_code() {
				return ret_code;
			}
			public void setRet_code(String ret_code) {
				this.ret_code = ret_code;
			}
			public String getError_info() {
				return error_info;
			}
			public void setError_info(String error_info) {
				this.error_info = error_info;
			}
		     
		     
		}
		public class Device{
			 private String device_type;
	         private String device_id;
			public String getDevice_type() {
				return device_type;
			}
			public void setDevice_type(String device_type) {
				this.device_type = device_type;
			}
			public String getDevice_id() {
				return device_id;
			}
			public void setDevice_id(String device_id) {
				this.device_id = device_id;
			}
	         
		}
	}
	/**
	 * 检查微信端绑定的蓝牙钥匙
	 * @param openId  
	 * @param deviceId
	 * @return 已绑定返回true 未绑定返回FALSE
	 */
	public boolean checkWxDeviceByOpenId(String openId, String deviceId) {
		List<Device> list =  getWxBindDevice(openId, deviceId);
		if(list.isEmpty()){
			return false;
		}else{
			for (Device device : list) {
				if(device.device_id.equals(deviceId)){
					return true;
				}
			}
			return false;
		}
	}
}
