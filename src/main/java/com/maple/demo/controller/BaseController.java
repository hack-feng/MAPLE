package com.maple.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.maple.demo.bean.User;
import com.maple.demo.config.WebMvcConfig;
import com.maple.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Controller
public class BaseController<K, V> {


	@Autowired
	private UserService userService;

    /**
     * 定义返回类型
     */
    public enum Type {
        success,
        error
    }

    public String message(int status, String content) {
        return "{\"status\":\"" + status + "\",\"content\":\"" + content + "\"}";
    }

    public Map<String, Object> messageToMap(int status, Object content) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("status", status);
        map.put("content", content);
        return map;
    }

    public User getUser(HttpSession session){
        String userName = session.getAttribute(WebMvcConfig.LOGIN_USER).toString();
        User user = userService.getOne(new QueryWrapper<User>().eq("user_name", userName));
        return user;
    }

}
