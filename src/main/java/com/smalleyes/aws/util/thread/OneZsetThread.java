/**
 * @(#)OneBaseThread.java 2017年7月10日
 */
package com.smalleyes.aws.util.thread;


import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.smalleyes.aws.redis.RedisClient;

/**
 * TODO 请加入本类的说明
 * @author <a href="mailto:284040429@qq.com" >张明俊</a>
 * @version 1.0.0
 */
public abstract class OneZsetThread  implements Runnable{
	public static final Logger LOGGER = LoggerFactory.getLogger(OneZsetThread.class);
	//任务打开标记
	private static final String RUN_FLAG = "on";
	
	private String key;
	private String openFlag="off";
	private long defaultSleepTime=10;
	private long sleepTime=1000;
	
	@Autowired
	protected RedisClient<String,Object> redisClient;
	
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
			String key=getKey();
			boolean hasGetLock=false;
			sleepTime=defaultSleepTime;
			
			try{
				if(!redisClient.evalSetNx(key, 6)){
					sleepTime=1000;
					continue;
				}
				hasGetLock=true;
				
				hander();
			}catch(Exception e){
				LOGGER.error("",e);
			}finally{
				if(hasGetLock){
					try{
						redisClient.remove(key);
					}catch(Exception e){
						LOGGER.error("OneZsetThread redis error",e);
					}
				}
				
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

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public long getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(long sleepTime) {
		if(this.sleepTime==defaultSleepTime){
			this.sleepTime = sleepTime;
		}else{
			if(sleepTime<this.sleepTime){
				this.sleepTime=sleepTime;
			}
		}
	}
	
	/**
	 * 重置时间，取较小值
	 * @author <a href="mailto:284040429@qq.com" >张明俊</a>
	 * @param sleepTime
	 */
	public void reSetSleepTime(long sleepTime) {
		if(this.sleepTime==defaultSleepTime){
			this.sleepTime = sleepTime;
			return;
		}
		
		if(this.sleepTime<=sleepTime){
			return;
		}
		
		this.sleepTime = sleepTime;
	}

	public long getDefaultSleepTime() {
		return defaultSleepTime;
	}

	public void setDefaultSleepTime(long defaultSleepTime) {
		this.defaultSleepTime = defaultSleepTime;
	}
	
}
