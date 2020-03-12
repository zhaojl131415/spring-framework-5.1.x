package com.zhao.cglib;

import org.springframework.cglib.proxy.Enhancer;

public class Test {
	public static void main(String[] args) throws Exception {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(Z.class);
		enhancer.setCallback(new ZhaoMethodInterceptor());
		enhancer.setUseFactory(false);
		enhancer.setCallbackType(ZhaoMethodInterceptor.class);
		Z cglib = (Z) enhancer.create();
		cglib.cglibMethod();
	}
}
