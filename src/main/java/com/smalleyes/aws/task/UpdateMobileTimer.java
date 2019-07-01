/**
 * @(#)BaseTask.java 2019年3月4日
 */
package com.smalleyes.aws.task;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smalleyes.aws.constants.CacheKey;
import com.smalleyes.aws.redis.RedisClient;
import com.smalleyes.aws.task.runnable.UpdateMobileTask;

/**
 * TODO 从队列里取imsi_mobile，更新手机号到数据库
 * 
 * @author <a href="mailto:284040429@qq.com" >张明俊</a>
 * @version 1.0.0
 */
// for_branch
//@Service("updateMobileTimer")
public class UpdateMobileTimer extends BaseTaskTimer {

	public static final Logger LOGGER = LoggerFactory.getLogger(UpdateMobileTimer.class);

	@Autowired
	private RedisClient<String, String> redisClient;

	// 一次处理20条手机号
	private final static int BATCH_SIZE = 20;

	private long sleepTime = 2000L;

	@Override
	boolean isCanDo() {
		return true;
	}

	@Override
	void execute() {

		while (ThreadPoolShare.queueSize() >= ThreadPoolShare.QUEUE_SIZE_LIMIT) {
			LOGGER.warn("更新手机号任务排满");
			// 当前线程休眠3秒
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		List<String> imsiMobileList = new ArrayList<>(BATCH_SIZE);
		for (int i = 0; i < BATCH_SIZE; i++) {
			String imsiMobile = (String) redisClient.pollFromQueue(CacheKey.IMSI_MOBILE_QUEUE);
			if (imsiMobile == null || imsiMobile.length() == 0) {
				break;
			}
			imsiMobileList.add(imsiMobile);
		}

		int size = imsiMobileList.size();

		if (size == 0) {
			return;
		}

		UpdateMobileTask updateMobileTask = new UpdateMobileTask();
		ThreadPoolShare.executeTask(updateMobileTask);
	}

	@Override
	String threadName() {
		return UpdateMobileTimer.class.getSimpleName();
	}

	@Override
	long initSleepTime() {
		return sleepTime;
	}

}
