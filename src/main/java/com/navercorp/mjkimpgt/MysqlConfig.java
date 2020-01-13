package com.navercorp.mjkimpgt;

import java.util.Properties;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class MysqlConfig {
	
//	@Bean
//	public JdbcTemplate getUserName(String userId) throws Exception {
//		Properties properties = new Properties();
//		properties.setProperty("user_id", userId);
//		
//		BasicDataSource createDataSource = BasicDataSourceFactory.createDataSource(properties);
//		return new JdbcTemplate(createDataSource);
//	}
}
