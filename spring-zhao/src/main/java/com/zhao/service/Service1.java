package com.zhao.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author zhaojinliang
 * @version 1.0
 * @description TODO
 * @date 2020-03-08 11:10
 */
@Component
public class Service1 implements Service {
	@Override
	public void hello() {
		System.out.println("hello, i am service1");
	}
}
