package com.huiju.weixin.WeixinServer.mapper;

import java.util.List;
import java.util.Map;

import com.huiju.weixin.WeixinServer.model.TempTb;
/**
 * 处理用户添加钥匙时存储待添加的钥匙与用户的关系的临时表业务
 */
public interface TempTbMapper {
    int insert(TempTb record);

    int insertSelective(TempTb record);

	List<TempTb> findTempTbByOpenId(Map<String, Object> map);

	int deleteByOpenIdAndKeyId(Map<String, Object> map);
}