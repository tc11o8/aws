package com.smalleyes.aws.task.data;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smalleyes.aws.pojo.BizTask;
import com.smalleyes.aws.task.bean.StepBean;
import com.smalleyes.aws.util.thread.OneZsetThread;

public abstract class ActionDataBase extends OneZsetThread {
	public static final Logger LOGGER = LoggerFactory.getLogger(ActionDataBase.class);

	public abstract List<StepBean> createSteps(BizTask task);

}
