package com.zhao.zhaoBatis;

import com.zhao.config.AppConfig;
import com.zhao.zhaoBatis.annotation.ZhaoMapper;
import com.zhao.zhaoBatis.config.MybatisAppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.type.filter.AnnotationTypeFilter;

/**
 * @author zhaojinliang
 * @version 1.0
 * @description TODO
 * @date 2020-03-08 14:48
 */
public class ZhaoBatisTest {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext();
		ac.register(MybatisAppConfig.class);
		ac.refresh();


		ZhaoScanner scanner = new ZhaoScanner(ac);
		scanner.addIncludeFilter(new AnnotationTypeFilter(ZhaoMapper.class));
		int scan = scanner.scan("com.zhao.zhaoBatis");
		System.out.println(scan);




//		// 能够得到一个实现了UserMapper的代理对象
//		UserMapper userMapper = (UserMapper) ZhaoBatisFactory.getMapper(UserMapper.class);
//		OrderMapper orderMapper = (OrderMapper) ZhaoBatisFactory.getMapper(OrderMapper.class);
//
//		// 完成了查询
//		userMapper.queryUser();
//		// 完成了查询
//		orderMapper.queryOrder();



		UserMapper userMapper = ac.getBean(UserMapper.class);
		userMapper.queryUser();
	}
}
