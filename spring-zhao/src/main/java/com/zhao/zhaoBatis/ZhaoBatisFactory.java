package com.zhao.zhaoBatis;

import com.zhao.zhaoBatis.annotation.ZhaoSelect;

import java.lang.reflect.Proxy;

/**
 * @author zhaojinliang
 * @version 1.0
 * @description TODO
 * @date 2020-03-08 14:46
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ZhaoBatisFactory {

	// 实现jdk动态代理, 能够得到一个实现了Mapper接口的代理对象
	public static Object getMapper(Class clazz) {
		/**
		 * ClassLoader loader, 动态加载一个在本项目中不存在类
		 * Class<?>[] interfaces, 指定通过动态代理返回的那个对象需要实现的接口
		 * InvocationHandler h 代理对象实现Mapper接口后需要重写接口方法的具体逻辑
		 */
		Class<?>[] interfaces = new Class[]{clazz};
		return Proxy.newProxyInstance(ZhaoBatisFactory.class.getClassLoader(),
				interfaces,
				(proxy, method, args) -> {
					// 数据库连接和查询伪代码实现
					System.out.println("conn db");

					String sql = method.getAnnotation(ZhaoSelect.class).value();

					System.out.println(sql);

					System.out.println("sql execute");

//					method.getReturnType()
					return null;
				});
	}
}


