package com.zhao.zhaoBatis;

/**
 * @author zhaojinliang
 * @version 1.0
 * @description TODO
 * @date 2020-03-08 14:39
 */
public interface OrderMapper {


	@ZhaoSelect("select * from order")
	public void queryOrder();
}
