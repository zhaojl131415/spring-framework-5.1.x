package com.zhao.zhaoBatis.annotation;


import com.zhao.zhaoBatis.ZhaoImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

//@Import(ZhaoImportBeanDefinitionRegistrar.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ZhaoMapperScan {
	String value() default "";
}
