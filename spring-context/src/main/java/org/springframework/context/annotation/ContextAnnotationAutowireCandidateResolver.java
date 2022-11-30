/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.context.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Complete implementation of the
 * {@link org.springframework.beans.factory.support.AutowireCandidateResolver} strategy
 * interface, providing support for qualifier annotations as well as for lazy resolution
 * driven by the {@link Lazy} annotation in the {@code context.annotation} package.
 *
 * @author Juergen Hoeller
 * @since 4.0
 */
public class ContextAnnotationAutowireCandidateResolver extends QualifierAnnotationAutowireCandidateResolver {

	@Override
	@Nullable
	public Object getLazyResolutionProxyIfNecessary(DependencyDescriptor descriptor, @Nullable String beanName) {
		// 如果依赖描述符加了Lazy注解, 则构建一个cglib的代理对象, 否则返回null
		return (isLazy(descriptor) ? buildLazyResolutionProxy(descriptor, beanName) : null);
	}

	protected boolean isLazy(DependencyDescriptor descriptor) {
		// 判断依赖描述符(属性)上是否添加Lazy注解
		for (Annotation ann : descriptor.getAnnotations()) {
			Lazy lazy = AnnotationUtils.getAnnotation(ann, Lazy.class);
			if (lazy != null && lazy.value()) {
				return true;
			}
		}
		// 判断依赖描述符的方法参数上是否添加Lazy注解
		MethodParameter methodParam = descriptor.getMethodParameter();
		if (methodParam != null) {
			Method method = methodParam.getMethod();
			if (method == null || void.class == method.getReturnType()) {
				Lazy lazy = AnnotationUtils.getAnnotation(methodParam.getAnnotatedElement(), Lazy.class);
				if (lazy != null && lazy.value()) {
					return true;
				}
			}
		}
		return false;
	}

	protected Object buildLazyResolutionProxy(final DependencyDescriptor descriptor, final @Nullable String beanName) {
		Assert.state(getBeanFactory() instanceof DefaultListableBeanFactory,
				"BeanFactory needs to be a DefaultListableBeanFactory");
		final DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) getBeanFactory();
		TargetSource ts = new TargetSource() {
			@Override
			public Class<?> getTargetClass() {
				return descriptor.getDependencyType();
			}
			@Override
			public boolean isStatic() {
				return false;
			}
			@Override
			public Object getTarget() {
				// 当注入的代理对象使用的时候, 才会通过spring容器根据依赖描述符找到对应的对象, 再去使用
				// todo: 构造器/方法等解决循环依赖的关键?
				Object target = beanFactory.doResolveDependency(descriptor, beanName, null, null);
				if (target == null) {
					Class<?> type = getTargetClass();
					if (Map.class == type) {
						return Collections.emptyMap();
					}
					else if (List.class == type) {
						return Collections.emptyList();
					}
					else if (Set.class == type || Collection.class == type) {
						return Collections.emptySet();
					}
					throw new NoSuchBeanDefinitionException(descriptor.getResolvableType(),
							"Optional dependency not present for lazy injection point");
				}
				return target;
			}
			@Override
			public void releaseTarget(Object target) {
			}
		};
		ProxyFactory pf = new ProxyFactory();
		pf.setTargetSource(ts);
		Class<?> dependencyType = descriptor.getDependencyType();
		if (dependencyType.isInterface()) {
			pf.addInterface(dependencyType);
		}
		// 生成一个cglib的代理对象提供注入
		return pf.getProxy(beanFactory.getBeanClassLoader());
	}

}
