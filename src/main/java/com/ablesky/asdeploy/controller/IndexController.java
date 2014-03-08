package com.ablesky.asdeploy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ablesky.asdeploy.service.IDeployService;
import com.ablesky.asdeploy.util.AuthUtil;

@Controller
public class IndexController {

	@Autowired
	private IDeployService deployService;
	
	@RequestMapping("/main")
	public String main(Model model) {
		model.addAttribute("deployLock", deployService.checkCurrentLock());
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
	 * 登录操作(实际的登录操作在FormAuthenticationFilter中进行)
	 * 只有登录失败后，才会进入到此方法中
	 */
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public String loginFailed(String username, String password, Model model) {
		model.addAttribute("errorMessage", "用户名或密码错误，请重试!");
		return "login";
	}
	
	@RequestMapping("/unauthorized")
	public String unauthorized() {
		return "unauthorized";
	}
}
