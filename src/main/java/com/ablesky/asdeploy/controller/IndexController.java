package com.ablesky.asdeploy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

	@RequestMapping("/main")
	public String toMain(Model model) {
		model.addAttribute("username", "zyang");
		return "main";
	}
}
