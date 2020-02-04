package com.zhao.service;

import org.springframework.beans.factory.annotation.Autowired;
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
	UserService userService;


	public CommodityService() {
		System.out.println("CommodityService create");
	}


	public void testAop() {
		System.out.println("commodity test aop");
		userService.testAop();
	}
}
