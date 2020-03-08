package com.zhao.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author zhaojinliang
 * @version 1.0
 * @description TODO
 * @date 2020-01-07 17:34
 */
@Component
public class CommodityService {

	@Autowired
//	Service service;
	Service service1;
//	Service service2;

	public void test() {
//		System.out.println(service);
		System.out.println(service1);
//		System.out.println(service2);
	}

//	public CommodityService() {
//		System.out.println("CommodityService create");
//	}
//
//	@Lookup
//	public abstract OrderService orderAbc();
//
//	public void testOrderScope() {
//		System.out.println(orderAbc().hashCode());
//	}





//	@Autowired
//	UserService userService;
//
//	public void testAop() {
//		System.out.println("commodity test aop");
//		userService.testAop();
//	}
}
