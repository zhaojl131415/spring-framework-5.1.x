package com.zhao.web;

import com.zhao.service.OrderService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.stereotype.Component;

/**
 * @author zhaojinliang
 * @version 1.0
 * @description 改变你需要改变的bean类所对应的BeanDefinition的一些属性
 * @date 2020-01-16 11:20
 *
 *
 * spring bean生命周期
 *
 * 1、实例化spring容器 --- 扫描类 --- 解析类（class）
 * 2、把class实例化出一个BeanDefinition对象，put到BeanDefinitionMap中
 * 3、调用bean工厂的后置处理器：BeanFactoryPostProcessor
 * 4、遍历BeanDefinitionMap，取出BeanDefinition对象
 * 5、validate：验证BeanDefinition对象是否单列，是否Lazy等
 * 6、通过BeanDefinition.getBeanClass推断构造方法，通过构造方法反射实例化对象
 * 7、缓存 注解信息 解析合并BeanDefinition
 * 8、提前暴露一个工厂？？？
 * 9、填充属性---自动注入
 * 10、执行部分aware
 * 11、执行部分aware且执行生命周期回调方法
 * 12、执行生命周期回调方法
 * 13、完成aop代理
 * 14、put单例池容器
 *
 */
//@Component
public class ZhaoBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		BeanDefinition beanDefinition = (BeanDefinition) beanFactory.getBeanDefinition("commodityService");
		beanDefinition.setDestroyMethodName(AbstractBeanDefinition.INFER_METHOD);
	}
}
