package com.smalleyes.aws.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

//@Configuration
@SpringBootConfiguration
public class WebAppConfig extends WebMvcConfigurerAdapter {

	@Autowired
	private AccessInterceptor accessInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
//取消拦截器
//		registry.addInterceptor(accessInterceptor).addPathPatterns("/**");
		super.addInterceptors(registry);

	}
}
