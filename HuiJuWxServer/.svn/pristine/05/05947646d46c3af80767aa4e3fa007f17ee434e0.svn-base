package com.huiju.weixin.WeixinServer.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.huiju.weixin.WeixinServer.conf.StartupConfig;
import com.huiju.weixin.WeixinServer.mapper.LockTbMapper;
import com.huiju.weixin.WeixinServer.mapper.UserLockTbMapper;
import com.huiju.weixin.WeixinServer.mapper.UserTbMapper;
import com.huiju.weixin.WeixinServer.model.LockKeyTb;
import com.huiju.weixin.WeixinServer.model.LockTb;
import com.huiju.weixin.WeixinServer.model.UserLockTb;
import com.huiju.weixin.WeixinServer.model.UserTb;
import com.huiju.weixin.WeixinServer.util.WxResp;

import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.util.http.SimplePostRequestExecutor;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.util.json.WxMpGsonBuilder;

/**
 * 处理微信用户和锁之间关系的业务类 (用户和锁之间的关系建立是通过钥匙建立的)
 * 
 * @author rencf 2016年11月10日10:58:39
 */
@Service
public class UserLockService {

	@Autowired
	private UserLockTbMapper userLockTbMapper;

	@Autowired
	private StartupConfig startupConfig;
	@Autowired
	private UserTbMapper userTbMapper;
	private static final Logger logger = Logger.getLogger(UserLockService.class);
	@Autowired
	private LockTbMapper locktbMapper;
	
	/**
	 * 绑定设备
	 * 
	 * @param userLockTb
	 *            传递参数是 openId 和 lockId
	 * @return true or false
	 */
	@Transactional
	public int bindDevice(UserLockTb userLockTb) {
		userLockTb.preInsert();
		return userLockTbMapper.insert(userLockTb);
	}

	/**
	 * 解绑设备
	 * 
	 * @param id
	 *            要删除锁用户关系表记录的主键ID
	 * @param openId
	 *            用户ID
	 * @param deviceId
	 *            设备id
	 * @param ticket
	 *            jsapi 参数
	 * @return true or false
	 */
	public boolean unbindDevice(String openId, String deviceId,String ticket) {
		WxMpService wxMpService = startupConfig.getWxMpService();
		boolean isok = false;
		try {
			String url = "https://api.weixin.qq.com/device/unbind";
			Map<String, String> data = new HashMap<String, String>();
			data.put("device_id", deviceId);
			data.put("openid", openId);
		    data.put("ticket", ticket);
			String resultContent = wxMpService.execute(new SimplePostRequestExecutor(), url,
					WxMpGsonBuilder.INSTANCE.create().toJson(data));
			logger.info("result: " + resultContent);
			WxResp resp = WxMpGsonBuilder.INSTANCE.create().fromJson(resultContent, WxResp.class);
			if (resp.getBaseResp().getErrcode() == 0) {
				// 解绑成功
				logger.info("unbind success.");
					isok = true;
			} else {
				logger.info("unbind fail.");
			}
		} catch (WxErrorException e) {
			logger.error("unbindDevice fail :" + e.getMessage());
		}
		return isok;
	}
	/**
	 * 绑定
	 * @param openId
	 * @param deviceId
	 * @param ticket
	 * @return
	 */
	public boolean bindDevice(String openId, String deviceId,String ticket) {
		WxMpService wxMpService = startupConfig.getWxMpService();
		boolean isok = false;
		try {
			String url = "https://api.weixin.qq.com/device/bind";
			Map<String, String> data = new HashMap<String, String>();
			data.put("ticket", ticket);
			data.put("device_id", deviceId);
			data.put("openid", openId);
			String resultContent = wxMpService.execute(new SimplePostRequestExecutor(), url,
					WxMpGsonBuilder.INSTANCE.create().toJson(data));
			logger.info("result: " + resultContent);
			WxResp resp = WxMpGsonBuilder.INSTANCE.create().fromJson(resultContent, WxResp.class);
			if (resp.getBaseResp().getErrcode() == 0) {
				//绑定成功
				logger.info("bind success.");
					isok = true;
			} else {
				logger.info("unbind fail.");
			}
		} catch (WxErrorException e) {
			logger.error("unbindDevice fail :" + e.getMessage());
		}
		return isok;
	}
	// 强制解绑和openId有关系的设备
	@Transactional
	public int unbindAllLockKey(String openId, List<UserLockTb> locks) {
		int i = 0;
		for (UserLockTb lock : locks) {
			Map<String, Object> map  = new HashMap<String,Object>();
			map.put("openId", openId);
			map.put("lockId", lock.getLockId());
			userLockTbMapper.deleteByOpenIdAndLockId(map);
			i++;
		}
		 return i;
		
	}

	/**
	 * 强制解绑设备
	 * 
	 * @param id
	 * @param openId
	 * @param deviceId
	 * @return
	 */
	public boolean compelUnbindDevice(String openId, String deviceId) {
		WxMpService wxMpService = startupConfig.getWxMpService();
		try {
			String url = "https://api.weixin.qq.com/device/compel_unbind";
			Map<String, String> data = new HashMap<String, String>();
			data.put("device_id", deviceId);
			data.put("openid", openId);
			String resultContent = wxMpService.execute(new SimplePostRequestExecutor(), url,
					WxMpGsonBuilder.INSTANCE.create().toJson(data));
			logger.info("result: " + resultContent);
			WxResp resp = WxMpGsonBuilder.INSTANCE.create().fromJson(resultContent, WxResp.class);
			if (resp.getBaseResp().getErrcode() == 0) {
				// 强制解绑成功
				logger.info("unbind success.");
				return true;
			} else {
				logger.info("unbind fail.");
			}
		} catch (WxErrorException e) {
			logger.error("unbindDevice fail :" + e.getMessage());
		}
		return false;
	}
	
	/**
	 * 根据设备ID和openid获取锁列表 
	 * @param openId
	 * @param deviceId
	 * @return
	 */
	public List<UserLockTb> getLockByOpenIdAndDeviceId(String openId, String deviceId) {
		Map<String,Object> map = new HashMap<String,Object>();
		map = new HashMap<String,Object>();
		map.put("openId", openId);
		map.put("deviceId", deviceId);
		List<UserLockTb> lockResult = userLockTbMapper.findLockIdsByOpenIdAndDeviceId( map);
		return lockResult==null?new ArrayList<UserLockTb>():lockResult;
	}
	/**
	 * 根据openid 获取锁列表
	 * @param openId
	 * @return
	 */
	public List<UserLockTb> getLockByOpenId(String openId) {
		return userLockTbMapper.getLockByOpenId(openId);
	}
	//解绑某一个具体的设备锁与用户的关系
	@Transactional
	public void unbindLock(String ticket, String openId, String lockId) {
		Map<String, Object> map  = new HashMap<String,Object>();
		map.put("openId", openId);
		map.put("lockId", lockId);
		 userLockTbMapper.deleteByOpenIdAndLockId(map);
	}

	public void compelbindDevice(String openId, String deviceId) {
		WxMpService wxMpService = startupConfig.getWxMpService();
		try {
			String url = "https://api.weixin.qq.com/device/compel_bind";
			Map<String, String> data = new HashMap<String, String>();
			data.put("device_id", deviceId);
			data.put("openid", openId);
			String resultContent = wxMpService.execute(new SimplePostRequestExecutor(), url,
					WxMpGsonBuilder.INSTANCE.create().toJson(data));
			logger.info("result: " + resultContent);
			WxResp resp = WxMpGsonBuilder.INSTANCE.create().fromJson(resultContent, WxResp.class);
			if (resp.getBaseResp().getErrcode() == 0) {
				// 强制绑定成功
				logger.info("bind success.");
				 
			} else {
				logger.info("bind fail.");
			}
		} catch (WxErrorException e) {
			logger.error("bindDevice fail :" + e.getMessage());
		}
	}
	/**
	 * 根据lockid  和openid 检查用户和锁是否存在关系 如果存在并返回
	 * @param openId
	 * @param lockId
	 * @return
	 */
	public UserLockTb checkUserLock(String openId, String lockId) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("lockId", lockId);
		data.put("openId", openId);
		List<UserLockTb> userlocks = userLockTbMapper.checkUserLock(data);
		return userlocks!=null&&!userlocks.isEmpty()&&userlocks.get(0)!=null?userlocks.get(0):null;
	}
	
	/**
	 * 当用户点击或者扫一扫二维码时
	 * @param inMessage 关注 /或扫描事件传递的值
	 */
	@Transactional
	public boolean subScribeOrScan(String openId, String lockId) {
		boolean isok = false;
		List<UserTb> users = userTbMapper.getUserByOpenId(openId);
		if (users==null||users.isEmpty()) {
			// 检查用户不存在时添加用户
			UserTb user = new UserTb();
			user.setOpenId(openId);
			user.setUserId(openId);
			user.preInsert();
			userTbMapper.insert(user);
		}
		UserLockTb newLock = null;
		if (lockId!=null&&!lockId.equals("")&&((newLock = checkUserLock(openId, lockId))==null)) {
			newLock = new UserLockTb();
			newLock.setOpenId(openId);
			newLock.setLockId(lockId);
			if (bindDevice(newLock) > 0) {
				isok = true;
			}
		}
		return isok;
	}
	/**
	 * 根据openid 和lockid删除记录
	 * @param openId
	 * @param lockId
	 */
	@Transactional
	public boolean deleteByOpenIdAndLockId(String openId, String lockId) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("openId", openId);
		data.put("lockId", lockId);
		if(userLockTbMapper.deleteByOpenIdAndLockId(data)>0){
			return true;
		}else{
			return false;
		}
	}
	@Transactional
	public int updateLock(LockTb lock) {
		return locktbMapper.updateBylockIdSelective(lock);
	}
	@Transactional
	public void bindDevice(UserLockTb newLock, List<Object> locksByDevice) {
		for (int i = 0; i < locksByDevice.size(); i++) {
			LockKeyTb lock = (LockKeyTb) locksByDevice.get(i);
			newLock.setLockId(lock.getLockId());
			newLock.preInsert();
			userLockTbMapper.insert(newLock);
		}
	}
	
	public int getLockByLockId(String lockId) {
		List<Integer> list = userLockTbMapper.getLockByLockId(lockId);	
		return list!=null&&!list.isEmpty()&&list.get(0)!=null?list.get(0):0;
	}

	public UserLockTb getLockByLockIdAndRoleId(String lockId, Integer roleId) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("roleId", roleId);
		data.put("lockId", lockId);
		List<UserLockTb> list = userLockTbMapper.getLockByLockIdAndRoleId(data);
		return list!=null&&!list.isEmpty()&&list.get(0)!=null?list.get(0):null;
	}
	
	public LockTb getLockTbByLockId(String lockId) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("lockId", lockId);
		List<LockTb> list = locktbMapper.getLockTbByLockId(data);
		return list!=null&&!list.isEmpty()&&list.get(0)!=null?list.get(0):null;
	}
	
	 
}
