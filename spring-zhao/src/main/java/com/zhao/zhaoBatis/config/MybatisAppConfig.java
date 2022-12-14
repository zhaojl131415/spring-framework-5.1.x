package com.zhao.zhaoBatis.config;

import com.zhao.zhaoBatis.annotation.ZhaoMapperScan;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author zhaojinliang
 * @version 1.0
 * @description TODO
 * @date 2020-04-15 20:30
 */
@Configuration
@ComponentScan("com.zhao.zhaoBatis")
@ZhaoMapperScan("com.zhao.zhaoBatis.mapper")
public class MybatisAppConfig {

	@Bean
	public SqlSessionFactory sqlSessionFactory() throws IOException {
		InputStream inputStream = Resources.getResourceAsStream("mybatis.xml");
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		return sqlSessionFactory;
	}
}
