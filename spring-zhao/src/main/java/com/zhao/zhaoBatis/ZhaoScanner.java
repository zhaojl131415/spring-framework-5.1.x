package com.zhao.zhaoBatis;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

public class ZhaoScanner extends ClassPathBeanDefinitionScanner {
	public ZhaoScanner(BeanDefinitionRegistry registry) {
		super(registry);
	}

	@Override
	public void addIncludeFilter(TypeFilter includeFilter) {
		super.addIncludeFilter(new AnnotationTypeFilter(ZhaoMapper.class));
	}
}
