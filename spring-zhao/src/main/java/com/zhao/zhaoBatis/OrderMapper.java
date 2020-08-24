package com.zhao.zhaoBatis;

import com.zhao.zhaoBatis.annotation.ZhaoMapper;
import com.zhao.zhaoBatis.annotation.ZhaoSelect;

/**
 * @author zhaojinliang
 * @version 1.0
 * @description TODO
 * @date 2020-03-08 14:39
 */
@ZhaoMapper
public interface OrderMapper {


	@ZhaoSelect("select * from order")
	public void queryOrder();
}
