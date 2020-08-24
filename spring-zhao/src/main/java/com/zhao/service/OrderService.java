package com.zhao.service;

import com.zhao.cglib.CGLibUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author zhaojinliang
 * @version 1.0
 * @description TODO
 * @date 2020-01-16 10:52
 */
@Component
public class OrderService {

	@Autowired
	UserService userService;

	public OrderService() {
		System.out.println("OrderService default");
	}
//	public OrderService(Service s) {
//		System.out.println("Service");
//	}
//	@Autowired
//	public OrderService(UserService u) {
//		userService = u;
//		System.out.println("UserService");
//	}
//	@Autowired
//	public OrderService(X x, UserService u) {
//		System.out.println("x UserService");
//	}
}
