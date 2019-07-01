package com.smalleyes.aws.task;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolShare {

	public static final int QUEUE_SIZE_LIMIT = 50;
	public static final int QUEUE_SIZE_10 = 10;

	// 线程数量
	private static final int corePoolSize = 3;
	private static final int maxPoolSize = 3;

	// 任务队列最大长度 100
	private static final BlockingQueue<Runnable> blockQueue = new ArrayBlockingQueue<Runnable>(50);

	private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(corePoolSize, maxPoolSize, 1000,
			TimeUnit.MICROSECONDS, blockQueue);

	static {
		// 线程池策略为 CallerRunsPolicy，当前线程运行任务
		threadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
	}

	public static int queueSize() {
		return blockQueue.size();
	}
	
	public static void clearQueue() {
		blockQueue.clear();
	}

	public static void executeTask(Runnable task) {
		threadPool.execute(task);
	}

	public static void setPoolSize(int size) {
		threadPool.setCorePoolSize(size);
		threadPool.setMaximumPoolSize(size);
	}
	
	public static int getPoolSize() {
		return threadPool.getPoolSize();
	}
	
	public static int getMaximumPoolSize() {
		return threadPool.getMaximumPoolSize();
	}
}
