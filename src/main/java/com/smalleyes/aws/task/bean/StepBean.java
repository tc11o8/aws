/**
 * @(#)StepBean.java 2018年5月4日
 */
package com.smalleyes.aws.task.bean;


import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO 请加入本类的说明
 * @author <a href="mailto:284040429@qq.com" >张明俊</a>
 * @version 1.0.0
 */
public class StepBean implements Serializable{
	/**
	 * serialVersionUID TODO 请描述这个变量的含义
	 */
	private static final long serialVersionUID = 5514808017501667745L;

	public static final Logger LOGGER = LoggerFactory.getLogger(StepBean.class);

	private int methodNum;
	private long delayTime;
	private boolean needWaiting;
	
	
	public int getMethodNum() {
		return methodNum;
	}
	public void setMethodNum(int methodNum) {
		this.methodNum = methodNum;
	}
	public long getDelayTime() {
		return delayTime;
	}
	public void setDelayTime(long delayTime) {
		this.delayTime = delayTime;
	}
	public boolean isNeedWaiting() {
		return needWaiting;
	}
	public void setNeedWaiting(boolean needWaiting) {
		this.needWaiting = needWaiting;
	}

	
}
