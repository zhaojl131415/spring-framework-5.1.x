package com.zhao.factoryBean;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author zhaojinliang
 * @version 1.0
 * @description TODO
 * @date 2020-03-08 11:56
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ZhaoFactoryBean implements FactoryBean {
	@Override
	public Object getObject() throws Exception {
		return new Z();
	}

	@Override
	public Class<?> getObjectType() {
		return Z.class;
	}

//	@Override
//	public boolean isSingleton() {
//		return false;
//	}
}
