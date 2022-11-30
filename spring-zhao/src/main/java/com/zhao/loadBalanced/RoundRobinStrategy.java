package com.zhao.loadBalanced;

import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @author zhaojinliang
 * @version 1.0.0
 * @since 2022/11/30
 */
@Component
@RoundRobin
public class RoundRobinStrategy implements LoadBalanced {

	@Override
	public String select() {
		return "roundRobin";
	}
}