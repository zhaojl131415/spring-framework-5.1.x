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

package org.springframework.context.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.lang.Nullable;

/**
 * Delegate for AbstractApplicationContext's post-processor handling.
 *
 * @author Juergen Hoeller
 * @since 4.0
 */
final class PostProcessorRegistrationDelegate {

	private PostProcessorRegistrationDelegate() {
	}


	/**
	 * 执行bean工厂后置处理器
	 *
	 *
	 * 这里的步骤为:
	 * 1 先执行BeanDefinitionRegistryPostProcessor的实现类
	 * 		1.1 执行通过api自定义的.postProcessBeanDefinitionRegistry():ac.addBeanFactoryPostProcessor(new ZhaoBeanDefinitionRegistryPostProcessor());
	 * 		1.2 执行所有的(spring内置和@Component注解自定义的)PriorityOrdered的.postProcessBeanDefinitionRegistry()
	 * 		1.3 执行所有的(spring内置和@Component注解自定义的)Ordered的.postProcessBeanDefinitionRegistry()
	 * 		1.4 执行所有的(spring内置和@Component注解自定义的)剩余的.postProcessBeanDefinitionRegistry()
	 * 		1.5	执行所有的(spring内置和api自定义和@Component注解自定义的)的BeanDefinitionRegistryPostProcessor.postProcessBeanFactory()
	 * 		1.6	执行api自定义的BeanDefinitionPostProcessor.postProcessBeanFactory()
	 * 2 再执行BeanDefinitionPostProcessor的实现类
	 * 		2.1 执行PriorityOrdered的.postProcessBeanFactory()
	 * 		2.2 执行Ordered的.postProcessBeanFactory()
	 * 		2.3 执行剩余的.postProcessBeanFactory()
	 *
	 *
	 * 先执行spring内置的，然后执行自定义的
	 * @param beanFactory
	 * @param beanFactoryPostProcessors 一般情况没值, 有值一般也就两种类型, 这里如果是通过api直接提供的才会有, 比如:
	 *                                  ac.addBeanFactoryPostProcessor(new ZhaoBeanFactoryPostProcessor());
	 *                                  ac.addBeanFactoryPostProcessor(new ZhaoBeanDefinitionRegistryPostProcessor());
	 *
	 */
	public static void invokeBeanFactoryPostProcessors(
			ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {

		// Invoke BeanDefinitionRegistryPostProcessors first, if any. 如果有的话，先调用BeanDefinitionRegistryPostProcessors。
		// 所有存在的BeanDefinitionRegistryPostProcessors的名字
		Set<String> processedBeans = new HashSet<>();

		if (beanFactory instanceof BeanDefinitionRegistry) {
			BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
			// 存放自定义的BeanFactoryPostProcessor:现在不调用
			List<BeanFactoryPostProcessor> regularPostProcessors = new ArrayList<>();
			// 存放自定义的BeanDefinitionRegistryPostProcessor:现在就调用
			// 存放所有spring内置的BeanFactoryPostProcessor
			List<BeanDefinitionRegistryPostProcessor> registryProcessors = new ArrayList<>();

			//----------------------------------------------1.1----------------------------------------------
			// 方法调用时候传进来的List<BeanFactoryPostProcessor>, 方法传参处有详细说明
			for (BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors) {
				// 判断是否spring内置bean工厂内置处理器
				// 如果有自定义的类实现了BeanDefinitionRegistryPostProcessor，先执行谁的？看Ordered
				if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
					// 这里可能包含BeanDefinitionRegistry的子类
					BeanDefinitionRegistryPostProcessor registryProcessor =
							(BeanDefinitionRegistryPostProcessor) postProcessor;
					// 执行自定义的BeanDefinitionRegistryPostProcessor.postProcessBeanDefinitionRegistry方法
					registryProcessor.postProcessBeanDefinitionRegistry(registry);
					registryProcessors.add(registryProcessor);
				}
				else {
					// 不属于BeanDefinitionRegistryPostProcessor, 就属于BeanFactoryPostProcessor
					regularPostProcessors.add(postProcessor);
				}
			}
			//-----------------------------------------------------------------------------------------------

			// Do not initialize FactoryBeans here: We need to leave all regular beans
			// uninitialized to let the bean factory post-processors apply to them!
			// Separate between BeanDefinitionRegistryPostProcessors that implement
			// PriorityOrdered, Ordered, and the rest.
			// 不要在这里初始化factorybean:我们需要保持所有常规bean未初始化，以便让bean factory后处理器应用于它们!
			// 独立于BeanDefinitionRegistryPostProcessors，它实现了PriorityOrdered, Ordered和其他。
			// 这个List存的是当前需要执行的 BeanDefinitionRegistryPostProcessor 集合
			// 当前？因为这里spring使用了策略模式，不同策略执行时机不同
			List<BeanDefinitionRegistryPostProcessor> currentRegistryProcessors = new ArrayList<>();

			//----------------------------------------------1.2----------------------------------------------
			// First, invoke the BeanDefinitionRegistryPostProcessors that implement PriorityOrdered.
			// 首先，调用实现PriorityOrdered的BeanDefinitionRegistryPostProcessors
			// 通俗的讲 这一步就可以理解为找女朋友要找身材好的大长腿女孩
			// 根据BeanDefinitionRegistryPostProcessor类型从BeanDefinitionMap中找到名字
			// ConfigurationClassPostProcessor
			String[] postProcessorNames =
					beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
			for (String ppName : postProcessorNames) {
				// 判断这个名字所对应的bd是否实现了PriorityOrdered（优先级排序）
				if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
					// currentRegistryProcessors存的是当前需要执行的类，执行完之后会清空
					// beanFactory.getBean 直接从容器中拿，拿不到再实例化
					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
					// processedBeans存的是已经找到的的类的名字
					processedBeans.add(ppName);
				}
			}
			// 排序
			sortPostProcessors(currentRegistryProcessors, beanFactory);
			// registryProcessors存的是已经找到的的类
			registryProcessors.addAll(currentRegistryProcessors);
			// 策略模式：执行的是 ConfigurationClassPostProcessor.postProcessBeanDefinitionRegistry
			// 完成扫描，扫描指定目录下的@Compent的bean
			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
			// 执行完，清空currentRegistryProcessors，给第二、三步用
			currentRegistryProcessors.clear();
			//-----------------------------------------------------------------------------------------------

			//----------------------------------------------1.3----------------------------------------------
			// Next, invoke the BeanDefinitionRegistryPostProcessors that implement Ordered.
			// 接下来，调用实现Ordered的BeanDefinitionRegistryPostProcessors
			// 通俗的讲 这一步就可以理解为找大长腿女孩
			postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
			for (String ppName : postProcessorNames) {
				// 这里还会找到第一步中找到的ConfigurationClassPostProcessor, 但是第一步中已经执行过了，所以这里要!processedBeans.contains(ppName)
				// 可能这里还会找到一些其他的后置处理器：自定义的、第三方的（比如Mybatis），如果没有currentRegistryProcessors可能为空
				if (!processedBeans.contains(ppName) && beanFactory.isTypeMatch(ppName, Ordered.class)) {
					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
					processedBeans.add(ppName);
				}
			}
			// 排序
			sortPostProcessors(currentRegistryProcessors, beanFactory);
			// registryProcessors存的是已经找到的的类
			registryProcessors.addAll(currentRegistryProcessors);
			// 策略模式：执行postProcessBeanDefinitionRegistry
			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
			// 清空currentRegistryProcessors，给第三步用
			currentRegistryProcessors.clear();
			//-----------------------------------------------------------------------------------------------

			//----------------------------------------------1.4----------------------------------------------
			// Finally, invoke all other BeanDefinitionRegistryPostProcessors until no further ones appear.
			// 最后，调用所有其他的BeanDefinitionRegistryPostProcessors实现类，直到没有。
			// 通俗的讲 这一步就可以理解为只要是个女孩就行
			boolean reiterate = true;
			while (reiterate) {
				reiterate = false;
				postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
				for (String ppName : postProcessorNames) {
					if (!processedBeans.contains(ppName)) {
						currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
						processedBeans.add(ppName);
						reiterate = true;
					}
				}
				sortPostProcessors(currentRegistryProcessors, beanFactory);
				registryProcessors.addAll(currentRegistryProcessors);
				// 策略模式：执行postProcessBeanDefinitionRegistry
				invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
				currentRegistryProcessors.clear();
			}
			//-----------------------------------------------------------------------------------------------

			//----------------------------------------------1.5----------------------------------------------
			// Now, invoke the postProcessBeanFactory callback of all processors handled so far.
			// 执行spring内置的BeanFactoryPostProcessor.postProcessBeanFactory
			// 执行自定义的BeanDefinitionRegistryPostProcessor.postProcessBeanFactory
			invokeBeanFactoryPostProcessors(registryProcessors, beanFactory);
			//----------------------------------------------1.6----------------------------------------------
			// 执行自定义的BeanFactoryPostProcessor.postProcessBeanFactory
			invokeBeanFactoryPostProcessors(regularPostProcessors, beanFactory);
		}

		else {
			// Invoke factory processors registered with the context instance.
			// 调用通过上下文实例注册的工厂处理器。
			invokeBeanFactoryPostProcessors(beanFactoryPostProcessors, beanFactory);
		}

		// Do not initialize FactoryBeans here: We need to leave all regular beans
		// uninitialized to let the bean factory post-processors apply to them!
		// 不要在这里初始化factorybean:我们需要保持所有常规bean未初始化，以便让bean factory后处理器应用于它们!
		/**
		 * 这里是找BeanFactoryPostProcessor的实现类，前面找的是BeanDefinitionRegistryPostProcessor的实现类,
		 * 二者的关系是：BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor,
		 * 也就是说先找子类的，再找父类的
		 */
		String[] postProcessorNames =
				beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, true, false);

		// Separate between BeanFactoryPostProcessors that implement PriorityOrdered,
		// Ordered, and the rest.
		// 将实现PriorityOrdered、Ordered和其他的BeanFactoryPostProcessors分开。他们之间的关系史：PriorityOrdered extends Ordered
		// 用于存储实现PriorityOrdered的后置处理器
		List<BeanFactoryPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
		// 用于存储实现Ordered的
		List<String> orderedPostProcessorNames = new ArrayList<>();
		// 用于存储没有实现Ordered的
		List<String> nonOrderedPostProcessorNames = new ArrayList<>();
		for (String ppName : postProcessorNames) {
			if (processedBeans.contains(ppName)) {
				// skip - already processed in first phase above
			}
			else if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
				priorityOrderedPostProcessors.add(beanFactory.getBean(ppName, BeanFactoryPostProcessor.class));
			}
			else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
				orderedPostProcessorNames.add(ppName);
			}
			else {
				nonOrderedPostProcessorNames.add(ppName);
			}
		}

		//----------------------------------------------2.1----------------------------------------------
		// First, invoke the BeanFactoryPostProcessors that implement PriorityOrdered.
		// 首先，调用实现PriorityOrdered的BeanFactoryPostProcessors。
		sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
		invokeBeanFactoryPostProcessors(priorityOrderedPostProcessors, beanFactory);

		//----------------------------------------------2.2----------------------------------------------
		// Next, invoke the BeanFactoryPostProcessors that implement Ordered.
		// 接下来，调用实现Ordered的BeanFactoryPostProcessors
		List<BeanFactoryPostProcessor> orderedPostProcessors = new ArrayList<>();
		for (String postProcessorName : orderedPostProcessorNames) {
			orderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
		}
		sortPostProcessors(orderedPostProcessors, beanFactory);
		invokeBeanFactoryPostProcessors(orderedPostProcessors, beanFactory);

		//----------------------------------------------2.3----------------------------------------------
		// Finally, invoke all other BeanFactoryPostProcessors.
		// 最后，调用所有其他BeanFactoryPostProcessors。
		List<BeanFactoryPostProcessor> nonOrderedPostProcessors = new ArrayList<>();
		for (String postProcessorName : nonOrderedPostProcessorNames) {
			nonOrderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
		}
		invokeBeanFactoryPostProcessors(nonOrderedPostProcessors, beanFactory);

		// Clear cached merged bean definitions since the post-processors might have
		// modified the original metadata, e.g. replacing placeholders in values...
		beanFactory.clearMetadataCache();
	}

	public static void registerBeanPostProcessors(
			ConfigurableListableBeanFactory beanFactory, AbstractApplicationContext applicationContext) {

		String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanPostProcessor.class, true, false);

		// Register BeanPostProcessorChecker that logs an info message when
		// a bean is created during BeanPostProcessor instantiation, i.e. when
		// a bean is not eligible for getting processed by all BeanPostProcessors.
		int beanProcessorTargetCount = beanFactory.getBeanPostProcessorCount() + 1 + postProcessorNames.length;
		beanFactory.addBeanPostProcessor(new BeanPostProcessorChecker(beanFactory, beanProcessorTargetCount));

		// Separate between BeanPostProcessors that implement PriorityOrdered,
		// Ordered, and the rest.
		List<BeanPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
		List<BeanPostProcessor> internalPostProcessors = new ArrayList<>();
		List<String> orderedPostProcessorNames = new ArrayList<>();
		List<String> nonOrderedPostProcessorNames = new ArrayList<>();
		for (String ppName : postProcessorNames) {
			if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
				BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
				priorityOrderedPostProcessors.add(pp);
				if (pp instanceof MergedBeanDefinitionPostProcessor) {
					internalPostProcessors.add(pp);
				}
			}
			else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
				orderedPostProcessorNames.add(ppName);
			}
			else {
				nonOrderedPostProcessorNames.add(ppName);
			}
		}

		// First, register the BeanPostProcessors that implement PriorityOrdered.
		sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
		registerBeanPostProcessors(beanFactory, priorityOrderedPostProcessors);

		// Next, register the BeanPostProcessors that implement Ordered.
		List<BeanPostProcessor> orderedPostProcessors = new ArrayList<>();
		for (String ppName : orderedPostProcessorNames) {
			BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
			orderedPostProcessors.add(pp);
			if (pp instanceof MergedBeanDefinitionPostProcessor) {
				internalPostProcessors.add(pp);
			}
		}
		sortPostProcessors(orderedPostProcessors, beanFactory);
		registerBeanPostProcessors(beanFactory, orderedPostProcessors);

		// Now, register all regular BeanPostProcessors.
		List<BeanPostProcessor> nonOrderedPostProcessors = new ArrayList<>();
		for (String ppName : nonOrderedPostProcessorNames) {
			BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
			nonOrderedPostProcessors.add(pp);
			if (pp instanceof MergedBeanDefinitionPostProcessor) {
				internalPostProcessors.add(pp);
			}
		}
		registerBeanPostProcessors(beanFactory, nonOrderedPostProcessors);

		// Finally, re-register all internal BeanPostProcessors.
		sortPostProcessors(internalPostProcessors, beanFactory);
		registerBeanPostProcessors(beanFactory, internalPostProcessors);

		// Re-register post-processor for detecting inner beans as ApplicationListeners,
		// moving it to the end of the processor chain (for picking up proxies etc).
		beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(applicationContext));
	}

	private static void sortPostProcessors(List<?> postProcessors, ConfigurableListableBeanFactory beanFactory) {
		Comparator<Object> comparatorToUse = null;
		if (beanFactory instanceof DefaultListableBeanFactory) {
			comparatorToUse = ((DefaultListableBeanFactory) beanFactory).getDependencyComparator();
		}
		if (comparatorToUse == null) {
			comparatorToUse = OrderComparator.INSTANCE;
		}
		postProcessors.sort(comparatorToUse);
	}

	/**
	 * Invoke the given BeanDefinitionRegistryPostProcessor beans.
	 */
	private static void invokeBeanDefinitionRegistryPostProcessors(
			Collection<? extends BeanDefinitionRegistryPostProcessor> postProcessors, BeanDefinitionRegistry registry) {

		for (BeanDefinitionRegistryPostProcessor postProcessor : postProcessors) {
			postProcessor.postProcessBeanDefinitionRegistry(registry);
		}
	}

	/**
	 * Invoke the given BeanFactoryPostProcessor beans.
	 */
	private static void invokeBeanFactoryPostProcessors(
			Collection<? extends BeanFactoryPostProcessor> postProcessors, ConfigurableListableBeanFactory beanFactory) {

		for (BeanFactoryPostProcessor postProcessor : postProcessors) {
			postProcessor.postProcessBeanFactory(beanFactory);
		}
	}

	/**
	 * Register the given BeanPostProcessor beans.
	 */
	private static void registerBeanPostProcessors(
			ConfigurableListableBeanFactory beanFactory, List<BeanPostProcessor> postProcessors) {

		for (BeanPostProcessor postProcessor : postProcessors) {
			beanFactory.addBeanPostProcessor(postProcessor);
		}
	}


	/**
	 * BeanPostProcessor that logs an info message when a bean is created during
	 * BeanPostProcessor instantiation, i.e. when a bean is not eligible for
	 * getting processed by all BeanPostProcessors.
	 */
	private static final class BeanPostProcessorChecker implements BeanPostProcessor {

		private static final Log logger = LogFactory.getLog(BeanPostProcessorChecker.class);

		private final ConfigurableListableBeanFactory beanFactory;

		private final int beanPostProcessorTargetCount;

		public BeanPostProcessorChecker(ConfigurableListableBeanFactory beanFactory, int beanPostProcessorTargetCount) {
			this.beanFactory = beanFactory;
			this.beanPostProcessorTargetCount = beanPostProcessorTargetCount;
		}

		@Override
		public Object postProcessBeforeInitialization(Object bean, String beanName) {
			return bean;
		}

		@Override
		public Object postProcessAfterInitialization(Object bean, String beanName) {
			if (!(bean instanceof BeanPostProcessor) && !isInfrastructureBean(beanName) &&
					this.beanFactory.getBeanPostProcessorCount() < this.beanPostProcessorTargetCount) {
				if (logger.isInfoEnabled()) {
					logger.info("Bean '" + beanName + "' of type [" + bean.getClass().getName() +
							"] is not eligible for getting processed by all BeanPostProcessors " +
							"(for example: not eligible for auto-proxying)");
				}
			}
			return bean;
		}

		private boolean isInfrastructureBean(@Nullable String beanName) {
			if (beanName != null && this.beanFactory.containsBeanDefinition(beanName)) {
				BeanDefinition bd = this.beanFactory.getBeanDefinition(beanName);
				return (bd.getRole() == RootBeanDefinition.ROLE_INFRASTRUCTURE);
			}
			return false;
		}
	}

}
