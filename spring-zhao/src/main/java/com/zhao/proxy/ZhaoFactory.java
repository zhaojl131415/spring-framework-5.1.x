package com.zhao.proxy;

import java.lang.reflect.Proxy;

public class ZhaoFactory {

	/**
	 * 能够得到一个实现了UserDao的对象
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static Object getMapper(Class clazz){
		//实现JDK动态代理

		/**
		 * (ClassLoader loader, 动态加载一个类？
		 * Class<?>[] interfaces,
		 * InvocationHandler h
		 */


		Class[] classes = new Class[]{clazz};
		/**
		 * xxx.class==zijiema
		 *
		 *
		 */
		Object o = Proxy.newProxyInstance(ZhaoFactory.class.getClassLoader(),classes,new ZhaoInvocationHandler());
		return o;
	}
}
