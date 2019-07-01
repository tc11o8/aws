package com.smalleyes.aws.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.druid.support.spring.stat.annotation.Stat;
import com.google.gson.Gson;
import com.smalleyes.aws.annotation.STATS;
import com.smalleyes.aws.constants.CacheKey;
import com.smalleyes.aws.pojo.FbMessage;
import com.smalleyes.aws.redis.RedisClient;
import com.smalleyes.aws.util.ResponseUtils;

@Controller
@RequestMapping("/message")
public class MessageController {

	public static final Logger logger = LoggerFactory.getLogger(MessageController.class);

	@Resource
	private RedisClient<String, Object> redisClient;

	public static final int COUNT = 100;
	
	@STATS
	@RequestMapping("/get")
	@ResponseBody
	public void get(HttpServletResponse response) {

		// 用于测试
//		for (int i = 0; i < 5; i++) {
//			FbMessage fbMessage = new FbMessage();
//			fbMessage.setMessage("ewe");
//			fbMessage.setMobile("15234325");
//			fbMessage.setReplyTime(2543265L);
//			redisClient.putToQueue(CacheKey.FB_MESSAGE_QUEUE, fbMessage);
//		}

		// 一次取最多一百条
		int i = 0;
		List<FbMessage> list = new ArrayList<>(16);
		while (i < COUNT) {
			FbMessage fbMessage = (FbMessage) redisClient.pollFromQueue(CacheKey.FB_MESSAGE_QUEUE);
			if (fbMessage == null) {
				break;
			}
			list.add(fbMessage);
			++i;
		}

		Gson gson = new Gson();
		String json = gson.toJson(list);
		logger.info("fb_result=" + json);

		ResponseUtils.renderText(response, json);
	}

	@RequestMapping("/view")
	@ResponseBody
	public void view(HttpServletResponse response) {
		StringBuilder sb = new StringBuilder(128);
		sb.append("\n\t");
		sb.append("message_queue=").append(redisClient.getQueueSize(CacheKey.FB_MESSAGE_QUEUE));

		ResponseUtils.renderText(response, sb.toString());
	}

}