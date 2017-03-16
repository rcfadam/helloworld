package com.huiju.weixin.WeixinServer.mapper;

import java.util.List;

import com.huiju.weixin.WeixinServer.model.KeyTable;

public interface KeyTableMapper {
    int deleteByPrimaryKey(String id);

    int insert(KeyTable record);

    int insertSelective(KeyTable record);

    KeyTable selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(KeyTable record);

    int updateByPrimaryKey(KeyTable record);
    
    List<KeyTable> findKeyTablesByDeviceId(String deviceId);
    
    List<KeyTable> findKeyTablesByOpenId(String openId);
    
    KeyTable getByDeviceId(String deviceId);
    
    List<KeyTable> findKeyTablesByOpenIdAndLockId(String openId,String lockId);
    
    KeyTable getByTicketId(String ticketId);
    
    KeyTable getByKeyId(String keyId);
    
    List<KeyTable> findKeyTablesByLockId(String lockId);
}