package com.zhao.test;

import com.zhao.config.AppConfig;
import com.zhao.importAnnotation.ZhaoDeferredImportSelectorService;
import com.zhao.importAnnotation.ZhaoImportService;
import com.zhao.importAnnotation.ZhaoImportService2;
import com.zhao.loadBalanced.RuleService;
import com.zhao.service.*;
import com.zhao.web.ZhaoBeanFactoryPostProcessor;
import org.springframework.beans.BeansException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author zhaojinliang
 * @version 1.0
 * @description TODO
 * @date 2020-01-07 17:37
 */
public class Test {
	public static void main(String[] args) throws BeansException {
//		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
//		AnnotationConfigWebApplicationContext
//		System.out.println(ac.getBean("orderService"));
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext();
		ac.register(AppConfig.class);
//		ac.register(ZhaoService.class);
		ac.addBeanFactoryPostProcessor(new ZhaoBeanFactoryPostProcessor());
//		ac.addBeanFactoryPostProcessor(new ZhaoBeanDefinitionRegistryPostProcessor());
//		RootBeanDefinition rbd = new RootBeanDefinition();
//		rbd.setScope(BeanDefinition.SCOPE_SINGLETON);
//		rbd.setLazyInit(false);
//		rbd.setAutowireCandidate(true);
//		rbd.setPrimary(false);
//		rbd.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_NO);
//		rbd.setAbstract(true);
//		ac.registerBeanDefinition("rbd", rbd);
//
//		ChildBeanDefinition bdA = new ChildBeanDefinition("rbd");
//		bdA.setBeanClass(AService.class);
//		ac.registerBeanDefinition("aService", bdA);
//
//		ChildBeanDefinition bdB = new ChildBeanDefinition("rbd");
//		bdB.setBeanClass(BService.class);
//		ac.registerBeanDefinition("bService", bdB);
//
//		ChildBeanDefinition bdC = new ChildBeanDefinition("aService");
//		bdC.setBeanClass(CService.class);
//		ac.registerBeanDefinition("cService", bdC);

		ac.refresh();

//		System.out.println(ac.getBean("zhaoService"));
//		System.out.println(ac.getBean("bService"));
//		System.out.println(ac.getBean("cService"));


//		System.out.println(ac.getBean(UserService.class));

		// spring 上下文初始化，扫描并实例化spring bean
//		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
//		System.out.println(ac.getBean("zhaoFactoryBean"));
//		System.out.println(ac.getBean("zhaoFactoryBean"));
//		System.out.println(ac.getBean("zhaoFactoryBean"));
//		System.out.println(ac.getBean("zhaoFactoryBean"));
//		System.out.println(ac.getBean("&zhaoFactoryBean"));
//		System.out.println(ac.getBean("&zhaoFactoryBean"));
//
//		System.out.println(ac.getBean(CommodityService.class));
//		ac.getBean(CommodityService.class).close();
//		ac.getBean(CommodityService.class).testOrderScope();
		/**
		 * 相当于上一行代码
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext();
		ac.register(AppConfig.class);
		ac.getBeanFactory().registerSingleton("z", new Z());
		ac.refresh();
		 */

		/**
		 * 如果需要关闭spring循环依赖
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext();

		// 方法1
		ac.setAllowCircularReferences(false);
		// 方法2
		((GenericApplicationContext)ac.getBeanFactory()).setAllowCircularReferences(false);

		ac.register(AppConfig.class);
		ac.refresh();
		 */
		// 同一接口有多个实现类, 如果有指定autowire模式为byName/byType方式, 则优先通过set方法注入, 其次@Resource/@AutoWired注入
		OrderService orderService = ac.getBean(OrderService.class);
		orderService.test();
		// 自己注入自己与@Bean注入: 优先@Bean注入
		AaService a = (AaService) ac.getBean("aaService");
		a.test();
//		System.out.println(ac.getBean(OrderService.class));
//		System.out.println(ac.getBean(UserService.class));
//		System.out.println(ac.getBean(CommodityService.class));


//		((UserService) ac.getBean("userService")).testAop();

//		ac.getBean(UserService.class).testAop();


		// 自定义注解借助@Qualifier注解通过策略模式实现负载均衡
		RuleService ruleService = ac.getBean(RuleService.class);
		ruleService.test();

		ac.publishEvent("zhao publish event!");

		// @Import
		ZhaoImportService zhaoImportService = ac.getBean(ZhaoImportService.class);
		System.out.println(zhaoImportService);
		zhaoImportService.test();

		ZhaoImportService2 zhaoImportService2 = ac.getBean(ZhaoImportService2.class);
		System.out.println(zhaoImportService2);
		zhaoImportService2.test();

		ZhaoDeferredImportSelectorService zhaoDeferredImportSelectorService = ac.getBean(ZhaoDeferredImportSelectorService.class);
		System.out.println(zhaoDeferredImportSelectorService);
		zhaoDeferredImportSelectorService.test();
	}
}
