package com.zhao.proxy;

/**
 * @author zhaojinliang
 * @version 1.0
 * @description TODO
 * @date 2020-01-07 17:33
 */
public class ZhaoService implements ZhaoInterface{

	public ZhaoService() {
		System.out.println("ZhaoService create");
	}

	@Override
	public void test() {
		System.out.println("hello test!");
//		throw new RuntimeException();
//		throw new NullPointerException();
//		throw new IllegalArgumentException();
	}

	public void zhao() {
		System.out.println("hello zhao!");
	}

	public void world() {
		System.out.println("hello world!");
	}
}
