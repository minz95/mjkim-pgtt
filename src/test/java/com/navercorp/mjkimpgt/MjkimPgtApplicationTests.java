package com.navercorp.mjkimpgt;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

//@SpringBootTest
class MjkimPgtApplicationTests {

	@Test
	void contextLoads() {
	}
	
	@Test
	public void testHelloDB() throws Exception {
		
		Properties properties = new Properties();
		properties.setProperty("username", "mjkim");
		properties.setProperty("password", "alswl9412");
		properties.setProperty("url", "jdbc:mysql://10.106.163.73:13306/pgtDB?autoReconnect=true&useTimezone=true&serverTimezone=UTC");

		BasicDataSource createDataSource = BasicDataSourceFactory.createDataSource(properties);

		JdbcTemplate jdbcTemplate = new JdbcTemplate(createDataSource);
		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList("SELECT * FROM User");
		System.out.println(queryForList);
	}

}
