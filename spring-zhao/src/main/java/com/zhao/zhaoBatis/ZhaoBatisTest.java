package com.zhao.zhaoBatis;

/**
 * @author zhaojinliang
 * @version 1.0
 * @description TODO
 * @date 2020-03-08 14:48
 */
public class ZhaoBatisTest {

	public static void main(String[] args) {
		// 能够得到一个实现了UserMapper的代理对象
		UserMapper userMapper = (UserMapper) ZhaoBatisFactory.getMapper(UserMapper.class);

		// 完成了查询
		userMapper.queryUser();
		// 完成了查询
		userMapper.queryOrder();
	}
}
