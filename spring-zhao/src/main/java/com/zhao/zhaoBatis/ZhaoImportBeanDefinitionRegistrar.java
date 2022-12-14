package com.zhao.zhaoBatis;

import com.zhao.zhaoBatis.annotation.ZhaoMapperScan;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;


public class ZhaoImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {


		Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(ZhaoMapperScan.class.getName());
		String value = (String) annotationAttributes.get("value");

		ZhaoScanner zhaoScanner = new ZhaoScanner(registry);
		zhaoScanner.addIncludeFilter((reader, readerFactory) -> true);
		zhaoScanner.scan(value);


		// 能够得到一个实现了UserMapper的代理对象
//		UserMapper userMapper = (UserMapper) ZhaoBatisFactory.getMapper(UserMapper.class);

//		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
//		BeanDefinition beanDefinition = builder.getBeanDefinition();
//		beanDefinition.setBeanClassName(ZhaoBatisFactory.class.getName());
//		beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(UserMapper.class);
//		registry.registerBeanDefinition("userMapper", beanDefinition);
//
//		BeanDefinitionBuilder builder1 = BeanDefinitionBuilder.genericBeanDefinition();
//		BeanDefinition beanDefinition1 = builder1.getBeanDefinition();
//		beanDefinition1.setBeanClassName(ZhaoBatisFactory.class.getName());
//		beanDefinition1.getConstructorArgumentValues().addGenericArgumentValue(OrderMapper.class);
//		registry.registerBeanDefinition("orderMapper", beanDefinition1);
	}
}
