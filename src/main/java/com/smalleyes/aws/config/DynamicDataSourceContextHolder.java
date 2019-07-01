package com.smalleyes.aws.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据源配置
 *
 * @author <a href="mailto:xupengcheng@qq.com" >徐鹏程</a>
 * @version 1.0.0
 */
public class DynamicDataSourceContextHolder {

    final static Logger logger = LoggerFactory.getLogger(DynamicDataSourceContextHolder.class);

    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>(); // ThreadLocal.withInitial(DataSourceEnum.crack::name);

    public static String getDataSourceKey() {
        return CONTEXT_HOLDER.get();
    }

    public static void setDataSourceKey(String key) {
        CONTEXT_HOLDER.set(key);
        logger.debug("db {}", key);
    }

    public static void clearDataSourceKey() {
        CONTEXT_HOLDER.remove();
        logger.debug("db clear");
    }
}
