package com.smalleyes.aws.aop;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.netflix.hystrix.util.HystrixRollingNumber;
import com.netflix.hystrix.util.HystrixRollingNumberEvent;
import com.smalleyes.aws.annotation.STATS;

/**
 * 用于统计接口方法的QPS
 * 
 * @author <a href="mailto:284040429@qq.com" >张明俊</a>
 * @version 0.0.1
 *
 */
@Aspect
@Component
public class QpsAspect {

	private static final Logger LOGGER = LoggerFactory.getLogger(QpsAspect.class);

	//10秒，分10次
	private static HystrixRollingNumber counter = new HystrixRollingNumber(10000, 10);

	public static long get10Second() {
		long count = counter.getRollingSum(HystrixRollingNumberEvent.SUCCESS);
		return count;
	}

	// 申明一个切点 里面是 execution表达式
	@Pointcut("execution(public * com.smalleyes.aws.controller.*.*(..))")
	private void controllerAspect() {
	}

	// 接口请求日志打印
	@Around(value = "controllerAspect()")
	public Object methodAround(ProceedingJoinPoint joinPoint) throws Throwable {

		Object result = null;
		try {

//			String targetClassName = joinPoint.getTarget().getClass().getName();
//			String methodName = joinPoint.getSignature().getName();
//			String methodKey = String.format("%s_%s", targetClassName, methodName);
//			if (COMMAND_CONTROLLER_GET.equals(methodKey)) {
//				doIncr();
//			}
			
			MethodSignature signature = (MethodSignature) joinPoint.getSignature();
			Method method = signature.getMethod();

			STATS stats = method.getAnnotation(STATS.class);
			if (stats != null) {
				doIncr2();
			}

			result = joinPoint.proceed();

		} catch (Throwable throwable) {
			LOGGER.error("接口拦截获取返回结果异常", throwable);
		}
		return result;
	}

	@Before(value = "controllerAspect()")
	public void methodBefore(JoinPoint joinPoint) {
	}

	private static void doIncr2() {
		counter.increment(HystrixRollingNumberEvent.SUCCESS);
	}
	


	public static void main(String[] args) {
		
//		counter.increment(HystrixRollingNumberEvent.SUCCESS);
//		counter.increment(HystrixRollingNumberEvent.SUCCESS);
//		counter.increment(HystrixRollingNumberEvent.SUCCESS);
//		
//		
//		for (int i = 0; i < 100; i++) {
//			counter.increment(HystrixRollingNumberEvent.SUCCESS);
//		}
//		
//		long ff = counter.getRollingSum(HystrixRollingNumberEvent.SUCCESS);
//		System.out.println(ff);

		// doIncr();

		Thread thread1 = new Thread(() -> {

			while (!Thread.currentThread().isInterrupted()) {
				doIncr2();
			}
			System.out.println("thread1 stopped");

		});

		Thread thread2 = new Thread(() -> {

			while (!Thread.currentThread().isInterrupted()) {
				doIncr2();
			}
			System.out.println("thread2 stopped");

		});

		thread1.start();
		thread2.start();

		System.out.println("thread started!");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		thread1.interrupt();
		thread2.interrupt();
		System.out.println("thread end!");

		
//		long[] array = counter.getValues(HystrixRollingNumberEvent.SUCCESS);
//		for (long l : array) {
//			System.out.println(l);
//		}
		
		long dd = counter.getRollingSum(HystrixRollingNumberEvent.SUCCESS);
		System.out.println(dd);

	}

}
