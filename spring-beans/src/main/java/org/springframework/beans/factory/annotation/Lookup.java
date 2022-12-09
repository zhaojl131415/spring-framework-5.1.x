/*
 * Copyright 2002-2015 the original author or authors.
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

package org.springframework.beans.factory.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that indicates 'lookup' methods, to be overridden by the container
 * to redirect them back to the {@link org.springframework.beans.factory.BeanFactory}
 * for a {@code getBean} call. This is essentially an annotation-based version of the
 * XML {@code lookup-method} attribute, resulting in the same runtime arrangement.
 *
 * <p>The resolution of the target bean can either be based on the return type
 * ({@code getBean(Class)}) or on a suggested bean name ({@code getBean(String)}),
 * in both cases passing the method's arguments to the {@code getBean} call
 * for applying them as target factory method arguments or constructor arguments.
 *
 * <p>Such lookup methods can have default (stub) implementations that will simply
 * get replaced by the container, or they can be declared as abstract - for the
 * container to fill them in at runtime. In both cases, the container will generate
 * runtime subclasses of the method's containing class via CGLIB, which is why such
 * lookup methods can only work on beans that the container instantiates through
 * regular constructors: i.e. lookup methods cannot get replaced on beans returned
 * from factory methods where we cannot dynamically provide a subclass for them.
 *
 * <p><b>Concrete limitations in typical Spring configuration scenarios:</b>
 * When used with component scanning or any other mechanism that filters out abstract
 * beans, provide stub implementations of your lookup methods to be able to declare
 * them as concrete classes. And please remember that lookup methods won't work on
 * beans returned from {@code @Bean} methods in configuration classes; you'll have
 * to resort to {@code @Inject Provider<TargetBean>} or the like instead.
 *
 * @author Juergen Hoeller
 * @since 4.1
 * @see org.springframework.beans.factory.BeanFactory#getBean(Class, Object...)
 * @see org.springframework.beans.factory.BeanFactory#getBean(String, Object...)
 *
 * 为了解决A单例bean里引入一个B原型bean的依赖, 在spring容器中A单例bean初始化的时候, 就已经将B原型bean实例化注入了, 会导致被依赖的B原型bean每次都是同一个对象.
 * 这显然不是我们想要的结果, 我们需要B原型bean，而事实上B原型bean的依赖只注入了一次, 变成了事实上的单例bean.
 * 详见: https://note.youdao.com/s/G8BRwG8U
 *
 * 这是一个作用在方法上的注解，被其标注的方法会被重写
 * 被标注的方法要满足一下语法要求:
 * <public|protected> [abstract] <return-type> theMethodName(no-arguments);
 * public|protected要求方法必须是可以被子类重写和调用的
 * abstract可选，如果是抽象方法，CGLIB的动态代理类就会实现这个方法，如果不是抽象方法，就会覆盖这个方法
 * return-type是非单例的类型
 * no-arguments不允许有参数
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Lookup {

	/**
	 * This annotation attribute may suggest a target bean name to look up.
	 * If not specified, the target bean will be resolved based on the
	 * annotated method's return type declaration.
	 */
	String value() default "";

}
