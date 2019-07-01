/**
 * @(#)ITaskHandler.java 2018年4月26日
 */
package com.smalleyes.aws.task.iframe;


/**
 * TODO 请加入本类的说明
 * @author <a href="mailto:284040429@qq.com" >张明俊</a>
 * @version 1.0.0
 */
public interface ITaskHandler<T> {

	void handler(T data);
	
}
