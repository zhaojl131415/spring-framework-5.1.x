/*
 * Copyright 2002-2019 the original author or authors.
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

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.LookupOverride;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * {@link org.springframework.beans.factory.config.BeanPostProcessor} implementation
 * that autowires annotated fields, setter methods and arbitrary config methods.
 * Such members to be injected are detected through a Java 5 annotation: by default,
 * Spring's {@link Autowired @Autowired} and {@link Value @Value} annotations.
 *
 * <p>Also supports JSR-330's {@link javax.inject.Inject @Inject} annotation,
 * if available, as a direct alternative to Spring's own {@code @Autowired}.
 *
 * <p>Only one constructor (at max) of any given bean class may declare this annotation
 * with the 'required' parameter set to {@code true}, indicating <i>the</i> constructor
 * to autowire when used as a Spring bean. If multiple <i>non-required</i> constructors
 * declare the annotation, they will be considered as candidates for autowiring.
 * The constructor with the greatest number of dependencies that can be satisfied by
 * matching beans in the Spring container will be chosen. If none of the candidates
 * can be satisfied, then a primary/default constructor (if present) will be used.
 * If a class only declares a single constructor to begin with, it will always be used,
 * even if not annotated. An annotated constructor does not have to be public.
 *
 * <p>Fields are injected right after construction of a bean, before any
 * config methods are invoked. Such a config field does not have to be public.
 *
 * <p>Config methods may have an arbitrary name and any number of arguments; each of
 * those arguments will be autowired with a matching bean in the Spring container.
 * Bean property setter methods are effectively just a special case of such a
 * general config method. Config methods do not have to be public.
 *
 * <p>Note: A default AutowiredAnnotationBeanPostProcessor will be registered
 * by the "context:annotation-config" and "context:component-scan" XML tags.
 * Remove or turn off the default annotation configuration there if you intend
 * to specify a custom AutowiredAnnotationBeanPostProcessor bean definition.
 * <p><b>NOTE:</b> Annotation injection will be performed <i>before</i> XML injection;
 * thus the latter configuration will override the former for properties wired through
 * both approaches.
 *
 * <p>In addition to regular injection points as discussed above, this post-processor
 * also handles Spring's {@link Lookup @Lookup} annotation which identifies lookup
 * methods to be replaced by the container at runtime. This is essentially a type-safe
 * version of {@code getBean(Class, args)} and {@code getBean(String, args)},
 * See {@link Lookup @Lookup's javadoc} for details.
 *
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @author Stephane Nicoll
 * @author Sebastien Deleuze
 * @since 2.5
 * @see #setAutowiredAnnotationType
 * @see Autowired
 * @see Value
 */
public class AutowiredAnnotationBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter
		implements MergedBeanDefinitionPostProcessor, PriorityOrdered, BeanFactoryAware {

	protected final Log logger = LogFactory.getLog(getClass());

	// 用于储存自动装配的注解类型 @Autowired/@Value/@Inject
	private final Set<Class<? extends Annotation>> autowiredAnnotationTypes = new LinkedHashSet<>(4);

	private String requiredParameterName = "required";

	private boolean requiredParameterValue = true;

	private int order = Ordered.LOWEST_PRECEDENCE - 2;

	@Nullable
	private ConfigurableListableBeanFactory beanFactory;

	private final Set<String> lookupMethodsChecked = Collections.newSetFromMap(new ConcurrentHashMap<>(256));

	/**
	 * 用于存储已经被推断完成的类, 和其对应的所有构造方法
	 */
	private final Map<Class<?>, Constructor<?>[]> candidateConstructorsCache = new ConcurrentHashMap<>(256);

	/** 用于缓存bean对应的注入点元数据<beanName, 注入点元数据对象> */
	private final Map<String, InjectionMetadata> injectionMetadataCache = new ConcurrentHashMap<>(256);


	/**
	 * Create a new {@code AutowiredAnnotationBeanPostProcessor} for Spring's
	 * standard {@link Autowired @Autowired} annotation.
	 * <p>Also supports JSR-330's {@link javax.inject.Inject @Inject} annotation,
	 * if available.
	 */
	@SuppressWarnings("unchecked")
	public AutowiredAnnotationBeanPostProcessor() {
		this.autowiredAnnotationTypes.add(Autowired.class);
		this.autowiredAnnotationTypes.add(Value.class);
		try {
			this.autowiredAnnotationTypes.add((Class<? extends Annotation>)
					ClassUtils.forName("javax.inject.Inject", AutowiredAnnotationBeanPostProcessor.class.getClassLoader()));
			logger.trace("JSR-330 'javax.inject.Inject' annotation found and supported for autowiring");
		}
		catch (ClassNotFoundException ex) {
			// JSR-330 API not available - simply skip.
		}
	}


	/**
	 * Set the 'autowired' annotation type, to be used on constructors, fields,
	 * setter methods and arbitrary config methods.
	 * <p>The default autowired annotation type is the Spring-provided {@link Autowired}
	 * annotation, as well as {@link Value}.
	 * <p>This setter property exists so that developers can provide their own
	 * (non-Spring-specific) annotation type to indicate that a member is supposed
	 * to be autowired.
	 */
	public void setAutowiredAnnotationType(Class<? extends Annotation> autowiredAnnotationType) {
		Assert.notNull(autowiredAnnotationType, "'autowiredAnnotationType' must not be null");
		this.autowiredAnnotationTypes.clear();
		this.autowiredAnnotationTypes.add(autowiredAnnotationType);
	}

	/**
	 * Set the 'autowired' annotation types, to be used on constructors, fields,
	 * setter methods and arbitrary config methods.
	 * <p>The default autowired annotation type is the Spring-provided {@link Autowired}
	 * annotation, as well as {@link Value}.
	 * <p>This setter property exists so that developers can provide their own
	 * (non-Spring-specific) annotation types to indicate that a member is supposed
	 * to be autowired.
	 */
	public void setAutowiredAnnotationTypes(Set<Class<? extends Annotation>> autowiredAnnotationTypes) {
		Assert.notEmpty(autowiredAnnotationTypes, "'autowiredAnnotationTypes' must not be empty");
		this.autowiredAnnotationTypes.clear();
		this.autowiredAnnotationTypes.addAll(autowiredAnnotationTypes);
	}

	/**
	 * Set the name of a parameter of the annotation that specifies whether it is required.
	 * @see #setRequiredParameterValue(boolean)
	 */
	public void setRequiredParameterName(String requiredParameterName) {
		this.requiredParameterName = requiredParameterName;
	}

	/**
	 * Set the boolean value that marks a dependency as required
	 * <p>For example if using 'required=true' (the default), this value should be
	 * {@code true}; but if using 'optional=false', this value should be {@code false}.
	 * @see #setRequiredParameterName(String)
	 */
	public void setRequiredParameterValue(boolean requiredParameterValue) {
		this.requiredParameterValue = requiredParameterValue;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public int getOrder() {
		return this.order;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
			throw new IllegalArgumentException(
					"AutowiredAnnotationBeanPostProcessor requires a ConfigurableListableBeanFactory: " + beanFactory);
		}
		this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
	}


	/**
	 * 找出自动装配注入的元数据
	 * @param beanDefinition the merged bean definition for the bean
	 * @param beanType the actual type of the managed bean instance
	 * @param beanName the name of the bean
	 */
	@Override
	public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
		// 寻找注入点: @Autowired/@Value/@Inject的属性和方法, 构建注入元数据对象
		InjectionMetadata metadata = findAutowiringMetadata(beanName, beanType, null);
		metadata.checkConfigMembers(beanDefinition);
	}

	@Override
	public void resetBeanDefinition(String beanName) {
		this.lookupMethodsChecked.remove(beanName);
		this.injectionMetadataCache.remove(beanName);
	}

	@Override
	@Nullable
	public Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, final String beanName)
			throws BeanCreationException {

		// Let's check for lookup methods here... 在这里检查lookup方法…
		if (!this.lookupMethodsChecked.contains(beanName)) {
			try {
				ReflectionUtils.doWithMethods(beanClass, method -> {
					Lookup lookup = method.getAnnotation(Lookup.class);
					if (lookup != null) {
						Assert.state(this.beanFactory != null, "No BeanFactory available");
						LookupOverride override = new LookupOverride(method, lookup.value());
						try {
							RootBeanDefinition mbd = (RootBeanDefinition)
									this.beanFactory.getMergedBeanDefinition(beanName);
							mbd.getMethodOverrides().addOverride(override);
						}
						catch (NoSuchBeanDefinitionException ex) {
							throw new BeanCreationException(beanName,
									"Cannot apply @Lookup to beans without corresponding bean definition");
						}
					}
				});
			}
			catch (IllegalStateException ex) {
				throw new BeanCreationException(beanName, "Lookup method resolution failed", ex);
			}
			this.lookupMethodsChecked.add(beanName);
		}

		// Quick check on the concurrent map first, with minimal locking. 首先快速检查并发映射，并使用最少的锁。
		// 根据类获取已经被推断完成的类和对应的所有构造方法
		// todo:这个地方的场景可能在配置类中没有@Configuration, 其中@Bean方法调用其他的@Bean方法
		Constructor<?>[] candidateConstructors = this.candidateConstructorsCache.get(beanClass);
		if (candidateConstructors == null) {
			// Fully synchronized resolution now...
			synchronized (this.candidateConstructorsCache) {
				// 这里类似 double check lock
				candidateConstructors = this.candidateConstructorsCache.get(beanClass);
				if (candidateConstructors == null) {
					Constructor<?>[] rawCandidates;
					try {
						rawCandidates = beanClass.getDeclaredConstructors();
					}
					catch (Throwable ex) {
						throw new BeanCreationException(beanName,
								"Resolution of declared constructors on bean Class [" + beanClass.getName() +
								"] from ClassLoader [" + beanClass.getClassLoader() + "] failed", ex);
					}
					// 用于存储合格的构造方法
					List<Constructor<?>> candidates = new ArrayList<>(rawCandidates.length);
					Constructor<?> requiredConstructor = null; // 必需的构造方法:@AutoWired标注的构造方法
					Constructor<?> defaultConstructor = null; // 默认的构造方法:默认无参构造方法
					// 把推断主要的构造方法委托给Kotlin, 而对于非Kotlin类, 全都返回空. 这里对Java而言可以理解为一直为null
					Constructor<?> primaryConstructor = BeanUtils.findPrimaryConstructor(beanClass);
					// 非合成构造方法计数器
					int nonSyntheticConstructors = 0;
					for (Constructor<?> candidate : rawCandidates) {
						if (!candidate.isSynthetic()) {
							nonSyntheticConstructors++;
						}
						else if (primaryConstructor != null) {
							continue;
						}
						// 查找构造方法上的注解属性
						AnnotationAttributes ann = findAutowiredAnnotation(candidate);
						if (ann == null) {
							Class<?> userClass = ClassUtils.getUserClass(beanClass);
							// 判断class是否相等, beanClass 表示bd存的class, userClass构造方法所在的class, 内部类不相等
							if (userClass != beanClass) {
								try {
									Constructor<?> superCtor =
											userClass.getDeclaredConstructor(candidate.getParameterTypes());
									ann = findAutowiredAnnotation(superCtor);
								}
								catch (NoSuchMethodException ex) {
									// Simply proceed, no equivalent superclass constructor found...
								}
							}
						}
						if (ann != null) {
							if (requiredConstructor != null) {
								// 表示有多个添加@AutoWired的构造方法
								throw new BeanCreationException(beanName,
										"Invalid autowire-marked constructor: " + candidate +
										". Found constructor with 'required' Autowired annotation already: " +
										requiredConstructor);
							}
							// 推断@AutoWired中的required
							boolean required = determineRequiredStatus(ann);
							if (required) {
								if (!candidates.isEmpty()) {
									// 表示有多个添加@AutoWired的构造方法
									throw new BeanCreationException(beanName,
											"Invalid autowire-marked constructors: " + candidates +
											". Found constructor with 'required' Autowired annotation: " +
											candidate);
								}
								requiredConstructor = candidate;
							}
							// 如果多个@AutoWired(required = false)不会报错, 会返回多个
							candidates.add(candidate);
						}
						// 如果构造方法的参数数量等于0, 赋值给默认构造方法
						else if (candidate.getParameterCount() == 0) {
							defaultConstructor = candidate;
						}
					}
					if (!candidates.isEmpty()) {
						// Add default constructor to list of optional constructors, as fallback.
						if (requiredConstructor == null) {
							if (defaultConstructor != null) {
								candidates.add(defaultConstructor);
							}
							else if (candidates.size() == 1 && logger.isInfoEnabled()) {
								logger.info("Inconsistent constructor declaration on bean with name '" + beanName +
										"': single autowire-marked constructor flagged as optional - " +
										"this constructor is effectively required since there is no " +
										"default constructor to fall back to: " + candidates.get(0));
							}
						}
						candidateConstructors = candidates.toArray(new Constructor<?>[0]);
					}
					else if (rawCandidates.length == 1 && rawCandidates[0].getParameterCount() > 0) {
						candidateConstructors = new Constructor<?>[] {rawCandidates[0]};
					}
					else if (nonSyntheticConstructors == 2 && primaryConstructor != null &&
							defaultConstructor != null && !primaryConstructor.equals(defaultConstructor)) {
						candidateConstructors = new Constructor<?>[] {primaryConstructor, defaultConstructor};
					}
					else if (nonSyntheticConstructors == 1 && primaryConstructor != null) {
						candidateConstructors = new Constructor<?>[] {primaryConstructor};
					}
					else {
						candidateConstructors = new Constructor<?>[0];
					}
					this.candidateConstructorsCache.put(beanClass, candidateConstructors);
				}
			}
		}
		return (candidateConstructors.length > 0 ? candidateConstructors : null);
	}

	/**
	 * 找出自动装配注入的元数据，进行依赖注入
	 * @param pvs
	 * @param bean
	 * @param beanName
	 * @return
	 */
	@Override
	public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) {
		// 寻找注入点: @Autowired/@Value/@Inject的属性和方法, 构建注入元数据对象
		InjectionMetadata metadata = findAutowiringMetadata(beanName, bean.getClass(), pvs);
		try {
			// 依赖注入: 关键
			metadata.inject(bean, beanName, pvs);
		}
		catch (BeanCreationException ex) {
			throw ex;
		}
		catch (Throwable ex) {
			throw new BeanCreationException(beanName, "Injection of autowired dependencies failed", ex);
		}
		return pvs;
	}

	@Deprecated
	@Override
	public PropertyValues postProcessPropertyValues(
			PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) {

		return postProcessProperties(pvs, bean, beanName);
	}

	/**
	 * 'Native' processing method for direct calls with an arbitrary target instance,
	 * resolving all of its fields and methods which are annotated with {@code @Autowired}.
	 * @param bean the target instance to process
	 * @throws BeanCreationException if autowiring failed
	 */
	public void processInjection(Object bean) throws BeanCreationException {
		Class<?> clazz = bean.getClass();
		InjectionMetadata metadata = findAutowiringMetadata(clazz.getName(), clazz, null);
		try {
			metadata.inject(bean, null, null);
		}
		catch (BeanCreationException ex) {
			throw ex;
		}
		catch (Throwable ex) {
			throw new BeanCreationException(
					"Injection of autowired dependencies failed for class [" + clazz + "]", ex);
		}
	}

	/**
	 * 这个方法有几处调用
	 * 1、刚实例化生成bean后，第三次调用后置处理器，会先调用此方法，找出添加了@AutoWired的自动装配元数据
	 * 2、在填充属性方法中，第六次调用后置处理器，会调用此方法，获取上一次调用此方法找出的自动装配元数据，如果上一次没找到，就再找一次
	 * @param beanName
	 * @param clazz
	 * @param pvs
	 * @return
	 */
	private InjectionMetadata findAutowiringMetadata(String beanName, Class<?> clazz, @Nullable PropertyValues pvs) {
		// Fall back to class name as cache key, for backwards compatibility with custom callers.
		// 返回类名作为缓存键，以便向后兼容自定义调用程序。
		String cacheKey = (StringUtils.hasLength(beanName) ? beanName : clazz.getName());
		// Quick check on the concurrent map first, with minimal locking.
		// 首先快速检查并发映射，并使用最少的锁。
		// 注入元数据: 这里缓存应该是为了原型对象服务的, 因为对于单例对象而言, 反正都是要查找注入的元数据, 实例化前后没有什么区别
		// 从缓存中获取当前对象的所有注入点
		InjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
		// 判断是否需要刷新:metadata是否为空
		if (InjectionMetadata.needsRefresh(metadata, clazz)) {
			synchronized (this.injectionMetadataCache) {
				metadata = this.injectionMetadataCache.get(cacheKey);
				if (InjectionMetadata.needsRefresh(metadata, clazz)) {
					// 如果注入点需要刷新, 则清空原来的数据
					if (metadata != null) {
						metadata.clear(pvs);
					}
					// 寻找当前类的注入点, 把所有的注入点整合成一个InjectionMetadata对象
					metadata = buildAutowiringMetadata(clazz);
					// 存入缓存
					this.injectionMetadataCache.put(cacheKey, metadata);
				}
			}
		}
		return metadata;
	}

	/**
	 * 寻找当前类的注入点, 把所有的注入点整合成一个InjectionMetadata对象
	 * @param clazz
	 * @return
	 */
	private InjectionMetadata buildAutowiringMetadata(final Class<?> clazz) {
		// 5.3版本此处做了优化, 判断bean的类型是否需要进行依赖注入

		// 用于存储所有需要注入的元素
		List<InjectionMetadata.InjectedElement> elements = new ArrayList<>();
		Class<?> targetClass = clazz;

		do {
			final List<InjectionMetadata.InjectedElement> currElements = new ArrayList<>();
			// 遍历bean的所有field
			ReflectionUtils.doWithLocalFields(targetClass, field -> {
				// 查找field的注解属性
				AnnotationAttributes ann = findAutowiredAnnotation(field);
				if (ann != null) {
					// 如果找到字段上的@Autowired/@Value/@Inject注入注解, 判断字段是否为静态的
					if (Modifier.isStatic(field.getModifiers())) {
						// 静态的不处理注入
						// 因为如果当前bean是原型bean, 其中的某个属性也是原型bean, 这里静态属性如果可以注入的话,
						// 会在当前原型bean多次创建的时候, 都会给这个属性注入一个新的对象,
						// 但是基于静态对象的jvm内存存储, 会出现第一次创建的原型bean对象访问的这个属性的时候, 访问到的是最后一次创建原型bean内的属性.
						// 既然会出现错乱的情况, 这里则只能选择静态的不处理注入
						if (logger.isInfoEnabled()) {
							logger.info("Autowired annotation is not supported on static fields: " + field);
						}
						return;
					}
					/**
					 * 推断注解里required的值
					 * @see Autowired
					 * 例: @Autowired(required = true), 表示属性必须要注入
					 */
					boolean required = determineRequiredStatus(ann);
					// 构造注入点
					currElements.add(new AutowiredFieldElement(field, required));
				}
			});
			// 遍历bean的所有Methods, 跟上一段代码类似
			ReflectionUtils.doWithLocalMethods(targetClass, method -> {
				// 根据当前方法寻找相关的桥接方法(跟泛型有关)
				Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
				if (!BridgeMethodResolver.isVisibilityBridgeMethodPair(method, bridgedMethod)) {
					return;
				}
				// 获取方法上的@Autowired/@Value/@Inject注入注解
				AnnotationAttributes ann = findAutowiredAnnotation(bridgedMethod);
				if (ann != null && method.equals(ClassUtils.getMostSpecificMethod(method, clazz))) {
					// 静态方法不注入
					if (Modifier.isStatic(method.getModifiers())) {
						if (logger.isInfoEnabled()) {
							logger.info("Autowired annotation is not supported on static methods: " + method);
						}
						return;
					}
					// 方法参数数量为0, 输入日志, 继续执行
					if (method.getParameterCount() == 0) {
						if (logger.isInfoEnabled()) {
							logger.info("Autowired annotation should only be used on methods with parameters: " +
									method);
						}
					}
					boolean required = determineRequiredStatus(ann);
					PropertyDescriptor pd = BeanUtils.findPropertyForMethod(bridgedMethod, clazz);
					currElements.add(new AutowiredMethodElement(method, required, pd));
				}
			});

			elements.addAll(0, currElements);
			// 递归当前类的父类
			targetClass = targetClass.getSuperclass();
		}
		// 当前类不为空 且 不为Object类时 继续循环, 也就是说 当前类为空或者当前类为Object类时, 结束循环.
		while (targetClass != null && targetClass != Object.class);
		// 实例化一个注入元数据, 存储类和类对应的所有需要注入的元素
		return new InjectionMetadata(clazz, elements);
	}

	@Nullable
	private AnnotationAttributes findAutowiredAnnotation(AccessibleObject ao) {
		if (ao.getAnnotations().length > 0) {  // autowiring annotations have to be local 自动装配注解必须是本地的
			// this.autowiredAnnotationTypes: @Autowired/@Value/@Inject
			for (Class<? extends Annotation> type : this.autowiredAnnotationTypes) {
				// 获取@Autowired/@Value/@Inject注解的属性
				// 因为autowiredAnnotationTypes是一个LinkedHashSet, 所以会按照顺序去判断是否有@Autowired/@Value/@Inject注解, 只有其中一个存在就返回
				AnnotationAttributes attributes = AnnotatedElementUtils.getMergedAnnotationAttributes(ao, type);
				if (attributes != null) {
					return attributes;
				}
			}
		}
		return null;
	}

	/**
	 * Determine if the annotated field or method requires its dependency.
	 * <p>A 'required' dependency means that autowiring should fail when no beans
	 * are found. Otherwise, the autowiring process will simply bypass the field
	 * or method when no beans are found.
	 * @param ann the Autowired annotation
	 * @return whether the annotation indicates that a dependency is required
	 */
	protected boolean determineRequiredStatus(AnnotationAttributes ann) {
		// 判断注解的required
		return (!ann.containsKey(this.requiredParameterName) ||
				this.requiredParameterValue == ann.getBoolean(this.requiredParameterName));
	}

	/**
	 * Obtain all beans of the given type as autowire candidates.
	 * @param type the type of the bean
	 * @return the target beans, or an empty Collection if no bean of this type is found
	 * @throws BeansException if bean retrieval failed
	 */
	protected <T> Map<String, T> findAutowireCandidates(Class<T> type) throws BeansException {
		if (this.beanFactory == null) {
			throw new IllegalStateException("No BeanFactory configured - " +
					"override the getBeanOfType method or specify the 'beanFactory' property");
		}
		return BeanFactoryUtils.beansOfTypeIncludingAncestors(this.beanFactory, type);
	}

	/**
	 * Register the specified bean as dependent on the autowired beans.
	 */
	private void registerDependentBeans(@Nullable String beanName, Set<String> autowiredBeanNames) {
		if (beanName != null) {
			for (String autowiredBeanName : autowiredBeanNames) {
				if (this.beanFactory != null && this.beanFactory.containsBean(autowiredBeanName)) {
					this.beanFactory.registerDependentBean(autowiredBeanName, beanName);
				}
				if (logger.isTraceEnabled()) {
					logger.trace("Autowiring by type from bean name '" + beanName +
							"' to bean named '" + autowiredBeanName + "'");
				}
			}
		}
	}

	/**
	 * Resolve the specified cached method argument or field value.
	 */
	@Nullable
	private Object resolvedCachedArgument(@Nullable String beanName, @Nullable Object cachedArgument) {
		if (cachedArgument instanceof DependencyDescriptor) {
			/**
			 * 这里的缓存其实是: {@link ShortcutDependencyDescriptor}  快捷依赖描述符
			 */
			DependencyDescriptor descriptor = (DependencyDescriptor) cachedArgument;
			Assert.state(this.beanFactory != null, "No BeanFactory available");
			/**
			 * 通过快捷依赖描述符快速获取对应的注入对象
			 * @see DefaultListableBeanFactory#resolveDependency(DependencyDescriptor, String, Set, TypeConverter)
			 */
			return this.beanFactory.resolveDependency(descriptor, beanName, null, null);
		}
		else {
			return cachedArgument;
		}
	}


	/**
	 * Class representing injection information about an annotated field.
	 */
	private class AutowiredFieldElement extends InjectionMetadata.InjectedElement {

		private final boolean required;

		// 这两个缓存其实是提供给非单例bean的情况使用的, 单例bean一次就过去了, 不会再回来了, 所以不需要缓存
		// 判断是否有缓存
		private volatile boolean cached = false;

		// 缓存字段的值
		@Nullable
		private volatile Object cachedFieldValue;

		public AutowiredFieldElement(Field field, boolean required) {
			super(field, null);
			this.required = required;
		}

		@Override
		protected void inject(Object bean, @Nullable String beanName, @Nullable PropertyValues pvs) throws Throwable {
			Field field = (Field) this.member;
			// 用于存储spring从容器中取出来，需要被注入的对象
			Object value;
			// cached 默认为false，等整个属性完成的时候改为true，会给cachedFieldValue赋值：ShortcutDependencyDescriptor
			if (this.cached) {
				// 对于非单例bean, 第一次创建的时候, 也找注入点, 然后进行属性注入, 此时cashe为false, 注入完成后cache为true,
				// 第二次创建的时候, 先找注入点(此时会拿到缓存好的注入点), 此时的cache为true, 也就会进入这里,
				// 注入点内并没有缓存被注入的具体bean对象, 而是beanName, 这样就能保证会创建不同的bean对象注入
				// 表示需要被注入的对象，已经有缓存，不再需要对属性进行解析去获取需要注入的值
				/**
				 * 从缓存中获取的快捷依赖描述符, spring容器通过这个快捷依赖描述符可以直接创建一个对象. 可以省略掉一些之间的流程.
				 * @see ShortcutDependencyDescriptor
				 */
				value = resolvedCachedArgument(beanName, this.cachedFieldValue);
			}
			else {
				// 表示需要被注入的对象没有缓存，没有解析过，需要对属性进行解析去获取需要注入的值
				// 依赖描述器：包含这个属性，是否必须注入
				DependencyDescriptor desc = new DependencyDescriptor(field, this.required);
				desc.setContainingClass(bean.getClass());
				Set<String> autowiredBeanNames = new LinkedHashSet<>(1);
				Assert.state(beanFactory != null, "No BeanFactory available");
				// 类型转换器
				TypeConverter typeConverter = beanFactory.getTypeConverter();
				try {
					/**
					 * value为spring从容器中取出来，需要被注入的对象
					 * 这个需要被注入的对象，通过getBean()去获取，有则获取，无则创建
					 * @see DefaultListableBeanFactory#resolveDependency(DependencyDescriptor, String, Set, TypeConverter)
					 */
					value = beanFactory.resolveDependency(desc, beanName, autowiredBeanNames, typeConverter);
				}
				catch (BeansException ex) {
					throw new UnsatisfiedDependencyException(null, beanName, new InjectionPoint(field), ex);
				}
				synchronized (this) {
					if (!this.cached) {
						if (value != null || this.required) {
							this.cachedFieldValue = desc;
							registerDependentBeans(beanName, autowiredBeanNames);
							if (autowiredBeanNames.size() == 1) {
								String autowiredBeanName = autowiredBeanNames.iterator().next();
								if (beanFactory.containsBean(autowiredBeanName) &&
										beanFactory.isTypeMatch(autowiredBeanName, field.getType())) {
									// 存入缓存, 便于之后在进入此方法中, 在上面代码中的缓存能获取到.
									// 这里并没有缓存被注入的具体bean对象, 而是beanName, 构建了一个快捷依赖描述符, 这样为了在非单例bean后续注入可以快速创建而准备的
									this.cachedFieldValue = new ShortcutDependencyDescriptor(
											desc, autowiredBeanName, field.getType());
								}
							}
						}
						else {
							this.cachedFieldValue = null;
						}
						this.cached = true;
					}
				}
			}
			// 如果找到需要被注入的对象
			if (value != null) {
				// 通过反射, 将属性赋值.
				ReflectionUtils.makeAccessible(field);
				// 把value set到这个field上
				field.set(bean, value);
			}
		}
	}


	/**
	 * Class representing injection information about an annotated method.
	 */
	private class AutowiredMethodElement extends InjectionMetadata.InjectedElement {

		private final boolean required;

		// 这两个缓存其实是提供给非单例bean的情况使用的, 单例bean一次就过去了, 不会再回来了, 所以不需要缓存
		// 判断是否有缓存
		private volatile boolean cached = false;

		// 缓存字段的值
		@Nullable
		private volatile Object[] cachedMethodArguments;

		public AutowiredMethodElement(Method method, boolean required, @Nullable PropertyDescriptor pd) {
			super(method, pd);
			this.required = required;
		}

		@Override
		protected void inject(Object bean, @Nullable String beanName, @Nullable PropertyValues pvs) throws Throwable {
			// 如果pvs中已经有当前注入点的值, 则跳过注入
			if (checkPropertySkipping(pvs)) {
				return;
			}
			// 成员为方法
			Method method = (Method) this.member;
			Object[] arguments;
			// 是否有缓存, 可以参照上面字段属性注入方法的实现
			if (this.cached) {
				// Shortcut for avoiding synchronization...
				arguments = resolveCachedArguments(beanName);
			}
			else {
				// 获取方法的参数类型数组
				Class<?>[] paramTypes = method.getParameterTypes();
				// 实例化一个长度为: 方法参数个数 的对象数组, 用于存储每个参数对应的注入对象
				arguments = new Object[paramTypes.length];
				// 实例化一个依赖描述符数组
				DependencyDescriptor[] descriptors = new DependencyDescriptor[paramTypes.length];
				Set<String> autowiredBeans = new LinkedHashSet<>(paramTypes.length);
				Assert.state(beanFactory != null, "No BeanFactory available");
				// 类型转换器
				TypeConverter typeConverter = beanFactory.getTypeConverter();
				// 遍历方法参数
				for (int i = 0; i < arguments.length; i++) {
					// 获取方法对应下标的参数
					MethodParameter methodParam = new MethodParameter(method, i);
					// 每个参数对应的依赖描述符
					DependencyDescriptor currDesc = new DependencyDescriptor(methodParam, this.required);
					currDesc.setContainingClass(bean.getClass());
					// 存入依赖描述符数组
					descriptors[i] = currDesc;
					try {
						/**
						 * 根据对应的参数找到匹配的bean对象
						 * @see DefaultListableBeanFactory#resolveDependency(DependencyDescriptor, String, Set, TypeConverter)
						 */
						Object arg = beanFactory.resolveDependency(currDesc, beanName, autowiredBeans, typeConverter);
						if (arg == null && !this.required) {
							arguments = null;
							break;
						}
						// 将对象根据下标存入对应的数组
						arguments[i] = arg;
					}
					catch (BeansException ex) {
						throw new UnsatisfiedDependencyException(null, beanName, new InjectionPoint(methodParam), ex);
					}
				}
				synchronized (this) {
					if (!this.cached) {
						if (arguments != null) {
							Object[] cachedMethodArguments = new Object[paramTypes.length];
							System.arraycopy(descriptors, 0, cachedMethodArguments, 0, arguments.length);
							registerDependentBeans(beanName, autowiredBeans);
							if (autowiredBeans.size() == paramTypes.length) {
								Iterator<String> it = autowiredBeans.iterator();
								for (int i = 0; i < paramTypes.length; i++) {
									String autowiredBeanName = it.next();
									if (beanFactory.containsBean(autowiredBeanName) &&
											beanFactory.isTypeMatch(autowiredBeanName, paramTypes[i])) {
										cachedMethodArguments[i] = new ShortcutDependencyDescriptor(
												descriptors[i], autowiredBeanName, paramTypes[i]);
									}
								}
							}
							this.cachedMethodArguments = cachedMethodArguments;
						}
						else {
							this.cachedMethodArguments = null;
						}
						this.cached = true;
					}
				}
			}
			if (arguments != null) {
				try {
					// 通过反射, 对属性赋值
					ReflectionUtils.makeAccessible(method);
					method.invoke(bean, arguments);
				}
				catch (InvocationTargetException ex) {
					throw ex.getTargetException();
				}
			}
		}

		@Nullable
		private Object[] resolveCachedArguments(@Nullable String beanName) {
			Object[] cachedMethodArguments = this.cachedMethodArguments;
			if (cachedMethodArguments == null) {
				return null;
			}
			Object[] arguments = new Object[cachedMethodArguments.length];
			for (int i = 0; i < arguments.length; i++) {
				arguments[i] = resolvedCachedArgument(beanName, cachedMethodArguments[i]);
			}
			return arguments;
		}
	}


	/**
	 * DependencyDescriptor variant with a pre-resolved target bean name.
	 */
	@SuppressWarnings("serial")
	private static class ShortcutDependencyDescriptor extends DependencyDescriptor {

		private final String shortcut;

		private final Class<?> requiredType;

		public ShortcutDependencyDescriptor(DependencyDescriptor original, String shortcut, Class<?> requiredType) {
			super(original);
			this.shortcut = shortcut;
			this.requiredType = requiredType;
		}

		// 不是直接将Bean缓存的, 而是缓存的快照
		@Override
		public Object resolveShortcut(BeanFactory beanFactory) {
			return beanFactory.getBean(this.shortcut, this.requiredType);
		}
	}

}
