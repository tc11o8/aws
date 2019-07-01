/**
 * @(#)ApkBaseEnum.java 2016年11月23日
 */
package com.smalleyes.aws.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 运营商类型
 */
public enum OperatorEnum {
	
	MOBILE(1, "移动"), UNICOM(2, "联通"), TELECOM(3, "电信"), UNKNOW(-1, "未知");

	private int type;
	private String name;

	private OperatorEnum(int type, String name) {
		this.type = type;
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private static Map<Integer, OperatorEnum> map = new HashMap<Integer, OperatorEnum>();

	static {
		for (OperatorEnum apkBaseEnum : OperatorEnum.values()) {
			map.put(apkBaseEnum.type, apkBaseEnum);
		}
	}

	public static OperatorEnum valueOf(int type) {
		return map.get(type);
	}
}
