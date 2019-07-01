/**
 * @(#)TaskExecuteTimer.java 2018年4月26日
 */
package com.smalleyes.aws.task.iframe;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smalleyes.aws.constants.CacheKey;
import com.smalleyes.aws.redis.RedisClient;
import com.smalleyes.aws.task.bean.CommTaskBean;
import com.smalleyes.aws.util.thread.OneQueueThread;

/**
 * 任务处理定时器
 */
@Service
public class TaskExecuteTimer extends OneQueueThread {
	public static final Logger LOGGER = LoggerFactory.getLogger(TaskExecuteTimer.class);

	@Autowired
	private RedisClient<String, Object> redisClient;

	private ThreadPoolExecutor threadPool;

	private String queueKey;

	private BlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<Runnable>(100);

	@Override
	public void prepare() {
		queueKey = CacheKey.COMM_TASK_QUEUE;
		threadPool = new ThreadPoolExecutor(30, 30, 0, TimeUnit.SECONDS, blockingQueue,
				new ThreadPoolExecutor.CallerRunsPolicy());

		super.setOpenFlag(CacheKey.OPEN);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void hander() throws Exception {

		if (blockingQueue.size() > 90) {
			this.setSleepTime(2000);
			return;
		}

		// 从队列中弹出一个任务
		final CommTaskBean task = (CommTaskBean) redisClient.pollFromQueue(queueKey);
		if (task == null) {
			LOGGER.debug("task=null,from COMM_TASK_QUEUE");
			this.setSleepTime(3000);
			return;
		}

		try {
			threadPool.execute(new Runnable() {
				@Override
				public void run() {
					task.start();
				}
			});
		} catch (Exception e) {
			LOGGER.error("STEP_TASK_ERROR", e);
		}

	}

	public BlockingQueue<Runnable> getBlockingQueue() {
		return blockingQueue;
	}

}
