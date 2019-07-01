package com.smalleyes.aws.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smalleyes.aws.constants.CacheKey;
import com.smalleyes.aws.redis.RedisClient;
import com.smalleyes.aws.util.AwsLogUtils;

/**
 * 延迟任务定时器
 */
//已停用
// @Service
public class TaskTimingTimer extends BaseTaskTimer {
	public static final Logger LOGGER = LoggerFactory.getLogger(TaskTimingTimer.class);

	@Autowired
	private RedisClient<String, Object> redisClient;

	@Override
	boolean isCanDo() {
		return false;
//		return redisClient.evalSetNx(CacheKey.IMSI_ZSET_KEY_LOCK, 2);
	}

	@Override
	void execute() {

		long endTime = System.currentTimeMillis() - 3000;
		Object[] taskSet = redisClient.rangeAndremoveByScore(CacheKey.IMSI_ZSET_KEY, 0, endTime);
		if (taskSet == null || taskSet.length == 0) {
			return;
		}
		redisClient.putToQueueBatch(CacheKey.IMSI_QUEUE_KEY, taskSet);
		
	}

	@Override
	String threadName() {
		return TaskTimingTimer.class.getSimpleName();
	}

	@Override
	long initSleepTime() {
		return 3000;
	}

	public static void delayTask(String imsi, long delayTime) {
		AwsLogUtils.getRedisClient().putZset(CacheKey.IMSI_ZSET_KEY, imsi, delayTime + System.currentTimeMillis());
	}

}
