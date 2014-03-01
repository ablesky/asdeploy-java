package com.ablesky.asdeploy.controller;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ablesky.asdeploy.pojo.User;
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
		User user = new User("zyang", "1234", new Timestamp(ts), new Timestamp(ts));
		userService.saveOrUpdateUser(user);
		System.out.println(userService.getUserById(1L));
		System.out.println(userService.getUserByUsername("zyang"));
		return "success";
	}
	
}
