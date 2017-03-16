package com.huiju.weixin.WeixinServer.mapper;

import java.util.List;
import java.util.Map;

import com.huiju.weixin.WeixinServer.model.KeyTb;

public interface KeyTbMapper {
    int deleteByPrimaryKey(Integer kid);

    int insert(KeyTb record);

    int insertSelective(KeyTb record);

    KeyTb selectByPrimaryKey(Integer kid);

    int updateByPrimaryKeySelective(KeyTb record);

    int updateByPrimaryKey(KeyTb record);

	KeyTb getByTicketId(String ticketId);

	List<KeyTb> getKeyTbByKeyId(String keyId);

	/**
	 * 根据钥匙序号获取钥匙信息
	 */
	List<KeyTb> getByQrcodeSerial(Map<String, Object> map);

	/**
	 * 根据设备deviceid 获取设备信息 
	 */
	List<KeyTb> findKeyTbByDeviceId(Map<String, Object> map);

	int updateByKeyId(KeyTb keytb);

}