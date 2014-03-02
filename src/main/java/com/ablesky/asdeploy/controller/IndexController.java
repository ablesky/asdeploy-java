package com.ablesky.asdeploy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ablesky.asdeploy.util.AuthUtil;

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
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String login() {
		return "login";
	}
	
	/**
	 * 登录操作
	 */
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public String login(String username, String password, Model model) {
		try {
			AuthUtil.login(username, password);
		} catch (Exception e) {
			model.addAttribute("errorMessage", "用户名或密码错误，请重试！");
			return "login";
		}
		return "redirect:/main";
	}
	
	@RequestMapping("/logout")
	public String logout() {
		AuthUtil.logout();
		return "redirect:/login";
	}
	
	@RequestMapping("/unauthorized")
	public String unauthorized() {
		return "unauthorized";
	}
}
