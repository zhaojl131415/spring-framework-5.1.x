package com.zhao.config;

import com.zhao.importAnnotation.ZhaoDeferredImportSelector;
import com.zhao.importAnnotation.ZhaoImportSelector;
import com.zhao.importAnnotation.ZhaoImportService2;
import com.zhao.service.*;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.*;

import java.lang.reflect.Method;


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
@Import({ZhaoImportSelector.class, ZhaoImportService2.class, ZhaoDeferredImportSelector.class})
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

	@Bean
	public AaService aaService1() {
		aaService2();
		return new AaService();
	}

	@Bean(autowireCandidate = false)
	public AaService aaService2() {
		return new AaService();
	}

	// 将通过指定的BeanNames找到符合要求的Bean, 为这些bean生成代理, 并指定对应的代理逻辑
	@Bean
	public BeanNameAutoProxyCreator beanNameAutoProxyCreator() {
		BeanNameAutoProxyCreator proxyCreator = new BeanNameAutoProxyCreator();
		proxyCreator.setBeanNames("zhaoService", "userSer*");
		proxyCreator.setInterceptorNames("zhaoThrowsAdvice");
		return proxyCreator;
	}

//	// ---------------------------DefaultAdvisor start---------------------------
//	@Import(DefaultAdvisorAutoProxyCreator.class)
//
//	@Bean
//	public DefaultPointcutAdvisor defaultPointcutAdvisor() {
//		NameMatchMethodPointcut pointcut= new NameMatchMethodPointcut();
//		pointcut.setMappedName("test");
//
//		DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
//		advisor.setPointcut(pointcut);
//		advisor.setAdvice(new MethodBeforeAdvice() {
//			@Override
//			public void before(Method method, Object[] args, Object target) throws Throwable {
//				System.out.println("DefaultPointcutAdvisor advice pointcut test before....");
//			}
//		});
//		return advisor;
//	}
//	// ---------------------------DefaultAdvisor end---------------------------
}

