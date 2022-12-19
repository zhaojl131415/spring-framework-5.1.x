package com.zhao.proxy;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.*;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * TODO
 *
 * @author zhaojinliang
 * @version 1.0.0
 * @since 2022/12/15
 */
public class ProxyFactoryDemo {
	public static void main(String[] args) {
		ZhaoInterface target = new ZhaoService();
		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.setTarget(target);
		// 如果代理工程指定了接口类, 则使用JDK动态代理, 反之则使用CGLib动态代理.
		proxyFactory.setInterfaces(ZhaoInterface.class);
		// 按照添加顺序增强: 责任链顺序执行
		// 添加增强
		proxyFactory.addAdvice(new MethodInterceptor() {
			@Override
			public Object invoke(MethodInvocation invocation) throws Throwable {
				System.out.println("MethodInterceptor invoke before...");
				Object proceed = invocation.proceed();
				System.out.println("MethodInterceptor invoke after...");
				return proceed;
			}
		});
		// 添加增强
		proxyFactory.addAdvice(new MethodBeforeAdvice(){
			@Override
			public void before(Method method, Object[] args, Object target) throws Throwable {
				System.out.println("before...");
			}
		});
		// 添加增强
		proxyFactory.addAdvice(new AfterReturningAdvice(){
			@Override
			public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
				System.out.println("after...return");
			}
		});
		// 添加增强
		proxyFactory.addAdvice(new ZhaoThrowsAdvice());

		proxyFactory.addAdvisor(new PointcutAdvisor() {
			@Override
			public Pointcut getPointcut() {
				return new StaticMethodMatcherPointcut() {
					/**
					 * 匹配判断, 只有返回true, 表示匹配, 这个切面才会生效
					 */
					@Override
					public boolean matches(Method method, Class<?> targetClass) {
						return method.getName().equals("test");
					}
				};
			}

			@Override
			public Advice getAdvice() {
				return new MethodBeforeAdvice() {
					@Override
					public void before(Method method, Object[] args, Object target) throws Throwable {
						System.out.println("PointcutAdvisor Before...");
					}
				};
			}

			@Override
			public boolean isPerInstance() {
				return false;
			}
		});
		ZhaoInterface zhaoInterface = (ZhaoInterface) proxyFactory.getProxy();
		System.out.println(zhaoInterface.getClass().getName());
		zhaoInterface.test();
	}
}


