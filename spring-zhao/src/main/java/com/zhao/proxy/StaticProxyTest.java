package com.zhao.proxy;

/**
 * @author zhaojinliang
 * @version 1.0
 * @description TODO
 * @date 2020-03-08 15:24
 */

public class StaticProxyTest {

}

class StaticProxyTest1 {
	public static void main(String[] args) {
		IndexService zService = new IndexServiceImplProxy();
		zService.test();
	}
}

class StaticProxyTest2 {
	public static void main(String[] args) {
		IndexService zService = IndexServiceStaticFactory.getInstance();
		zService.test();
	}
}

interface IndexService {
	void test();
}

class IndexServiceImpl implements IndexService {
	@Override
	public void test() {
		System.out.println("test");
	}
}

class IndexServiceImplProxy extends IndexServiceImpl {
	@Override
	public void test() {
		System.out.println("proxy start");
		super.test();
		System.out.println("proxy end");
	}
}

class IndexServiceImplProxy2 implements IndexService {
	private IndexService target;

	public IndexServiceImplProxy2(IndexService indexService) {
		this.target = indexService;
	}

	@Override
	public void test() {
		System.out.println("factory produce proxy start");
		target.test();
		System.out.println("factory produce proxy end");
	}
}

class IndexServiceStaticFactory {
	// 调用此工厂方法获得代理对象。
	public static IndexService getInstance(){
		System.out.println("factory start");
		return new IndexServiceImplProxy2(new IndexServiceImpl());
	}
}

