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
@Random
public class RandomStrategy implements LoadBalanced {

	@Override
	public String select() {
		return "random";
	}
}