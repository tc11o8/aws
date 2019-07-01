package com.smalleyes.aws.pojo;

import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smalleyes.aws.task.bean.TaskStepBean;

public class BizTask extends TaskStepBean implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final Logger LOGGER = LoggerFactory.getLogger(BizTask.class);

	private Integer bizId;
	private String imsi;
	private String province;
	
	public Integer getBizId() {
		return bizId;
	}
	public void setBizId(Integer bizId) {
		this.bizId = bizId;
	}
	public String getImsi() {
		return imsi;
	}
	public void setImsi(String imsi) {
		this.imsi = imsi;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	
}
