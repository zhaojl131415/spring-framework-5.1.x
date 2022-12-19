package com.zhao.proxy;

import org.springframework.cglib.proxy.*;

import java.lang.reflect.Method;

/**
 * CGLib Enhancer
 *
 * @author zhaojinliang
 * @version 1.0.0
 * @since 2022/12/15
 */
public class EnhancerDemo {

	public static void main(String[] args) {
		ZhaoService target = new ZhaoService();
//		System.out.println(target);
		target.test();
		// 增强代理
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(ZhaoService.class);
		// 指定回调
		enhancer.setCallbacks(new Callback[]{
				new MethodInterceptor() {
					@Override
					public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
						System.out.println("test before...");
						Object result = method.invoke(target, objects);
//						Object result = methodProxy.invoke(target, objects);
//						Object result = methodProxy.invokeSuper(o, objects);
						System.out.println("test after...");
						return result;
					}
				},
				new MethodInterceptor() {
					@Override
					public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
						System.out.println("zhao before...");
						Object result = method.invoke(target, objects);
//						Object result = methodProxy.invoke(target, objects);
//						Object result = methodProxy.invokeSuper(o, objects);
						System.out.println("zhao after...");
						return result;
					}
				},
				NoOp.INSTANCE
		});
		// 设置回调过滤
		enhancer.setCallbackFilter(new CallbackFilter() {
			/**
			 * 返回下标, 通过返回的下标值来确定执行哪个回调
			 * @param m
			 * @return
			 */
			@Override
			public int accept(Method m) {
				if (m.getName().equals("test")) {
					return 0;
				} else if (m.getName().equals("zhao")) {
					return 1;
				} else {
					return 2;
				}
			}
		});
		System.out.println("生成代理类");
		ZhaoService zhaoService = (ZhaoService) enhancer.create();
//		System.out.println(zhaoService);
		zhaoService.test();
		zhaoService.zhao();
		zhaoService.world();
	}
}
