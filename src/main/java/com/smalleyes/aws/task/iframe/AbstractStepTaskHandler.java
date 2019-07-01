/**
 * @(#)AbstractStepTaskHandler.java 2018年4月26日
 */
package com.smalleyes.aws.task.iframe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smalleyes.aws.annotation.TaskStep;
import com.smalleyes.aws.task.bean.CommTaskBean;
import com.smalleyes.aws.task.bean.StepBean;
import com.smalleyes.aws.task.bean.TaskStepBean;


/**
 * 步骤任务执行器
 */
public abstract class AbstractStepTaskHandler<T extends TaskStepBean> implements ITaskHandler<T> {
	public static final Logger LOGGER = LoggerFactory.getLogger(AbstractStepTaskHandler.class);

	public void handler(T data) {
		Method[] methodArr = this.getClass().getDeclaredMethods();
		if (methodArr == null || methodArr.length < 1) {
			LOGGER.error("获取不到类方法,丢弃任务");
			return;
		}

		Map<Integer, Method> methodMap = new HashMap<Integer, Method>();
		for (Method method : methodArr) {
			TaskStep step = method.getAnnotation(TaskStep.class);
			if (step == null) {
				continue;
			}
			methodMap.put(step.value(), method);
		}

		while (!data.canFinish()) {
			StepBean stepBean = data.getCurrStep();
			Method method = methodMap.get(stepBean.getMethodNum());
			if (method == null) {
				data.nextStep();
				continue;
			}

			doHandler(method, data);

			if (data.isFinishFlag()) {
				break;
			}

			// 等待回调任务
			if (stepBean.isNeedWaiting()) {
				CommTaskBean<T> task = new CommTaskBean<T>(this, data);
				WaitingTaskTimer.waitingTask(data.getMobileImsi(), task);
				return;
			}

			long delayTime = stepBean.getDelayTime();
			if (delayTime > 0) {
				LOGGER.info("任务{}延迟{}毫秒执行", data.getTokenId(), delayTime);
				CommTaskBean<T> task = new CommTaskBean<T>(this, data);
				TaskTimingTimer.delayTask(task, delayTime);
				return;
			}
		}

		finishTask(data);
	}

	public abstract void finishTask(T data);

	private void doHandler(Method method, T data) {
		try {
			method.invoke(this, data);
			data.nextStep();
			return;
		} catch (IllegalAccessException e) {
			LOGGER.error("任务{}执行方法{}失败", new Object[] { data.getTokenId(), method.getName(), e });
		} catch (IllegalArgumentException e) {
			LOGGER.error("任务{}执行方法{}失败", new Object[] { data.getTokenId(), method.getName(), e });
		} catch (InvocationTargetException e) {
			LOGGER.error("任务{}执行方法{}失败", new Object[] { data.getTokenId(), method.getName(), e });
		} catch (Exception e) {
			LOGGER.error("任务{}执行方法{}失败", new Object[] { data.getTokenId(), method.getName(), e });
		}

		LOGGER.warn("当前任务[{}]执行报错，结束任务", data.getTokenId());
		data.setFinishFlag(true);
	}

}
