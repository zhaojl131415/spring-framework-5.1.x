package com.zhao.test;

import com.zhao.config.AppConfig;
import com.zhao.factoryBean.Z;
import com.zhao.service.*;
import com.zhao.web.ZhaoBeanDefinitionRegistryPostProcessor;
import com.zhao.web.ZhaoBeanFactoryPostProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.ChildBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

/**
 * @author zhaojinliang
 * @version 1.0
 * @description TODO
 * @date 2020-01-07 17:37
 */public class Test {
	public static void main(String[] args) throws BeansException {
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext();
		ac.register(AppConfig.class);
		ac.register(ZhaoService.class);
		ac.addBeanFactoryPostProcessor(new ZhaoBeanFactoryPostProcessor());
		ac.addBeanFactoryPostProcessor(new ZhaoBeanDefinitionRegistryPostProcessor());
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

		System.out.println(ac.getBean("zhaoService"));
//		System.out.println(ac.getBean("bService"));
//		System.out.println(ac.getBean("cService"));


//		System.out.println(ac.getBean(UserService.class));

		// spring 上下文初始化，扫描并实例化spring bean
//		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
//		System.out.println(ac.getBean("zhaoFactoryBean"));
//		System.out.println(ac.getBean("zhaoFactoryBean"));
//		System.out.println(ac.getBean("zhaoFactoryBean"));
//		System.out.println(ac.getBean("&zhaoFactoryBean"));

//		ac.getBean(CommodityService.class).testOrderScope();
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

//		System.out.println(ac.getBean(OrderService.class));
//		System.out.println(ac.getBean(UserService.class));
//		System.out.println(ac.getBean(CommodityService.class));


//		((UserService) ac.getBean("userService")).testAop();

//		ac.getBean(UserService.class).testAop();
	}
}
