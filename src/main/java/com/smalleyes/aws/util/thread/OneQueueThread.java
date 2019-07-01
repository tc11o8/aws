/**
 * @(#)OneBaseThread.java 2017年7月10日
 */
package com.smalleyes.aws.util.thread;


import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO 请加入本类的说明
 * @author <a href="mailto:284040429@qq.com" >张明俊</a>
 * @version 1.0.0
 */
public abstract class OneQueueThread  implements Runnable{
	public static final Logger LOGGER = LoggerFactory.getLogger(OneQueueThread.class);
	//任务打开标记
	private static final String RUN_FLAG = "on";
	
	private String openFlag="off";
	private long defaultSleepTime=10;
	private long sleepTime=10;
	
	@PostConstruct
	public void init(){
		prepare();
		if(RUN_FLAG.equalsIgnoreCase(openFlag)){
			new Thread(this).start();
		}
	}
	
	@Override
	public void run(){
		while(true){
			sleepTime=defaultSleepTime;
			try{
				hander();
			}catch(Exception e){
				LOGGER.error("OneQueueThread error",e);
			}finally{
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					LOGGER.error("Thread sleep error",e);
				}
			}
		}
	}
	
	public abstract void prepare();
	
	public abstract void hander() throws Exception;
	
	public String getOpenFlag() {
		return openFlag;
	}

	public void setOpenFlag(String openFlag) {
		this.openFlag = openFlag;
	}

	public long getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(long sleepTime) {
		this.sleepTime = sleepTime;
	}

	public long getDefaultSleepTime() {
		return defaultSleepTime;
	}

	public void setDefaultSleepTime(long defaultSleepTime) {
		this.defaultSleepTime = defaultSleepTime;
	}
	
}
