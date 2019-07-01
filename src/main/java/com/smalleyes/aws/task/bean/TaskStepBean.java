/**
 * @(#)TaskStepBean.java 2018年4月26日
 */
package com.smalleyes.aws.task.bean;

import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO 请加入本类的说明
 * 
 * @author <a href="mailto:284040429@qq.com" >张明俊</a>
 * @version 1.0.0
 */
public class TaskStepBean extends ProxyBean implements Serializable {

	private static final long serialVersionUID = -665064945021126041L;
	public static final Logger LOGGER = LoggerFactory.getLogger(TaskStepBean.class);

	private List<StepBean> stepList;
	private volatile boolean finishFlag;
	private String tokenId;
	private int totalStep;
	private int currStep = 0;
	private boolean isNew;
	private boolean successFlag;
	// 任务类型 1新增 2留存 3补量
	private int taskType;
	protected String mobileImsi;

	public void nextStep() {
		++currStep;
	}

	public StepBean getCurrStep() {
		return stepList.get(currStep);
	}

	public void setCurrStep(int currStep) {
		this.currStep = currStep;
	}

	public boolean isFinishFlag() {
		return finishFlag;
	}

	public boolean canFinish() {
		return finishFlag || currStep >= totalStep;
	}

	public void setFinishFlag(boolean finishFlag) {
		this.finishFlag = finishFlag;
	}

	public List<StepBean> getStepList() {
		return stepList;
	}

	public void setStepList(List<StepBean> stepList) {
		this.stepList = stepList;
		this.totalStep = stepList.size();
	}

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public int getTotalStep() {
		return totalStep;
	}

	public void setTotalStep(int totalStep) {
		this.totalStep = totalStep;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public boolean isSuccessFlag() {
		return successFlag;
	}

	public void setSuccessFlag(boolean successFlag) {
		this.successFlag = successFlag;
	}

	public int getTaskType() {
		return taskType;
	}

	public void setTaskType(int taskType) {
		this.taskType = taskType;
	}

	public String getMobileImsi() {
		return mobileImsi;
	}

	public void setMobileImsi(String mobileImsi) {
		this.mobileImsi = mobileImsi;
	}

	public boolean hasFollow() {
		for (StepBean stepBean : stepList) {
			if (stepBean.getMethodNum() == 7) {
				return true;
			}
		}
		return false;
	}

	public boolean hasDigg() {
		for (StepBean stepBean : stepList) {
			if (stepBean.getMethodNum() == 9) {
				return true;
			}
		}
		return false;
	}

	public boolean hasComment() {
		for (StepBean stepBean : stepList) {
			if (stepBean.getMethodNum() == 10) {
				return true;
			}
		}
		return false;
	}

	public boolean hasShare() {
		for (StepBean stepBean : stepList) {
			if (stepBean.getMethodNum() == 27) {
				return true;
			}
		}
		return false;
	}

}
