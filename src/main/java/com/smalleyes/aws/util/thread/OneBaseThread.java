/**
 * @(#)OneBaseThread.java 2017年7月10日
 */
package com.smalleyes.aws.util.thread;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO 请加入本类的说明
 * @author <a href="mailto:284040429@qq.com" >张明俊</a>
 * @version 1.0.0
 */
public abstract class OneBaseThread  implements Runnable{
	public static final Logger LOGGER = LoggerFactory.getLogger(OneBaseThread.class);
	//任务打开标记
	private static final String RUN_FLAG = "on";
	
	private String openFlag="off";
	
	public void init(){
		if(RUN_FLAG.equalsIgnoreCase(openFlag)){
			new Thread(this).start();
		}
	}
	
	public String getOpenFlag() {
		return openFlag;
	}

	public void setOpenFlag(String openFlag) {
		this.openFlag = openFlag;
	}
	
}
