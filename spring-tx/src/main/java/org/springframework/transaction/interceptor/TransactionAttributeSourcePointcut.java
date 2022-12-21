/*
 * Copyright 2002-2018 the original author or authors.
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

package org.springframework.transaction.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.lang.Nullable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

/**
 * Inner class that implements a Pointcut that matches if the underlying
 * {@link TransactionAttributeSource} has an attribute for a given method.
 *
 * @author Juergen Hoeller
 * @since 2.5.5
 */
@SuppressWarnings("serial")
abstract class TransactionAttributeSourcePointcut extends StaticMethodMatcherPointcut implements Serializable {

	/**
	 * 在spring5.3版本中有做调整, 生成了无参构造函数,
	 * 在无参构造函数内通过setClassFilter()方法实例化了一个TransactionAttributeSourceClassFilter
	 * 这个类实现了接口: {@link ClassFilter}, 并重写了其匹配方法: {@link ClassFilter#matches(Class)}
	 * 在这个方法中判断了指定的类是否存在{@link Transactional}注解
	 */

	/**
	 * 查找并解析类/方法上的{@link Transactional}注解, 匹配注解的属性是否为空, 不为空返回false
	 * @param method the candidate method
	 * @param targetClass the target class
	 * @return
	 */
	@Override
	public boolean matches(Method method, Class<?> targetClass) {
		// 匹配判断, 这里基本都不匹配, 不会进入if
		if (TransactionalProxy.class.isAssignableFrom(targetClass) ||
				PlatformTransactionManager.class.isAssignableFrom(targetClass) ||
				PersistenceExceptionTranslator.class.isAssignableFrom(targetClass)) {
			return false;
		}
		/**
		 * {@link BeanFactoryTransactionAttributeSourceAdvisor#pointcut}内getTransactionAttributeSource
		 * 获取到的值:{@link AnnotationTransactionAttributeSource}
		 */
		TransactionAttributeSource tas = getTransactionAttributeSource();
		/**
		 * 所以这里获取事务属性实际上是执行{@link AnnotationTransactionAttributeSource}的父抽象类的方法:
		 * @see AbstractFallbackTransactionAttributeSource#getTransactionAttribute(Method, Class)
		 */
		return (tas == null || tas.getTransactionAttribute(method, targetClass) != null);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof TransactionAttributeSourcePointcut)) {
			return false;
		}
		TransactionAttributeSourcePointcut otherPc = (TransactionAttributeSourcePointcut) other;
		return ObjectUtils.nullSafeEquals(getTransactionAttributeSource(), otherPc.getTransactionAttributeSource());
	}

	@Override
	public int hashCode() {
		return TransactionAttributeSourcePointcut.class.hashCode();
	}

	@Override
	public String toString() {
		return getClass().getName() + ": " + getTransactionAttributeSource();
	}


	/**
	 * Obtain the underlying TransactionAttributeSource (may be {@code null}).
	 * To be implemented by subclasses.
	 */
	@Nullable
	protected abstract TransactionAttributeSource getTransactionAttributeSource();

}
