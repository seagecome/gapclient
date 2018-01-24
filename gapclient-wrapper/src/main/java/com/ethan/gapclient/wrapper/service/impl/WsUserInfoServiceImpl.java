package com.ethan.gapclient.wrapper.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.ethan.gap.api.ws.CxfUserInfoSerivce;
import com.ethan.gapclient.wrapper.service.WsUserInfoService;
import com.ethan.gapclient.wrapper.utils.SpringContextUtil;

@Service("wsUserInfoService")
public class WsUserInfoServiceImpl implements WsUserInfoService {

	@Autowired
	private SpringContextUtil springContextUtil;
	
	@Override
	public String showUserInfo(String userId) {
		ApplicationContext context = springContextUtil.getApplicationContext();
		CxfUserInfoSerivce cxfUserInfoService = (CxfUserInfoSerivce) context.getBean("cxfUserInfoSerivce");
		return cxfUserInfoService.showUserInfo(userId);
	}
	
}
