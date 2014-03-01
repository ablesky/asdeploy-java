package com.ablesky.asdeploy.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ablesky.asdeploy.service.IUserService;

@Controller
@RequestMapping("/test")
public class TestController {
	
	@Autowired
	private IUserService userService;

	@ResponseBody
	@RequestMapping("/test")
	public String test() {
		long ts = System.currentTimeMillis();
//		User user = new User("zyang", "1234", new Timestamp(ts), new Timestamp(ts));
//		System.out.println(userService.getUserById(1L));
//		System.out.println(userService.getUserByUsername("zyang"));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("username__endWith", "yan");
		System.out.println(userService.getUserListResult(0, 0, param));
		return "success";
	}
	
}
