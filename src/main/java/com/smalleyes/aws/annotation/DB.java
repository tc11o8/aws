package com.smalleyes.aws.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DB {

	/**
	 * name:数据库名
	 * 
	 * @author <a href="mailto:284040429@qq.com" >张明俊</a>
	 * @version 0.0.1
	 */
	String name();
}
