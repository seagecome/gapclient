package com.ethan.gapclient.web.controller.reg;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ethan.gapclient.biz.service.UserInfoService;
import com.ethan.gapclient.dal.model.GapUserInfo;
import com.ethan.gapclient.dal.model.UserLoginInfo;
import com.ethan.gapclient.web.config.WebsiteApplicationConfig;

@Controller
public class UserRegisterController {
	@Autowired
	private WebsiteApplicationConfig config;
	@Autowired
	private UserInfoService userInfoService;
	
	
	@RequestMapping(value="/reg/userReg.do")
	public String homePage(HttpServletRequest request, HttpServletResponse response, Model model){
		model.addAttribute("config", config);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		
		
		GapUserInfo gapUserInfo = new GapUserInfo();
		gapUserInfo.setCertId("310522198509021234");
		gapUserInfo.setUserAddress("shanghaipudong");
		gapUserInfo.setUserId(sdf.format(new Date()));
		gapUserInfo.setUserMobile("18612345678");
		gapUserInfo.setUserName("JIM");
		
		userInfoService.addUserInfo(gapUserInfo);
		
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("loginId", 1);
		UserLoginInfo userLoginInfo = userInfoService.queryUserInfo(queryMap);
		if(userLoginInfo == null){
			model.addAttribute("accountId", "未查询出日志");
			model.addAttribute("accountType", "未查询出日志");
		}else{
			model.addAttribute("accountId", userLoginInfo.getAccountId());
			model.addAttribute("accountType", userLoginInfo.getAccountType());
		}
		model.addAttribute("userLoginInfo", userLoginInfo==null?"":userLoginInfo);
		
		return "loginInfoShow";
	}
}
