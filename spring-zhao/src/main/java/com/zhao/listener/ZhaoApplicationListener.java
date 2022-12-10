package com.zhao.listener;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.stereotype.Component;

/**
 * 事件监听器
 *
 * @author zhaojinliang
 * @version 1.0.0
 * @since 2022/12/10
 */
@Component
@SuppressWarnings("rawtypes")
public class ZhaoApplicationListener implements ApplicationListener {

	/**
	 * Handle an application event.
	 *
	 * @param event the event to respond to
	 */
	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		PayloadApplicationEvent applicationEvent = (PayloadApplicationEvent) event;
		System.out.println("ZhaoApplicationListener 监听到了事件: " + applicationEvent.getPayload());
	}
}
