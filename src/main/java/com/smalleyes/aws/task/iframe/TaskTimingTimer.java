package com.smalleyes.aws.task.iframe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.smalleyes.aws.constants.CacheKey;
import com.smalleyes.aws.task.bean.CommTaskBean;
import com.smalleyes.aws.util.AwsLogUtils;
import com.smalleyes.aws.util.thread.OneZsetThread;


/**
 * 延迟任务定时器
 * @author <a href="mailto:284040429@qq.com" >张明俊</a>
 * @version 1.0.0
 */
@Service
public class TaskTimingTimer extends OneZsetThread{
	public static final Logger LOGGER = LoggerFactory.getLogger(TaskTimingTimer.class);

	private static final String ZSET_KEY=CacheKey.COMM_TIMING_ZSET;
	private static final String QUEUE_Key=CacheKey.COMM_TASK_QUEUE;
	
	@Override
	public void prepare() {
		super.setOpenFlag(CacheKey.OPEN);
		super.setKey(ZSET_KEY+"_LOCK");
	}
	
	@Override
	public void hander() throws Exception {
		long endTime=System.currentTimeMillis()-1000;
		
		Object[] taskSet=redisClient.rangeAndremoveByScore(ZSET_KEY, 0,endTime);
		if(taskSet==null||taskSet.length==0){
			this.setSleepTime(1000);
			return;
		}
		redisClient.putToQueueBatch(QUEUE_Key, taskSet);
		
	}
	
	public static void delayTask(CommTaskBean<?> task,long delayTime){
		AwsLogUtils.getRedisClient().putZset(ZSET_KEY, task, delayTime+System.currentTimeMillis());
	}
	
}
