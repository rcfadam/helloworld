package com.huiju.weixin.WeixinServer.mapper;

import java.util.List;
import java.util.Map;

import com.huiju.weixin.WeixinServer.model.LockTb;

public interface LockTbMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(LockTb record);

    int insertSelective(LockTb record);

    LockTb selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(LockTb record);

    int updateByPrimaryKey(LockTb record);
    
    int updateBylockIdSelective(LockTb lock);
    List<LockTb> getLockTbByLockId(Map<String, Object> data);
}