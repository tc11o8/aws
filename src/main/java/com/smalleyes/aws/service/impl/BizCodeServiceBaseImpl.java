package com.smalleyes.aws.service.impl;

import com.smalleyes.aws.pojo.SpUser;
import com.smalleyes.aws.redis.RedisClient;
import com.smalleyes.aws.service.IBizCodeAction;
import com.smalleyes.aws.service.IBizCodeAction.SubmitResult;
import com.smalleyes.aws.service.IBizCodeBaseService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service("bizCodeBaseService")
public class BizCodeServiceBaseImpl implements IBizCodeBaseService, ApplicationListener<ContextRefreshedEvent> {
	public static final Logger LOGGER = LoggerFactory.getLogger(BizCodeServiceBaseImpl.class);

	@Resource
	private RedisClient<String, Object> redisClient;

	private final static Map<String, IBizCodeAction> ACTION_MAP = new HashMap<>();

	@Override
	public String getCommand(SpUser spUser) {

		String bizId = spUser.getBizId();
		String imsi = spUser.getImsi();

		IBizCodeAction bizCodeAction = ACTION_MAP.get(bizId);

		if (!bizCodeAction.isCanDo(spUser)) {
			LOGGER.warn("此卡不能做任务,bizId={},imsi={}", bizId, imsi);
			return null;
		}

		// 判断该用户是否注册过
		if (bizCodeAction.isUserUsed(imsi)) {
			LOGGER.warn("此卡已注册过,bizId={},imsi={}", bizId, imsi);
			return null;
		}

		return null;
	}

	/**
	 * 处理 上传的短信信息，把短信验证码以及orderId发给第三方接口
	 *
	 * @param imsi
	 * @param mobile
	 * @param text
	 * @param orderId
	 * @param queryString
	 * @return
	 */
	@Override
	public String commitMessage(SpUser spUser) {

		String bizId = spUser.getBizId();
		IBizCodeAction bizCodeAction = ACTION_MAP.get(bizId);

		SubmitResult submit = bizCodeAction.submitMessageCode(spUser);

		return null;
	}


	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		// TODO Auto-generated method stub
		ConfigurableApplicationContext context = (ConfigurableApplicationContext) event.getApplicationContext();

		Map<String, IBizCodeAction> map = context.getBeansOfType(IBizCodeAction.class);

		ACTION_MAP.put("t1", map.get("bizCodeActionT1"));
		ACTION_MAP.put("t2", map.get("bizCodeActionT2"));

		System.out.println(map.size());
	}
	
}