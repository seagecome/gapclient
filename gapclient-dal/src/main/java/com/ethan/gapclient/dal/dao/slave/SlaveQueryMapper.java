package com.ethan.gapclient.dal.dao.slave;

import java.util.Map;

import com.ethan.gapclient.dal.model.UserLoginInfo;

public interface SlaveQueryMapper {
	UserLoginInfo selectByPrimaryKey(Integer loginId);
	UserLoginInfo selectByMap(Map<String, Object> queryMap);
}
