package com.zhao.listener;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 注解@EventListener
 *
 * @author zhaojinliang
 * @version 1.0.0
 * @since 2022/12/10
 */
@Component
@SuppressWarnings("rawtypes")
public class ListenerService {
	@EventListener
	public void hello(ApplicationEvent event) {
		PayloadApplicationEvent applicationEvent = (PayloadApplicationEvent) event;
		System.out.println("@EventListener 监听到了事件: " + applicationEvent.getPayload());
	}
}
