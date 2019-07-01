package com.smalleyes.aws.controller;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.LongAdder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smalleyes.aws.aop.QpsAspect;
import com.smalleyes.aws.constants.CacheKey;
import com.smalleyes.aws.redis.RedisClient;
import com.smalleyes.aws.task.CacheParamManager;
import com.smalleyes.aws.util.ResponseUtils;

@Controller
@RequestMapping("/monitor")
public class MonitorController {

	public static final Logger logger = LoggerFactory.getLogger(MonitorController.class);

	@Resource
	private RedisClient<String, String> redisClient;

	@Autowired
	private CacheParamManager cacheParamManager;

	@RequestMapping("/status")
	@ResponseBody
	public void status(HttpServletResponse response) {
		StringBuilder sb = new StringBuilder(128);
		sb.append("\n\t");
		sb.append("/showCitys,查看黑名单城市");
		sb.append("\n\t");
		sb.append("/showStatsMap,查看QPS统计");
		sb.append("\n\t");
		sb.append("imsi_mobile_queue=").append(redisClient.getQueueSize(CacheKey.IMSI_MOBILE_QUEUE));
		sb.append("\n\t");
		sb.append("startId=").append(redisClient.get(CacheKey.MTK_CITYID_ID));
		sb.append("\n\t");
		sb.append("showParamConfig");

		ResponseUtils.renderText(response, sb.toString());
	}

	/*
	 * 设置黑名单城市
	 */
	@RequestMapping("/setCity")
	@ResponseBody
	public String setCity(Integer cityId) {
		if (cityId == null) {
			return "null";
		}
		String city = cityId.toString();
		redisClient.putHash(CacheKey.MTK_FORBID_CITYID, city, "");
		return "ok";
	}

	@RequestMapping("/removeCity")
	@ResponseBody
	public String removeCity(Integer cityId) {
		if (cityId == null) {
			return "null";
		}
		String city = cityId.toString();
		redisClient.removeHash(CacheKey.MTK_FORBID_CITYID, city);
		return "ok";
	}

	@RequestMapping("/showCitys")
	@ResponseBody
	public void showCitys(HttpServletResponse response) {
		Map<Object, Object> map = redisClient.getHash(CacheKey.MTK_FORBID_CITYID);
		StringBuilder sb = new StringBuilder();

		sb.append("\n\t");
		sb.append("/setCity?cityId=,添加黑名单城市 ");
		sb.append("\n\t");
		sb.append("/removeCity?cityId=,删除黑名单城市 ");

		if (map != null) {
			Set<Object> keySet = map.keySet();
			Iterator<Object> it = keySet.iterator();
			while (it.hasNext()) {
				String city = (String) it.next();
				sb.append("\n\t");
				sb.append("cityId=" + city);
			}
		}

		ResponseUtils.renderText(response, sb.toString());
	}

	@RequestMapping("/resetCity")
	@ResponseBody
	public String resetCity() {

		redisClient.remove(CacheKey.MTK_FORBID_CITYID);
		String[] cityIds = new String[] { "233", "237", "325", "341", "326" };

		for (String cityId : cityIds) {
			redisClient.putHash(CacheKey.MTK_FORBID_CITYID, cityId, "");
		}
		return "ok";
	}

	@RequestMapping("/showParamConfig")
	@ResponseBody
	public void showParamConfig(HttpServletResponse response) {

		Map<String, Object> map = cacheParamManager.getParamsMap();

		StringBuilder sb = new StringBuilder();
		if (map != null) {
			Set<Map.Entry<String, Object>> entry = map.entrySet();
			Iterator<Map.Entry<String, Object>> it = entry.iterator();
			while (it.hasNext()) {
				Map.Entry<String, Object> obj = it.next();
				sb.append("\n\t");
				sb.append("key=" + obj.getKey());
				sb.append(",vale=" + obj.getValue().toString());
			}
		}

		ResponseUtils.renderText(response, sb.toString());
	}
	
	@RequestMapping("/showStatsMap")
	@ResponseBody
	public void showStatsMap(HttpServletResponse response) {
		
		Map<Integer, LongAdder> map = QpsAspect.getStatsMap();

		StringBuilder sb = new StringBuilder();
		if (map != null) {
			Set<Map.Entry<Integer, LongAdder>> entry = map.entrySet();
			Iterator<Map.Entry<Integer, LongAdder>> it = entry.iterator();
			while (it.hasNext()) {
				Map.Entry<Integer, LongAdder> obj = it.next();
				sb.append("\n\t");
				sb.append("key=" + obj.getKey());
				sb.append(",vale=" + obj.getValue().toString());
			}
		}

		ResponseUtils.renderText(response, sb.toString());
	}
	
	@RequestMapping("/showQps")
	@ResponseBody
	public void showQps(HttpServletResponse response) {
		long qps10Second = QpsAspect.get10Second();
		ResponseUtils.renderText(response, "10秒请求量"+qps10Second);
	}
	
}