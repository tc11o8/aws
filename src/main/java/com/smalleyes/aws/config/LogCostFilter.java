package com.smalleyes.aws.config;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogCostFilter implements Filter {

	public static final Logger LOGGER = LoggerFactory.getLogger(LogCostFilter.class);

	@Override
	public void destroy() {

	}

	@Override
	public void init(javax.servlet.FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		long start = System.currentTimeMillis();
		chain.doFilter(request, response);
		LOGGER.info("method_execute_coast={}ms", (System.currentTimeMillis() - start));
	}
}