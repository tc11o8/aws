package com.smalleyes.aws.task;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smalleyes.aws.constants.CacheKey;
import com.smalleyes.aws.mapper.SpBizCodeMapper;
import com.smalleyes.aws.pojo.SpBizCode;
import com.smalleyes.aws.redis.RedisClient;

/**
 * 业务定时器更新缓存
 * 
 * @author <a href="mailto:284040429@qq.com" >张明俊</a>
 * @version 0.0.1
 *
 */
// for_branch
@Service("bizCodeCacheTimer")
public class BizCodeCacheTimer extends BaseTaskTimer {

	public static final Logger LOGGER = LoggerFactory.getLogger(BizCodeCacheTimer.class);
	   
	@Autowired
	private SpBizCodeMapper spBizCodeMapper;

	@Autowired
	private RedisClient<String, Object> redisClient;

	private final static long MIN_5 = 10 * 1000;

	@Override
	boolean isCanDo() {
		return redisClient.evalSetNx(CacheKey.BIZ_CODE_LOCK, 60);
	}

	@Override
	void execute() {

//		String key = "biz_id";
//		String value = "15157009871";
//		redisClient.addByBloomFilter(bloomFilterHelper, key, value);
//		redisClient.includeByBloomFilter(bloomFilterHelper, key, value);
		
		
		LOGGER.info("bizCodeCacheTimer开始执行");

		List<SpBizCode> list = spBizCodeMapper.getList();
		redisClient.put(CacheKey.BIZ_CODE_CACHE, list);

		LOGGER.info("bizCodeCacheTimer执行结束");
	}

	@Override
	String threadName() {
		return BizCodeCacheTimer.class.getSimpleName();
	}

	@Override
	public long initSleepTime() {
		return MIN_5;
	}

}
