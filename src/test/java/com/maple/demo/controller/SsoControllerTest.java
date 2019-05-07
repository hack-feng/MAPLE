package com.maple.demo.controller;

import com.maple.demo.utils.HttpUtil;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class SsoControllerTest {

	/**
	 * 测试并发登录
	 */
	@Test
	public void testLogin() {
		CountDownLatch c = new CountDownLatch(1);
		for (int i = 0; i < 1; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						c.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Map<String, Object> map = new HashMap<>();
					map.put("username", "Maple");
					map.put("password", "123456");

					String result = HttpUtil.post("http://127.0.0.1:8081/sso/login", map, 3000, 3000, "UTF-8");
					System.out.println(result);
				}
			}).start();
		}
		c.countDown();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
