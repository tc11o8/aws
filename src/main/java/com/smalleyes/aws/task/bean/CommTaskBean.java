/**
 * @(#)CommTaskBean.java 2018年4月26日
 */
package com.smalleyes.aws.task.bean;


import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smalleyes.aws.task.iframe.ITaskHandler;


/**
 * TODO 请加入本类的说明
 * @author <a href="mailto:284040429@qq.com" >张明俊</a>
 * @version 1.0.0
 */
public class CommTaskBean<T> implements Serializable{
	/**
	 * serialVersionUID TODO 请描述这个变量的含义
	 */
	private static final long serialVersionUID = 7254374050401034491L;
	
	public static final Logger LOGGER = LoggerFactory.getLogger(CommTaskBean.class);

	/**
	 * 具体的任务处理器
	 */
	private ITaskHandler<T> taskHandler;
	
	private T data;
	
	private long time;
	
	public CommTaskBean(ITaskHandler<T> taskHandler,T data){
		this.taskHandler=taskHandler;
		this.data=data;
	}

	public void start(){
		taskHandler.handler(data);
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
	
}
