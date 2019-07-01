package com.smalleyes.aws.task.bean;


import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProxyBean implements Serializable{
	/**
	 * serialVersionUID TODO 请描述这个变量的含义
	 */
	private static final long serialVersionUID = -5124331360001074336L;
	public static final Logger LOGGER = LoggerFactory.getLogger(ProxyBean.class);

	private String proxyImsi;
	private String proxyProvince;
	private String proxyBizId;
	private String proxyCity;
	
	private MyProxyHost currProxyHost;

	public MyProxyHost getCurrProxyHost() {
		return currProxyHost;
	}

	public void setCurrProxyHost(MyProxyHost currProxyHost) {
		this.currProxyHost = currProxyHost;
	}

	public String getProxyImsi() {
		return proxyImsi;
	}

	public void setProxyImsi(String proxyImsi) {
		this.proxyImsi = proxyImsi;
	}

	public String getProxyProvince() {
		return proxyProvince;
	}

	public void setProxyProvince(String proxyProvince) {
		this.proxyProvince = proxyProvince;
	}

	public String getProxyBizId() {
		return proxyBizId;
	}

	public void setProxyBizId(String proxyBizId) {
		this.proxyBizId = proxyBizId;
	}

	public String getProxyCity() {
		return proxyCity;
	}

	public void setProxyCity(String proxyCity) {
		this.proxyCity = proxyCity;
	}
	
}
