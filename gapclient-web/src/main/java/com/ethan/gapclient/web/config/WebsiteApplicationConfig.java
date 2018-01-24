package com.ethan.gapclient.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

@Controller
public class WebsiteApplicationConfig {
	@Value(value="${domain.host}")
	private String domainHost;

	public String getDomainHost() {
		return domainHost;
	}
	
}
