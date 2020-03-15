package com.zhao.config;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author zhaojinliang
 * @version 1.0
 * @description TODO
 * @date 2020-01-17 15:29
 */
//@Component
//@Aspect
public class ZhaoAspect {

	@Pointcut("execution(* com.zhao..*.* ())")
//	@Pointcut("within(com.zhao.service.UserService)")
	public void pointcut() {

	}

	@Before("pointcut()")
	public void before() {
		System.out.println("-----aop-----");
	}
}
