/**
 * @(#)BaseTask.java 2019年3月4日
 */
package com.smalleyes.aws.pojo;

import java.io.Serializable;

/**
 * 
 * @author <a href="mailto:284040429@qq.com">张明俊</a>
 * @version 1.0.0
 */
public class BizUser implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String mobile;
	private String name;
	
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
