package com.zhao.proxy;

import org.springframework.aop.ThrowsAdvice;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * TODO
 *
 * @author zhaojinliang
 * @version 1.0.0
 * @since 2022/12/15
 */
@Component
public class ZhaoThrowsAdvice implements ThrowsAdvice {
	public void afterThrowing(NullPointerException e) throws Throwable {
		System.out.println("Throw NullPointerException...");
	}
	public void afterThrowing(IllegalArgumentException e) throws Throwable {
		System.out.println("Throw IllegalArgumentException...");
	}
//	public void afterThrowing(Exception e) throws Throwable {
//		System.out.println("Throw Exception...");
//	}
	public void afterThrowing(Method method, Object[] args, Object target, Exception e) throws Throwable {
		System.out.println(method.getName() + " Throw Exception...");
	}
}
