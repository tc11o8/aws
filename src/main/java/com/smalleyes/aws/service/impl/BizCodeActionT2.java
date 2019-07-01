package com.smalleyes.aws.service.impl;

import com.smalleyes.aws.pojo.SpUser;
import com.smalleyes.aws.redis.RedisClient;
import com.smalleyes.aws.service.IBizCodeAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Service("bizCodeActionT2")
public class BizCodeActionT2 implements IBizCodeAction {

	public static final Logger LOGGER = LoggerFactory.getLogger(BizCodeActionT2.class);

	@Resource
	private RedisClient<String, Object> redisClient;

	@Override
	public boolean isCanDo(SpUser spUser) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isUserUsed(String imsi) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean submitMobile(SpUser spUser) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void saveUser(String imsi, String mobile) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getUploadUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SubmitResult submitMessageCode(SpUser spUser) {
		// TODO Auto-generated method stub
		return null;
	}

	
}