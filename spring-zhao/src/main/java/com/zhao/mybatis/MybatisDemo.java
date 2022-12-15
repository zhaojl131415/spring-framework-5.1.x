package com.zhao.mybatis;

import com.zhao.mybatis.config.MybatisConfig;
import com.zhao.mybatis.domain.User;
import com.zhao.mybatis.mapper.UserMapper;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

/**
 * TODO
 *
 * @author zhaojinliang
 * @version 1.0.0
 * @since 2022/12/15
 */
public class MybatisDemo {
	public static void main(String[] args) {
		try {

			AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext();
			ac.register(MybatisConfig.class);
			ac.refresh();

			UserMapper userMapper = (UserMapper) ac.getBean("userMapper");
			List<User> users = userMapper.queryUser();
			users.forEach(System.out::println);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
