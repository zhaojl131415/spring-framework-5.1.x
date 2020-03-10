package com.zhao.service;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;

/**
 * @author zhaojinliang
 * @version 1.0
 * @description TODO
 * @date 2020-01-07 17:33
 */
public class ZhaoService {

	public ZhaoService() {
		System.out.println("ZhaoService create");
	}
}
