package com.huiju.weixin.WeixinServer.mapper;

import java.util.List;
import java.util.Map;


import com.huiju.weixin.WeixinServer.model.LockTb;
import com.huiju.weixin.WeixinServer.model.UserLockTb;

public interface UserLockTbMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserLockTb record);

    int insertSelective(UserLockTb record);

    UserLockTb selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserLockTb record);

    int updateByPrimaryKey(UserLockTb record);
    //根据openId 和设备ID获取lockId
    List<UserLockTb> findLockIdByOpenIdAndDeviceId(String openId, String deviceId);

	List<LockTb> findLockIdByOpenId(String openId);

	List<LockTb> findLockIdByDeviceId(String deviceId);

	List<UserLockTb> getLockByOpenId(String openId);
	
	/**
	 * 根据openid 和 lockid 删除user_lock_tb 中的记录   
	 * @    map  openId=? 和 lockId = ? 
	 */
	int deleteByOpenIdAndLockId(Map<String,Object> map);

	List<UserLockTb> findLockIdsByOpenIdAndDeviceId(Map<String, Object> map);
	
	List<UserLockTb> checkUserLock(Map<String, String> data);

	List<Integer> getLockByLockId(String lockId);

	List<UserLockTb> getLockByLockIdAndRoleId(Map<String, Object> map);

}