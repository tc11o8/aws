package com.smalleyes.aws.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 数据库切换
 *
 * @author <a href="mailto:xupengcheng@qq.com" >徐鹏程</a>
 * @version 1.0.0
 */
public class DynamicRoutingDataSource extends AbstractRoutingDataSource {
    final static Logger logger = LoggerFactory.getLogger(DynamicRoutingDataSource.class);

    @Override
    protected Object determineCurrentLookupKey() {
        String key = DynamicDataSourceContextHolder.getDataSourceKey();
        logger.debug("数据库key : {} ", key);
        return key;
    }
}
