package com.ethan.gapclient.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ethan.gapclient.web.config.WebsiteApplicationConfig;
import com.ethan.gapclient.wrapper.service.WsUserInfoService;

@Controller
public class DefaultController {

	@Autowired
	private WebsiteApplicationConfig config;
	@Autowired
	private WsUserInfoService wsUserInfoService;
	
	@RequestMapping(value="/")
	public String homePage(HttpServletRequest request, HttpServletResponse response, Model model){
		model.addAttribute("config", config);
		return "index";
	}
	
	@RequestMapping(value="/show.do")
	public String showUserInfo(HttpServletRequest request, HttpServletResponse response, Model model){
		String userDetail = wsUserInfoService.showUserInfo("wolaile");
		model.addAttribute("config", config);
		model.addAttribute("userDetail", userDetail);
		return "userDetail";
	}
}
