package com.huiju.weixin.WeixinServer.mapper;

import java.util.List;

import com.huiju.weixin.WeixinServer.model.UserTb;

public interface UserTbMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserTb record);

    int insertSelective(UserTb record);

    UserTb selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserTb record);

    int updateByPrimaryKey(UserTb record);
    
	List<UserTb> getUserByOpenId(String openId);
}