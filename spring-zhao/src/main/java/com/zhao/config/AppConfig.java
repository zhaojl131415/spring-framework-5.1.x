package com.zhao.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author zhaojinliang
 * @version 1.0
 * @description https://www.cnblogs.com/CodeBear/p/10336704.html
 * @date 2020-01-07 17:28
 */
// https://www.cnblogs.com/CodeBear/p/10336704.html
//@Configurable
@ComponentScan("com.zhao")
@EnableAspectJAutoProxy
public class AppConfig {
}
