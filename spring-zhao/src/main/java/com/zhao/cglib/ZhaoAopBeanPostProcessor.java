package com.zhao.cglib;

import com.zhao.service.CommodityService;
import com.zhao.service.UserService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author zhaojinliang
 * @version 1.0
 * @description TODO
 * @date 2020-03-13 20:06
 */
public class ZhaoAopBeanPostProcessor implements BeanPostProcessor {
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof CommodityService) {
			bean = CGLibUtil.getProxy(bean.getClass());
		}
		return bean;
	}
}
