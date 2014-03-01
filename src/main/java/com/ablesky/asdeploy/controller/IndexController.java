package com.ablesky.asdeploy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class IndexController {

	@RequestMapping("/main")
	public String main(Model model) {
		model.addAttribute("username", "zyang");
		return "main";
	}
	
	/**
	 * 登录页面
	 */
	@RequestMapping(value="login", method=RequestMethod.GET)
	public String login() {
		return "login";
	}
	
	/**
	 * 登录操作
	 */
	@RequestMapping(value="login", method=RequestMethod.POST)
	public String login(String username, String password) {
		return "redirect:/main";
	}
}
