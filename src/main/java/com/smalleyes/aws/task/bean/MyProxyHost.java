package com.smalleyes.aws.task.bean;


import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO 请加入本类的说明
 * @author <a href="mailto:284040429@qq.com" >张明俊</a>
 * @version 1.0.0
 */
public class MyProxyHost implements Serializable{
	/**
	 * serialVersionUID TODO 请描述这个变量的含义
	 */
	private static final long serialVersionUID = 1241825286089885735L;

	public static final Logger LOGGER = LoggerFactory.getLogger(MyProxyHost.class);

	private String proxyIp;
	private int port;
	private String ipContent;
	private String md5;
	private String searchHost;
	
	public String getProxyIp() {
		return proxyIp;
	}
	public void setProxyIp(String proxyIp) {
		this.proxyIp = proxyIp;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getIpContent() {
		return ipContent;
	}
	public void setIpContent(String ipContent) {
		this.ipContent = ipContent;
	}
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	public String getSearchHost() {
		return searchHost;
	}
	public void setSearchHost(String searchHost) {
		this.searchHost = searchHost;
	}

}
