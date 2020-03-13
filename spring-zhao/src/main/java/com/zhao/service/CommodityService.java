package com.zhao.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.beans.ConstructorProperties;

/**
 * @author zhaojinliang
 * @version 1.0
 * @description TODO
 * @date 2020-01-07 17:34
 */
@Component
public class CommodityService {
//	@PreDestroy
	public void close() {
		System.out.println("close");
	}

//	@Autowired
//	UserService userService;


//	public CommodityService() {
//		System.out.println("CommodityService init");
//	}

//	public void testAop() {
//		System.out.println("commodity test aop");
//		userService.testAop();
//	}
}
