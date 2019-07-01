package com.smalleyes.aws.task.iframe;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.smalleyes.aws.constants.CacheKey;
import com.smalleyes.aws.task.bean.CommTaskBean;
import com.smalleyes.aws.util.AwsLogUtils;
import com.smalleyes.aws.util.thread.OneZsetThread;

@Service
public class WaitingTaskTimer extends OneZsetThread {
	public static final Logger LOGGER = LoggerFactory.getLogger(WaitingTaskTimer.class);

	@Override
	public void prepare() {
		super.setOpenFlag(CacheKey.OPEN);
		super.setKey(CacheKey.WAITING_TASK_LOCK);
	}

	@Override
	public void hander() throws Exception {

		String imsi = (String) redisClient.pollFromQueue(CacheKey.FB_MESSAGE_QUEUE);
		if (imsi != null) {

			Map<Object, Object> keyMap = redisClient.getHash(CacheKey.WAITING_HASH_KEY);

			if (keyMap == null || keyMap.isEmpty()) {
				return;
			}

			Object object = keyMap.get(imsi);
			if (object != null) {
				LOGGER.info("验证码回调放入任务队列");
				// 放入任务队列
				CommTaskBean<?> task = (CommTaskBean<?>) object;
				redisClient.putToQueue(CacheKey.COMM_TASK_QUEUE, task);
			}

		}
	}

	public static void waitingTask(String imsi, CommTaskBean<?> task) {
		task.setTime(System.currentTimeMillis());
		AwsLogUtils.getRedisClient().putHash(CacheKey.WAITING_HASH_KEY, imsi, task);
	}

}
