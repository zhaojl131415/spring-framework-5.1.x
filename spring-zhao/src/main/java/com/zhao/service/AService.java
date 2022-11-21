package com.zhao.service;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

/**
 * @author zhaojinliang
 * @version 1.0
 * @description TODO
 * @date 2020-01-07 17:33
 */
@Component
//@DependsOn(value = {"bService", "cService"})
public class AService {

	public AService() {
		System.out.println("AService create");
	}
}
