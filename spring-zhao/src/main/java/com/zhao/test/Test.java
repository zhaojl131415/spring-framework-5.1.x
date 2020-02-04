package com.zhao.test;

import com.zhao.config.AppConfig;
import com.zhao.service.CommodityService;
import com.zhao.service.OrderService;
import com.zhao.service.UserService;
import org.springframework.beans.BeansException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author zhaojinliang
 * @version 1.0
 * @description TODO
 * @date 2020-01-07 17:37
 */
public class Test {

	public static void main(String[] args) throws BeansException {
		// spring 上下文初始化，扫描并实例化spring bean
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
//		System.out.println(ac.getBean(OrderService.class));
//		System.out.println(ac.getBean(UserService.class));
//		System.out.println(ac.getBean(CommodityService.class));


		((UserService) ac.getBean("userService")).testAop();

//		ac.getBean(UserService.class).testAop();
	}
}