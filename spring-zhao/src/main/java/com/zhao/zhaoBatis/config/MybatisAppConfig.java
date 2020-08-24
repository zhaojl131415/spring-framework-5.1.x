package com.zhao.zhaoBatis.config;

import com.zhao.zhaoBatis.ZhaoImportBeanDefinitionRegistrar;
import com.zhao.zhaoBatis.annotation.ZhaoMapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author zhaojinliang
 * @version 1.0
 * @description TODO
 * @date 2020-04-15 20:30
 */
@Configuration
@ComponentScan("com.zhao.zhaoBatis")
@ZhaoMapperScan("com.zhao.zhaoBatis")
public class MybatisAppConfig {
}
