package com.zhao.mybatis.mapper;

import com.zhao.mybatis.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author zhaojinliang
 * @version 1.0
 * @description TODO
 * @date 2020-03-08 14:39
 */
@Mapper
public interface UserMapper {

	@Select("select * from user")
	public List<User> queryUser();

}
