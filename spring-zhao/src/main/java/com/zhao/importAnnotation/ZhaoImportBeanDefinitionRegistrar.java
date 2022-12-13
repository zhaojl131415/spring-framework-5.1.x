package com.zhao.importAnnotation;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * TODO
 *
 * @author zhaojinliang
 * @version 1.0.0
 * @since 2022/12/12
 */
public class ZhaoImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
	/**
	 * 根据导入的{@code @Configuration}类的给定注释元数据，根据需要注册Bean定义。
	 * Register bean definitions as necessary based on the given annotation metadata of
	 * the importing {@code @Configuration} class.
	 *
	 * <p>请注意{@link BeanDefinitionRegistryPostProcessor}类型可以<EM>不</EM>在这里注册，
	 * 由于与{@code @Configuration}类处理生命周期的约束。
	 * <p>Note that {@link BeanDefinitionRegistryPostProcessor} types may <em>not</em> be
	 * registered here, due to lifecycle constraints related to {@code @Configuration}
	 * class processing.
	 *
	 * @param importingClassMetadata annotation metadata of the importing class
	 * @param registry               current bean definition registry
	 */
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

	}
}
