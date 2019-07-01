/**
 * @(#)MiguMusicLogUtils.java 2018年4月28日
 */
package com.smalleyes.aws.util;

import com.smalleyes.aws.common.DelayExecuteBuffer;
import com.smalleyes.aws.pojo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smalleyes.aws.redis.RedisClient;

@Service
public class AwsLogUtils {

	public static final Logger LOGGER = LoggerFactory.getLogger(AwsLogUtils.class);

	private static DelayExecuteBuffer<UploadResult> uploadResultBuffer;

	private static RedisClient<String, Object> redisClient;

	public static void addUploadResult(UploadResult result) {
		uploadResultBuffer.add(result);
	}

	public static RedisClient<String, Object> getRedisClient() {
		return redisClient;
	}

	@Autowired
	public void setUploadResultBuffer(DelayExecuteBuffer<UploadResult> uploadResultBuffer) {
		AwsLogUtils.uploadResultBuffer = uploadResultBuffer;
	}

	@Autowired
	public void setRedisClient(RedisClient<String, Object> redisClient) {
		AwsLogUtils.redisClient = redisClient;
	}

}
