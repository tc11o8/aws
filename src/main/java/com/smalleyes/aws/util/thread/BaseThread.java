/**
 * @(#)BaseTask.java 2017年2月15日
 */
package com.smalleyes.aws.util.thread;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 任务公共开关控制类
 * @author <a href="mailto:284040429@xingbook.com" >张明俊</a>
 * @version 1.0.0
 */
public abstract class BaseThread implements Runnable{
	public static final Logger LOGGER = LoggerFactory.getLogger(BaseThread.class);
	//任务打开标记
	private static final String RUN_FLAG = "on";
	
	private String openFlag="off";
	/**
	 * 循环时间 ，单位毫秒
	 */
	private long cycleTime =15*1000;
	
	public void init(){
		if(RUN_FLAG.equalsIgnoreCase(openFlag)){
			new Thread(this).start();
		}
	}

	public void run(){
		while(true){
			try{
				start();
			}catch(Exception e){
				LOGGER.error("",e);
			}finally{
				try {
					Thread.sleep(cycleTime);
				} catch (InterruptedException e) {
					LOGGER.error("thead.sleep error",e);
				}
			}
		}
	}
	
	public abstract void start();
	
	public String getOpenFlag() {
		return openFlag;
	}

	public void setOpenFlag(String openFlag) {
		this.openFlag = openFlag;
	}

	public long getCycleTime() {
		return cycleTime;
	}

	public void setCycleTime(long cycleTime) {
		this.cycleTime = cycleTime;
	}
	
}
