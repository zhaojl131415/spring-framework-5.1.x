package com.zhao.zhaoBatis;

import com.zhao.zhaoBatis.annotation.ZhaoMapper;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

import java.io.IOException;

public class ZhaoScanner extends ClassPathBeanDefinitionScanner {
	public ZhaoScanner(BeanDefinitionRegistry registry) {
		super(registry);
	}

	@Override
	public void addIncludeFilter(TypeFilter includeFilter) {
		super.addIncludeFilter(includeFilter);
	}

	@Override
	protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {

		AnnotationMetadata metadata = beanDefinition.getMetadata();
		// 判断是否为独立类
		return (metadata.isIndependent() && metadata.isInterface());
//		return super.isCandidateComponent(beanDefinition);
	}
}
