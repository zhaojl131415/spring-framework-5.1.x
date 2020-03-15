package com.zhao.factoryBean;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

/**
 * @author zhaojinliang
 * @version 1.0
 * @description TODO
 * @date 2020-03-08 11:56
 */
//@Component
public class ZhaoFactoryBean implements FactoryBean {
	@Override
	public Object getObject() throws Exception {
		return new Z();
	}

	@Override
	public Class<?> getObjectType() {
		return Z.class;
	}
}
