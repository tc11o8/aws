package com.smalleyes.aws.task;

/**
 * @(#)CacheParamManager.java 2017年4月15日
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.smalleyes.aws.mapper.SpParamConfigMapper;
import com.smalleyes.aws.pojo.SpParamConfig;
import com.smalleyes.aws.redis.RedisClient;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存定义字段值管理
 *
 */
@Service
public class CacheParamManager {

	private static Map<String, Object> paramsMap = new ConcurrentHashMap<>();

	private final static Logger logger = LoggerFactory.getLogger(CacheParamManager.class);

	private final static long SLEEP_TIME = 60 * 1000;

	@Resource
	private RedisClient<String, Object> redisClient;

	@Resource
	private SpParamConfigMapper spParamConfigMapper;

	/**
	 * getParamValue:缓存缓存设置的值
	 *
	 * @param key
	 * @return
	 */
	public Object getParamValue(String key) {
		Object value = paramsMap.get(key);
		try {
			if (value == null) {
				value = redisClient.get(key);
				if (value != null) {
					paramsMap.put(key, value);
				}
			}
		} catch (Exception e) {
			logger.error("CacheParamManager getValue error", e);
		}

		if (value == null) {
			logger.error("CacheParamManager_key={},value=null", key);
		}

		return value;
	}

	@PostConstruct
	private void init() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(SLEEP_TIME);
						if (paramsMap != null) {
							String[] keys = (String[]) paramsMap.keySet().toArray(new String[0]);
							for (String key : keys) {
								Object value = redisClient.get(key);
								if (value != null && !StringUtils.isEmpty(value.toString())) {
									paramsMap.put(key, value);
								} else {
									paramsMap.remove(key);
								}
							}
						}
						List<SpParamConfig> list = spParamConfigMapper.getList();
						for (SpParamConfig bean : list) {
							String key = bean.getItemKey();
							String value = bean.getItemValue();
							redisClient.put(key, value);
							paramsMap.put(key, value);
						}
					} catch (Exception e) {
						logger.error("CacheParamManager error ", e);
					}
				}
			}
		}).start();
	}

	public static Map<String, Object> getParamsMap() {
		return Collections.unmodifiableMap(paramsMap);
	}

}
