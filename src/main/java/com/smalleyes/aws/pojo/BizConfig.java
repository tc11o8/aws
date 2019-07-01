/**
 * @(#)BaseTask.java 2019年3月4日
 */
package com.smalleyes.aws.pojo;

import java.io.Serializable;

/**
 * TODO 业务配置
 * 
 * @author <a href="mailto:284040429@qq.com">张明俊</a>
 * @version 1.0.0
 */
public class BizConfig implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String dayCount;
	private String bizId;
	
	public String getDayCount() {
		return dayCount;
	}
	public void setDayCount(String dayCount) {
		this.dayCount = dayCount;
	}
	public String getBizId() {
		return bizId;
	}
	public void setBizId(String bizId) {
		this.bizId = bizId;
	}
}
