package com.smalleyes.aws.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于监控统计
 * 
 * @author <a href="mailto:284040429@qq.com" >张明俊</a>
 * @version 0.0.1
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface STATS {

}
