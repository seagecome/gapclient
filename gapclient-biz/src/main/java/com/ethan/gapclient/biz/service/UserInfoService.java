package com.ethan.gapclient.biz.service;

import java.util.Map;

import com.ethan.gapclient.dal.model.GapUserInfo;
import com.ethan.gapclient.dal.model.UserLoginInfo;

public interface UserInfoService {
	public int addUserInfo(GapUserInfo gapUserInfo);
	
	public UserLoginInfo queryUserInfo(Map<String, Object> queryMap);
}
