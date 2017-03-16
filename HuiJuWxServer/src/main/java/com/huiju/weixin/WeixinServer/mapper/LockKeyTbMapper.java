package com.huiju.weixin.WeixinServer.mapper;

import java.util.List;
import java.util.Map;

import com.huiju.weixin.WeixinServer.bean.UserLockKeyBean;
import com.huiju.weixin.WeixinServer.model.KeyTb;
import com.huiju.weixin.WeixinServer.model.LockKeyTb;

public interface LockKeyTbMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(LockKeyTb record);

    int insertSelective(LockKeyTb record);

    LockKeyTb selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(LockKeyTb record);

    int updateByPrimaryKey(LockKeyTb record);
    
   
	/**
	 * 根据lockId 删除设备锁和钥匙的关系表记录
	 * @param lockId 设备锁lockId
	 * @return
	 */
	int deleteByLockId(String lockId);

	/**
	 * 根据lockId 和keyId 删除设备锁和钥匙的表关系
	 * @param lockId
	 * @param keyId
	 * @return
	 */
	int deleteByLockIdAndKeyId(Map<String,Object> map);
	
	/**
	 * 根据lockId获取钥匙列表
	 * @param lockId
	 * @return
	 */
	List<KeyTb> findKeyTablesByLockId(Map<String,Object> map);
	
	UserLockKeyBean getByDeviceId(String deviceId);

	List<Object> findLocksbydeviceId(Map<String,Object> map);
	
	List<LockKeyTb> findLocksListbydeviceId(String deviceId);
	
	int updateByLockIdAndKeyIdSelective(Map<String, Object> map);

	List<LockKeyTb> findLockKeyByLockIdAndKeyId(Map<String, Object> map);

	List<LockKeyTb> findLockKeyByKeyId(Map<String, Object> map);

	List<LockKeyTb> findLockKeyByKeyIdAndOpenID(Map<String, Object> map);
	List<LockKeyTb> findlockkeyByLockId(Map<String, Object> map);

	List<LockKeyTb> findLockKeyRelationCount(Map<String, Object> map);
}