package com.zhao.zhaoBatis;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

public class ZhaoImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {


		// 能够得到一个实现了UserMapper的代理对象
		UserMapper userMapper = (UserMapper) ZhaoBatisFactory.getMapper(UserMapper.class);

		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
		BeanDefinition beanDefinition = builder.getBeanDefinition();
		beanDefinition.setBeanClassName(ZhaoBatisFactory.class.getName());
		beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(UserMapper.class);

		registry.registerBeanDefinition("userMapper", beanDefinition);
	}
}
