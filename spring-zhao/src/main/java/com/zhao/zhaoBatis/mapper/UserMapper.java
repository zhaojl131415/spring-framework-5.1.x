package com.zhao.zhaoBatis.mapper;

import com.zhao.zhaoBatis.User;
import com.zhao.zhaoBatis.annotation.ZhaoMapper;
import com.zhao.zhaoBatis.annotation.ZhaoSelect;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author zhaojinliang
 * @version 1.0
 * @description TODO
 * @date 2020-03-08 14:39
 */
@ZhaoMapper
public interface UserMapper {

	@Select("select * from user")
	public List<User> queryUser();

}
