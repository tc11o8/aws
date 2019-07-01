/**
 * @(#)BaseTask.java 2019年3月4日
 */
package com.smalleyes.aws.pojo;

import java.io.Serializable;

/**
 * TODO Fb渠道 短信消息
 * 
 * @author <a href="mailto:284040429@qq.com">张明俊</a>
 * @version 1.0.0
 */
public class FbMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String mobile;
	private String message;
	private Long replyTime;
	
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Long getReplyTime() {
		return replyTime;
	}
	public void setReplyTime(Long replyTime) {
		this.replyTime = replyTime;
	}
}
