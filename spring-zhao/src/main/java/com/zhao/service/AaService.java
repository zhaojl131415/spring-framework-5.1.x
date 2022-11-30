package com.zhao.service;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhaojinliang
 * @version 1.0
 * @description TODO
 * @date 2020-01-07 17:33
 */
@Component
//@DependsOn(value = {"bService", "cService"})
public class AaService implements BeanNameAware {

	@Autowired
	private AaService aaService;

	public AaService() {
		System.out.println("AService create");
	}

	private String name;

	@Override
	public void setBeanName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "AaService{" +
				"name='" + name + "'" +
				"}";
	}

	public void test() {
		System.out.println(aaService);
	}
}
