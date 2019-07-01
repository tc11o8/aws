package com.smalleyes.aws.aop;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.smalleyes.aws.common.DelayExecuteBuffer;
import com.smalleyes.aws.pojo.RequestLog;


//@Aspect
//@Component
public class ControllerAspect {

	private static final Logger LOGGER = LoggerFactory.getLogger(ControllerAspect.class);
//	@Autowired
	private DelayExecuteBuffer<RequestLog> mtkRequestLogBuffer;

	// 申明一个切点 里面是 execution表达式
	@Pointcut("execution(public * com.smalleyes.aws.controller.*.*(..))")
	private void controllerAspect() {
	}

	// 接口请求日志打印
	@Around(value = "controllerAspect()")
	public Object methodAround(ProceedingJoinPoint joinPoint) throws Throwable {
		long beginTime = System.currentTimeMillis();
		Object result = null;
		RequestLog requestLog = new RequestLog();
		try {
			result = joinPoint.proceed();
			JSONObject resp = (JSONObject) JSON.toJSON(result);
			requestLog.setCode(resp.getString("code"));
			if(resp.containsKey("data")) {
				requestLog.setRespData(resp.getString("data"));
			}
			
		} catch (Throwable throwable) {
			LOGGER.error("接口拦截获取返回结果异常", throwable);
		}
		LOGGER.info("请求类方法:" + joinPoint.getSignature());
		LOGGER.info("请求类方法参数:" + Arrays.toString(joinPoint.getArgs()));
		long endTime = System.currentTimeMillis();
		LOGGER.info("耗时：" + (endTime - beginTime));
		
		requestLog.setCost(endTime - beginTime);
		mtkRequestLogBuffer.add(requestLog);
		LOGGER.info("返回结果:" + JSON.toJSONString(result));
		return result;
	}

	@Before(value = "controllerAspect()")
	public void methodBefore(JoinPoint joinPoint) {
	}
}
