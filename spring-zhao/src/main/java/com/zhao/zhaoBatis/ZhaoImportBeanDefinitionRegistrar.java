package com.zhao.zhaoBatis;

import com.zhao.zhaoBatis.annotation.ZhaoMapperScan;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;

import java.util.Map;

public class ZhaoImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {


		Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(ZhaoMapperScan.class.getName());
		Object value = annotationAttributes.get("value");
		System.out.println(value);



		// 能够得到一个实现了UserMapper的代理对象
//		UserMapper userMapper = (UserMapper) ZhaoBatisFactory.getMapper(UserMapper.class);

		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
		BeanDefinition beanDefinition = builder.getBeanDefinition();
		beanDefinition.setBeanClassName(ZhaoBatisFactory.class.getName());
//		beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(UserMapper.class);

		registry.registerBeanDefinition("userMapper", beanDefinition);
	}
}
