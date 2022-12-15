package com.zhao.mybatis.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

/**
 * TODO
 *
 * @author zhaojinliang
 * @version 1.0.0
 * @since 2022/12/14
 */
public class MybatisConfig {
	@Value("${jdbc.driver}")
	private String driver;
	@Value("${jdbc.url}")
	private String url;
	@Value("${jdbc.username}")
	private String username;
	@Value("${jdbc.password}")
	private String password;

	@Bean
	public DataSource dataSource(){

		// 1、new 第三方bean/对象
		DruidDataSource dataSource = new DruidDataSource();

		// 2、给第三方bean/对象属性赋值
		dataSource.setDriverClassName(driver);
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);

		// 3、把第三方bean/对象返回给该方法
		return dataSource;
	}

	/**
	 *  该sqlSessionFactory方法new出来的SqlSessionFactoryBean对象相当于整合mybatis的核心配置文件中的上面那些信息
	 */
	@Bean
	public SqlSessionFactoryBean sqlSessionFactory(){  // 传参的目的就是处理引用型依赖关系拿到DataSource对象
		// 1、new SqlSessionFactoryBean 对象
		SqlSessionFactoryBean sqfb =new SqlSessionFactoryBean();
		// 2、整合mybatis的核心配置信息
		sqfb.setTypeAliasesPackage("com.zhao.mybatis.domain");
		sqfb.setDataSource(dataSource()); // 引用型依赖关系为mybatis核心配置文件设置jdbc连接信息

		return sqfb;
	}



    /*
<mappers>
        <!-- 加载sql映射文件 -->
        <package name="com.itheima.dao"></package>
    </mappers>
     */

	/**
	 *  该mapperScannerConfigurer方法new出来的MapperScannerConfigurer对象相当于整合mybatis的核心配置文件中的上面那些信息
	 */
	@Bean
	public MapperScannerConfigurer mapperScannerConfigurer(){
		// 1、new MapperScannerConfigurer对象
		MapperScannerConfigurer mp =new MapperScannerConfigurer();
		// 2、整合mybatis的核心配置信息
		mp.setBasePackage("com.zhao.mybatis.mapper");
// 因为这里我们是用代理接口注解形式做的处理数据库的，也就是说没有用UserMapper.xml形式，所以直
//接加载到dao层就行了，如果有xml核心配置文件的话，就加载到核心配置文件
		return mp;
	}
}
