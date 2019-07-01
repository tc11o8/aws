/**
 * @(#)MatchUtils.java 2017年9月2日
 */
package com.smalleyes.aws.util;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO 请加入本类的说明
 * @author <a href="mailto:284040429@qq.com" >张明俊</a>
 * @version 1.0.0
 */
public class MatchUtils {
	public static final Logger LOGGER = LoggerFactory.getLogger(MatchUtils.class);
	
	public static String rule(String rule,String content){
		try{
			Pattern pattern=Pattern.compile(rule);
			Matcher matcher=pattern.matcher(content);
			if(matcher.find()){
				return matcher.group(1);
			}
		}catch(Exception e){
			LOGGER.error("",e);
		}
		return null;
	}
	
	
	public static void main(String[] args){
//		String content="验证码为：123213，正在啊实打实";
//		String content="【抖音】验证码1669，用于更改密码，5分钟内有效。验证码提供给他人可能导致账号被盗，请勿泄露，谨防被骗。";
//		String content="【京东】您申请了手机号码注册，验证码为：501641，两分钟内有效。请在注册页面中输入以完成注册。";
		String content="8642，短信验证码5分钟内有效【中国联通】";
//		String result=rule("验证码为：(\\d{6})",content);
		String result=rule("(\\d{4})",content);
		System.out.println(result);
	}
	
}
