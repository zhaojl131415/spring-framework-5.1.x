package com.zhao.synthetic;

import java.lang.reflect.Method;

/**
 * @author zhaojinliang
 * @version 1.0
 * @description TODO
 * @date 2020-03-14 11:10
 */
public class SyntheticDemo {

	public void test(){
		InnerDemo inner = new InnerDemo();
		System.out.println(inner.i);
	}

	class InnerDemo {
		private int i;
	}

	public static void main(String[] args) {
		for (Method declaredMethod : InnerDemo.class.getDeclaredMethods()) {
			System.out.println(declaredMethod.getName());
		}
	}
}
