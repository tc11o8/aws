package com.smalleyes.aws.config;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.smalleyes.aws.enums.DataSourceEnum;

/**
 * 数据源配置
 */
@Configuration
public class DataSourceConfigurer {

	@Resource(name = "aws")
	private DataSource aws;

	@Resource(name = "awsLog")
	private DataSource awsLog;

	@Bean("dynamicDataSource")
	public DataSource dynamicDataSource() {
		DynamicRoutingDataSource routingDataSource = new DynamicRoutingDataSource();
		Map<Object, Object> targetDataSources = new HashMap<>(4);
		targetDataSources.put(DataSourceEnum.aws.name(), aws);
		targetDataSources.put(DataSourceEnum.awsLog.name(), awsLog);
		routingDataSource.setTargetDataSources(targetDataSources);
		routingDataSource.setDefaultTargetDataSource(aws);
		return routingDataSource;
	}

	@Bean
	@ConfigurationProperties(prefix = "mybatis.configuration")
	public org.apache.ibatis.session.Configuration globalConfiguration() {
		return new org.apache.ibatis.session.Configuration();
	}

	@Bean
	@ConfigurationProperties(prefix = "mybatis")
	public SqlSessionFactoryBean sqlSessionFactoryBean(org.apache.ibatis.session.Configuration config)
			throws Exception {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dynamicDataSource());
		sqlSessionFactoryBean.setConfiguration(config);
		return sqlSessionFactoryBean;
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		return new DataSourceTransactionManager(dynamicDataSource());
	}

}
