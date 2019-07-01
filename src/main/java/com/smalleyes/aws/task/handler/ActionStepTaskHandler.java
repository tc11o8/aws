package com.smalleyes.aws.task.handler;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smalleyes.aws.annotation.TaskStep;
import com.smalleyes.aws.pojo.BizTask;
import com.smalleyes.aws.task.iframe.AbstractStepTaskHandler;

//任务步骤处理器
public class ActionStepTaskHandler extends AbstractStepTaskHandler<BizTask> implements Serializable {

	private static final long serialVersionUID = -4838280250620164254L;

	public static final Logger LOGGER = LoggerFactory.getLogger(ActionStepTaskHandler.class);

	@Override
	public void finishTask(BizTask task) {
	}

	
	@TaskStep(1)
	public void register(BizTask task) {
		
	}
	
	@TaskStep(2)
	public void doAction(BizTask task) {
		
	}
	
	@TaskStep(3)
	public void submit(BizTask task) {
		
	}

	
}
