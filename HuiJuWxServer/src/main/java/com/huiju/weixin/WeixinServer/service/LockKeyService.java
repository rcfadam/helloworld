package com.huiju.weixin.WeixinServer.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.huiju.weixin.WeixinServer.bean.UserLockKeyBean;
import com.huiju.weixin.WeixinServer.mapper.KeyTbMapper;
import com.huiju.weixin.WeixinServer.mapper.LockKeyTbMapper;
import com.huiju.weixin.WeixinServer.mapper.LockTbMapper;
import com.huiju.weixin.WeixinServer.mapper.TempTbMapper;
import com.huiju.weixin.WeixinServer.model.KeyTb;
import com.huiju.weixin.WeixinServer.model.LockKeyTb;
import com.huiju.weixin.WeixinServer.model.LockTb;
import com.huiju.weixin.WeixinServer.model.TempTb;
import com.huiju.weixin.WeixinServer.util.BleDeviceAuth;
import com.huiju.weixin.WeixinServer.util.CreateExecutorService;
import com.huiju.weixin.WeixinServer.util.GetBleDeviceQrcode;
import com.huiju.weixin.WeixinServer.util.ImportDataToExcel;

import jxl.write.WriteException;
import me.chanjar.weixin.common.exception.WxErrorException;

/**
 * 处理锁和钥匙关系的业务类
 * 
 * @author rencf 2016年11月10日10:57:15
 */
@Service
public class LockKeyService {
	Logger logger = Logger.getLogger(LockKeyService.class);
	@Autowired
	private LockKeyTbMapper lockKeyTbMapper;
	@Autowired
	private KeyTbMapper keyTbMapper;
	@Autowired
	private LockTbMapper lockTbMapper;
	
	@Autowired
	private BleDeviceAuth bleDeviceAuth;
	@Autowired
	private GetBleDeviceQrcode getBleDeviceQrcode;
	
	@Autowired
	private TempTbMapper tempTbMapper;
	// 解绑钥匙
	@Transactional
	public int unbindKey(String lockId, String keyId) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("lockId", lockId);
		map.put("keyId", keyId);
		return lockKeyTbMapper.deleteByLockIdAndKeyId(map);
	}
	// 添加钥匙
	@Transactional
	public int addKey(LockKeyTb lockKeyTb){
		lockKeyTb.preInsert();
		return lockKeyTbMapper.insert(lockKeyTb);
	}
	// 根据 活码值ticketID获取钥匙列表
	public KeyTb getKeyByTicketId(String ticketId){
		KeyTb keyTb = keyTbMapper.getByTicketId(ticketId);
		return keyTb==null?new KeyTb():keyTb;
	}
	// 根据keyID获取钥匙
	public KeyTb getKeyByKeyId(String keyId){
		List<KeyTb> keyTb = keyTbMapper.getKeyTbByKeyId(keyId);
		return keyTb!=null&&!keyTb.isEmpty()&&keyTb.get(0)!=null?keyTb.get(0):new KeyTb();
	}
	/** 根据lockId 获取钥匙列表
	 * 
	 * @param lockId
	 * @param manage  是否是管理钥匙    0普通钥匙  1 蓝牙钥匙
	 * @return
	 */
	public List<KeyTb> findKeyListByLockId(String lockId,Integer manage){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("lockId", lockId);
		//map.put("manage", manage); 
		List<KeyTb> keyTbs  = lockKeyTbMapper.findKeyTablesByLockId(map);
		return keyTbs==null?new ArrayList<KeyTb>():keyTbs;
	}
	// 修改钥匙
	@Transactional
	public int updateKey(KeyTb keyTb){
		return keyTbMapper.updateByPrimaryKey(keyTb);
	}
	
	/**
	 * 根据设备ID获取钥匙
	 * @param deviceId
	 * @return
	 */
	public UserLockKeyBean getKeyByDeviceId(String deviceId) {
		UserLockKeyBean key =  lockKeyTbMapper.getByDeviceId(deviceId);
		return key==null?new UserLockKeyBean():key;
	}
	/**保存表关系
	 * 
	 * @param key
	 */
	@Transactional
	public void save(LockKeyTb key) {
		lockKeyTbMapper.insert(key);
	}
	
	/**
	 * 
	 * @param key
	 * @param lockkey
	 * @return
	 */
	@Transactional
	public boolean addkey(KeyTb key, LockKeyTb lockkey) {
		boolean isok = false;
		key.preInsert();
		int i = keyTbMapper.updateByPrimaryKey(key);
		lockkey.preInsert();
		int j = 0;
		logger.debug("lockkey="+JSONObject.toJSONString(lockkey));
		if(lockkey!=null&&lockkey.getId()!=null){
			logger.debug("lockkey="+JSONObject.toJSONString(lockkey));
			j = lockKeyTbMapper.updateByPrimaryKey(lockkey);
		}else if(lockkey!=null){
			logger.debug("lockkey="+JSONObject.toJSONString(lockkey));
			j = lockKeyTbMapper.insert(lockkey);
		}
		if(i==j&&i>0){
			isok = true;
		}
		return isok;
	}
	
	/**
	 * 删除钥匙
	 * @param lockId
	 * @param keyId
	 */
	@Transactional
	public void deleteKey(String lockId, String keyId) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("lockId", lockId);
		map.put("keyId", keyId);
		lockKeyTbMapper.deleteByLockIdAndKeyId(map);
	}
	
	/**
	 * 根据keyId 获取key_tb记录
	 * @param keyId
	 * @return  
	 */
	public KeyTb getKeyTbByKeyId(String keyId) {
		 
		List<KeyTb> keyTb =  keyTbMapper.getKeyTbByKeyId(keyId);
		return keyTb!=null&&!keyTb.isEmpty()&&keyTb.get(0)!=null?keyTb.get(0):new KeyTb();
	}
	
	public List<Object> findLocksbydeviceId(String deviceId){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("deviceId", deviceId);
		List<Object> list =  lockKeyTbMapper.findLocksbydeviceId(map);
		return list;
	}
	
	/**
	 * 设备授权 并持久话锁芯，钥匙 ，以及锁芯和钥匙关系
	 * 
	 * @return
	 */
	boolean isok = false;
	public boolean deviceAuth(JSONObject jsonObject) {
		ExecutorService cacheThreadPool = CreateExecutorService.getThreadPool();
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("qrcodeSerial", jsonObject.getString("qrcodeSerial"));
		List<KeyTb> keys = keyTbMapper.getByQrcodeSerial(map);
		if (keys == null||keys.isEmpty()||keys.get(0)==null) {
			return false;
		}
		KeyTb key = keys.get(0);
		final String mac = jsonObject.getString("mac");
		final String deviceId = key.getDeviceId();
		key.setCreator(jsonObject.getString("readId"));
		key.setKeyId(jsonObject.getString("keyId"));
		key.setType(1);
		key.setPosition(Integer.parseInt(jsonObject.getString("position")));
		key.setState("1");
		//key.setToothCode(jsonObject.getString("toothCode"));
		key.setVersion(jsonObject.getString("keyVersion"));
		key.setBindTime(new Date());
		LockTb lock = new LockTb();
		lock.setLockId(jsonObject.getString("lockId"));
		lock.setCreator(jsonObject.getString("readId"));
		lock.setVersion(jsonObject.getString("lockVersion"));
		cacheThreadPool.execute(new Runnable() {
			public void run() {
				isok = saveLockAndKey(lock, key);
			}
		});
		cacheThreadPool.execute(new Runnable() {
			public void run() {
				try {
					bleDeviceAuth.bleDeviceAuth(deviceId, mac);
				} catch (IOException | WxErrorException e) {
					logger.error("蓝牙钥匙修改修改失败:" + e.getMessage());
				}
			}
		});
		isok = true;
		return isok;
	}
	/**
	 * 新增锁芯信息  以及修改钥匙信息  并且新增锁芯和钥匙的关系
	 * @param lock
	 * @param key
	 */
	@Transactional
	private  boolean saveLockAndKey(LockTb lock,KeyTb key){
		int j = keyTbMapper.updateByPrimaryKey(key);
		lock.preInsert();
		int k = lockTbMapper.insert(lock);
		LockKeyTb userLock = new LockKeyTb();
		userLock.preInsert();
		userLock.setKeyId(key.getKeyId());
		userLock.setLockId(lock.getLockId());
		int i = lockKeyTbMapper.insert(userLock );
		if(i==1&&j==1&&k==1){
			return true;
		}
		return false;
	}
	
	/**
	 * 授权蓝牙设备并获取设备id和二维码 封装到list中，
	 * @param num  获取设备的个数
	 * @param productId 设备管理中添加的产品id
	 * @return list 
	 * @throws IOException
	 * @throws WxErrorException
	 */
	private List<KeyTb> getDeviceIdAndQrcode(int num,String productId) throws IOException, WxErrorException{
		List<KeyTb> list = new ArrayList<KeyTb>();
		for (int i = 0; i < num; i++) {
			KeyTb key = new KeyTb();
			JSONObject jsonObject = getBleDeviceQrcode.getBleDeviceQrcode(productId);
			key.setDeviceId(jsonObject.getString("deviceid"));
			key.setQrcodeSerial(i+1+"");
			key.setTicketId(jsonObject.getString("qrticket"));
			list.add(key);
		}
		return list;
	}
	
	/**
	 * 授权蓝牙设备并获取设备id和二维码  并将设备id和 二维码 序列号写入到Excel表中以及数据库表中
	 * @param num
	 * @param productId
	 * @throws WxErrorException 
	 * @throws IOException 
	 */
	public void saveDeviceIdAndQrcode(int num,String productId,final String xlsPath) throws IOException, WxErrorException{
		final List<KeyTb> list = getDeviceIdAndQrcode(num, productId);
		ExecutorService cacheThreadPool = CreateExecutorService.getThreadPool();
		//持久化
		cacheThreadPool.execute(new Runnable() {
			public void run() {
				insertKey(list);
			}
		});
		//写入到Excel中
		cacheThreadPool.execute(new Runnable() {
			public void run() {
					try {
						ImportDataToExcel.execute(list,xlsPath);
					} catch (WriteException | IOException e) {
						e.printStackTrace();
					}
			}
		});
		
	}
	
	@Transactional
	private int insertKey(List<KeyTb> keyList){
		int result = 0;
		for (int i = 0; i < keyList.size(); i++) {
			keyTbMapper.insert(keyList.get(i));
		} 
		if(result==keyList.size()){
			return result;
		}
		return 0;
	}
	/**
	 * 修改钥匙和锁的关系
	 * @param lockKey
	 * @return
	 */
	@Transactional
	public int updateLockKey(LockKeyTb lockkey) {
		return lockKeyTbMapper.updateByPrimaryKey(lockkey);
	}
	
	public LockKeyTb findLockKeyByLockIdAndKeyId(String lockId, String keyId) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("lockId", lockId);
		map.put("keyId", keyId);
		List<LockKeyTb> list =  lockKeyTbMapper.findLockKeyByLockIdAndKeyId(map);
		return list!=null&&!list.isEmpty()&&list.get(0)!=null?list.get(0):null;
	}
	public List<LockKeyTb> findLockKeyByKeyId(String keyId){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("keyId", keyId);
		List<LockKeyTb> list =  lockKeyTbMapper.findLockKeyByKeyId(map);
		return list;
	}
	
	public LockKeyTb getLockKeyBylockId(List<LockKeyTb> lockKeyTbs,String lockId){
		if(lockKeyTbs!=null&&!lockKeyTbs.isEmpty()){
			for (LockKeyTb lockKeyTb : lockKeyTbs) {
				if(lockKeyTb!=null&&lockKeyTb.getLockId().equals(lockId)&&"1".equals(lockKeyTb.getIsFlag())){
					return lockKeyTb;
				}
			}
		}
		return new LockKeyTb();
	}
	
	public KeyTb getKeyTbByDeviceId(String deviceId) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("deviceId", deviceId);
		List<KeyTb> list = keyTbMapper.findKeyTbByDeviceId(map);
		return list!=null&&!list.isEmpty()&&list.get(0)!=null?list.get(0):null;
	}
	@Transactional
	public void addKey(KeyTb key) {
		key.preInsert();
		keyTbMapper.insertSelective(key);
	}
	
	public List<LockKeyTb> findLockKeyByKeyId(String keyId, String openId,String lockId) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("openId", openId);
		map.put("keyId", keyId);
		map.put("lockId", lockId);
		return lockKeyTbMapper.findLockKeyByKeyIdAndOpenID(map);
		 
	}
	
	public List<LockKeyTb> findlockkeyByLockId(String lockId,String openId) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("lockId", lockId);
		map.put("openId", openId);
		List<LockKeyTb> list = lockKeyTbMapper.findlockkeyByLockId(map);
		return list;
	}
	public int findLockKeyRelationCount(String keyId, String openId) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("keyId", keyId);
		map.put("openId", openId);
		List<LockKeyTb> list = lockKeyTbMapper.findLockKeyRelationCount(map);
		return list.size();
		
	}
	
	public KeyTb findKeyTbByQrcodeSerial(String customKeyid) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("qrcodeSerial", customKeyid);
		List<KeyTb> list = keyTbMapper.getByQrcodeSerial(map);
		return list!=null&&!list.isEmpty()&&list.get(0)!=null?list.get(0):null;
	}
	public List<TempTb> findTempTbByOpenId(String openId) {
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("openId", openId);
		List<TempTb> list = tempTbMapper.findTempTbByOpenId(map);
		return list;
	}
	@Transactional
	public void deleteByOpenIdAndKeyId(String keyId, String openId) {
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("openId", openId);
		map.put("keyId", keyId);
		tempTbMapper.deleteByOpenIdAndKeyId(map);
		
	}
	@Transactional
	public void insertSelective(TempTb temptb) {
		 tempTbMapper.insertSelective(temptb);
		
	}
	@Transactional
	public int updateKeyByKeyId(KeyTb keytb) {
		 
		return keyTbMapper.updateByKeyId(keytb);
	}
}
