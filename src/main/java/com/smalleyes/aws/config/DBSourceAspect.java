package com.smalleyes.aws.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.smalleyes.aws.annotation.DB;

/**
 * 多个数据源
 *
 * @author <a href="mailto:xupengcheng@qq.com" >徐鹏程</a>
 * @version 1.0.0
 */
@Aspect
@Component
@Order(1)
public class DBSourceAspect {

    final static Logger logger = LoggerFactory.getLogger(DBSourceAspect.class);

    @Pointcut("execution( * com.smalleyes.aws.mapper.*.*(..))")
    public void daoAspect() {
    }

    @Before("daoAspect()")
    public void switchDataSource(JoinPoint point) {
        long start = System.currentTimeMillis();
        for (Class<?> mapperInterface : point.getTarget().getClass().getInterfaces()) {
            String interfaceName = mapperInterface.getName();
            if (interfaceName.startsWith("com.smalleyes.aws.mapper")) {
                DB db = mapperInterface.getAnnotation(DB.class);
                if (db != null) {
                    DynamicDataSourceContextHolder.setDataSourceKey(db.name());
                    logger.debug("db {}", db.name());
                    logger.debug(" {} 毫秒", System.currentTimeMillis() - start);
                    return;
                }
            }
        }
    }

    @After("daoAspect()")
    public void restoreDataSource(JoinPoint point) {
        DynamicDataSourceContextHolder.clearDataSourceKey();
        logger.debug("db clear");
    }
}
