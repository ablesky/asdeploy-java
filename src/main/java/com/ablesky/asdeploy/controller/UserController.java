package com.ablesky.asdeploy.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ablesky.asdeploy.pojo.User;
import com.ablesky.asdeploy.service.IUserService;
import com.ablesky.asdeploy.util.AuthUtil;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private IUserService userService;

	@RequestMapping("/detail")
	public String detail() {
		return "user/detail";
	}
	
	/**
	 * 用户修改自己的密码
	 */
	@RequestMapping(value="/changePassword", method=RequestMethod.GET)
	public String changePassword(Model model) {
		model.addAttribute("user", AuthUtil.getCurrentUser());
		return "user/changePassword";
	}
	
	@RequestMapping(value="/changePassword", method=RequestMethod.POST)
	public @ResponseBody ModelMap changePassword(String oldPassword, String newPassword) {
		ModelMap resultMap = new ModelMap();
		if(StringUtils.isBlank(oldPassword) || StringUtils.isBlank(newPassword)) {
			return resultMap.addAttribute("success", false).addAttribute("message", "原密码和新密码不能为空!");
		}
		User user = userService.getUserById(AuthUtil.getCurrentUser().getId());
		if(!user.getPassword().equals(AuthUtil.hashPassword(user.getUsername(), oldPassword))) {
			return resultMap.addAttribute("success", false).addAttribute("message", "原密码不正确!");
		}
		user.setPassword(AuthUtil.hashPassword(user.getUsername(), newPassword));
		userService.saveOrUpdateUser(user);
		return resultMap.addAttribute("success", true);
	}
}
