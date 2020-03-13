package com.zhao.config;

import com.zhao.service.X;
import com.zhao.service.Y;
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
@EnableAspectJAutoProxy
@ComponentScan("com.zhao")
@Configuration
public class AppConfig {

	@Bean
	public X x() {
		System.out.println("x init");
		return new X();
	}

	@Bean
	public Y y() {
		x();
		System.out.println("y init");
		return new Y();
	}
}

