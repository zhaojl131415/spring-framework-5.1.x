package com.zhao.config;

import com.zhao.zhaoBatis.ZhaoMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.type.filter.AnnotationTypeFilter;



/**
 * @author zhaojinliang
 * @version 1.0
 * @description https://www.cnblogs.com/CodeBear/p/10336704.html
 * @date 2020-01-07 17:28
 */
// https://www.cnblogs.com/CodeBear/p/10336704.html
//@Configurable
@ComponentScan(basePackages ="com.zhao"
//		,useDefaultFilters = false
//		,includeFilters = @Filter(ZhaoMapper.class)
)
@MapperScan
@EnableAspectJAutoProxy
public class AppConfig {
}
