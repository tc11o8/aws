package com.smalleyes.aws.pojo;

import java.io.Serializable;
import java.util.Date;

public class SpBizCode  implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	// 第三方提供的  业务ID
	private String bizId;
	private String bizName;
	// 每天下发量
	private Integer dayCount;
	// 总量
	private Integer totalCount;
	// 下发最小时间时间 毫秒
	private Integer minTimeSpan;
	
	private Integer state;
	
	private Date createTime;
	private Date modifyTime;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getBizId() {
		return bizId;
	}

	public void setBizId(String bizId) {
		this.bizId = bizId;
	}

	public String getBizName() {
		return bizName;
	}

	public void setBizName(String bizName) {
		this.bizName = bizName;
	}

	public Integer getDayCount() {
		return dayCount;
	}

	public void setDayCount(Integer dayCount) {
		this.dayCount = dayCount;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}
	
	public Integer getMinTimeSpan() {
		return minTimeSpan;
	}

	public void setMinTimeSpan(Integer minTimeSpan) {
		this.minTimeSpan = minTimeSpan;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

}
