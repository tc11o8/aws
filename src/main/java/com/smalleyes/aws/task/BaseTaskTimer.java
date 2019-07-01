package com.smalleyes.aws.task;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseTaskTimer {

	final private static Logger logger = LoggerFactory.getLogger(BaseTaskTimer.class);

	// @Resource
	// protected CacheParamManager cacheParamManager;
	
	@PostConstruct
	private void init() {

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(initSleepTime());
						if (isCanDo()) {
							execute();
						}
					} catch (Exception e) {
						logger.error("baseTaskTimer_error", e);
					}
				}
			}
		}, threadName()).start();

	}

	/**
	 * isCanDo:是否可以执行
	 * 
	 * @return
	 */
	abstract boolean isCanDo();

	/**
	 * execute:任务执行
	 *
	 */
	abstract void execute();

	/**
	 * 线程名
	 * 
	 * @return
	 */
	abstract String threadName();

	/**
	 * initSleepTime: 默认一分钟
	 *
	 * @return
	 */
	abstract long initSleepTime();

}
