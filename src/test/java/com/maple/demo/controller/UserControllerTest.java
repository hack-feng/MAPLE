package com.maple.demo.controller;

import com.maple.demo.utils.HttpUtil;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class UserControllerTest {

	/**
	 * 测试登录拦截器
	 */
	@Test
	public void testLogin() {

		String param = null;
		switch (param) {
			case "null":
				System.out.println("null");
				break;
			default:
				System.out.println("default");
		}
//		Map map = new HashMap();
//		String result = HttpUtil.post("http://127.0.0.1:8044/user/test", map, 3000, 3000, "UTF-8");
	}

}
