package com.smalleyes.aws.task.data;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.smalleyes.aws.constants.CacheKey;
import com.smalleyes.aws.pojo.BizConfig;
import com.smalleyes.aws.pojo.BizTask;
import com.smalleyes.aws.pojo.BizUser;
import com.smalleyes.aws.task.bean.CommTaskBean;
import com.smalleyes.aws.task.bean.StepBean;
import com.smalleyes.aws.task.handler.ActionStepTaskHandler;

/**
 * 创建任务线程
 */
@Service
public class ActionNewDataCreateTimer extends ActionDataBase {
	public static final Logger LOGGER = LoggerFactory.getLogger(ActionNewDataCreateTimer.class);

	private String queueKey;

	@Override
	public void prepare() {
		this.setOpenFlag(CacheKey.OPEN);
		super.setKey(CacheKey.ACTION_NEW_TASK_LOCK);
		this.queueKey = CacheKey.COMM_TASK_QUEUE;
	}

	@Override
	public void hander() throws Exception {

		// 只要开启就做
		Long size = redisClient.getQueueSize(queueKey);
		if (size > 500) {
			LOGGER.info("当前队列长度为{},暂停5秒", size);
			this.setSleepTime(5000);
			return;
		}

		BizConfig config = new BizConfig();
		addTask(config);
	}

	private void addTask(BizConfig config) {

		BizUser bizUser = new BizUser();
		CommTaskBean<BizTask> task = createNewTask(bizUser, config);
		redisClient.putToQueue(queueKey, task);
	}

	/*
	 * 创建注册抖音用户任务
	 */
	public CommTaskBean<BizTask> createNewTask(BizUser bizUser, BizConfig bizConfig) {

		BizTask bizTask = new BizTask();

		List<StepBean> steps = createSteps(bizTask);
		bizTask.setStepList(steps);
		
		LOGGER.debug("bizTask is[{}]", bizTask.toString());
		CommTaskBean<BizTask> task = new CommTaskBean<BizTask>(new ActionStepTaskHandler(), bizTask);

		return task;
	}

	@Override
	public List<StepBean> createSteps(BizTask task) {

		List<StepBean> stepList = new ArrayList<StepBean>();

		// 1设备注册
		StepBean bean1 = new StepBean();
		bean1.setMethodNum(1);
		bean1.setDelayTime(1000);

		StepBean bean2 = new StepBean();
		bean2.setMethodNum(2);
		bean2.setDelayTime(1000);

		StepBean bean3 = new StepBean();
		bean3.setMethodNum(3);
		bean3.setDelayTime(1000);

		stepList.add(bean1);

		return stepList;
	}

}
