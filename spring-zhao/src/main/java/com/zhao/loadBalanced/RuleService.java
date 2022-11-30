package com.zhao.loadBalanced;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @author zhaojinliang
 * @version 1.0.0
 * @since 2022/11/30
 */
@Component
public class RuleService {

	@Autowired
	@Random
	private LoadBalanced loadBalance;

	public void test() {
		System.out.println(loadBalance.select());
	}
}
