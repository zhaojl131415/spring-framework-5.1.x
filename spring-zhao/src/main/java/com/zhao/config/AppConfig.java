package com.zhao.config;

import com.zhao.cglib.ZhaoAopBeanPostProcessor;
import com.zhao.service.*;
import com.zhao.zhaoBatis.ZhaoImportBeanDefinitionRegistrar;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.*;


/**
 * @author zhaojinliang
 * @version 1.0
 * @description https://www.cnblogs.com/CodeBear/p/10336704.html
 * @date 2020-01-07 17:28
 */
// https://www.cnblogs.com/CodeBear/p/10336704.html
//@Configurable
//@ComponentScan(basePackages ="com.zhao"
//		,useDefaultFilters = false
//		,includeFilters = @Filter(ZhaoMapper.class)
//)
//@MapperScan

/**
 * 开启aop注解
 */
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan("com.zhao")
@Configuration
//@Import(ZhaoAopBeanPostProcessor.class)
public class AppConfig {
//
//	@Bean
//	public X x() {
//		System.out.println("x init");
//		return new X();
//	}
//
//	@Bean
//	public Y y() {
//		x();
//		System.out.println("y init");
//		return new Y();
//	}

	@Bean(autowire = Autowire.BY_NAME)
	public OrderService orderService() {
		return new OrderService();
	}
}

