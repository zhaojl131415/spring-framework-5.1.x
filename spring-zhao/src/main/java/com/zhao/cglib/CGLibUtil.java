package com.zhao.cglib;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author zhaojinliang
 * @version 1.0
 * @description TODO
 * @date 2020-03-13 20:00
 */
public class CGLibUtil {
	public static Object getProxy(Class clazz) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(clazz);
		enhancer.setUseFactory(false);
		enhancer.setCallback((MethodInterceptor)(o, method, objects, methodProxy) -> {
			System.out.println("aop--start--");
			Object result = methodProxy.invokeSuper(o, objects);
			System.out.println("aop--end--");
			return result;
		});
		return enhancer.create();
	}
}
