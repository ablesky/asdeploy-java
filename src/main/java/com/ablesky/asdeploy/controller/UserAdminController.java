package com.ablesky.asdeploy.controller;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ablesky.asdeploy.pojo.Role;
import com.ablesky.asdeploy.pojo.User;
import com.ablesky.asdeploy.pojo.UserRelRole;
import com.ablesky.asdeploy.service.IAuthorityService;
import com.ablesky.asdeploy.service.IUserService;
import com.ablesky.asdeploy.util.AuthUtil;

@Controller
@RequestMapping("/admin/user")
public class UserAdminController {
	
	@Autowired
	private IUserService userService;
	@Autowired
	private IAuthorityService authorityService;

	@RequestMapping("/list")
	public String list(Model model) {
		List<UserRelRole> superAdminRelList = authorityService.getUserRelRoleListResultByParam(0, 0, new ModelMap()
				.addAttribute("role_name", Role.NAME_SUPER_ADMIN)
		);
		ModelMap superAdminMap = new ModelMap();
		for(UserRelRole rel: superAdminRelList) {
			User user = rel.getUser();
			superAdminMap.put(user.getUsername(), user);
		}
		model.addAttribute("list", userService.getUserListResult(0, 0, Collections.<String, Object>emptyMap()));
		model.addAttribute("superAdminMap", superAdminMap);
		return "admin/user/list";
	}
	
	@RequestMapping(value="/switchSuperAdmin", method=RequestMethod.POST)
	public @ResponseBody ModelMap switchSuperAdmin(Long userId, Boolean isSuperAdmin) {
		ModelMap resultMap = new ModelMap();
		if(userId == null || isSuperAdmin == null) {
			return resultMap.addAttribute("success", false)
					.addAttribute("message", "参数有误!");
		}
		if(!AuthUtil.isSuperAdmin()) {
			return resultMap.addAttribute("success", false)
					.addAttribute("message", "没有权限!");
		}
		if(userId.equals(AuthUtil.getCurrentUser().getId())) {
			return resultMap.addAttribute("success", false)
					.addAttribute("message", "不允许超级管理员将自身将为普通用户!");
		}
		if(!isSuperAdmin) {
			authorityService.deleteUserRelRoleByUserIdAndRoleName(userId, Role.NAME_SUPER_ADMIN);
			return resultMap.addAttribute("success", true);
		}
		
		UserRelRole superAdminRel = authorityService.getUserRelRoleByParam(new ModelMap()
				.addAttribute("user_id", userId)
				.addAttribute("role_name", Role.NAME_SUPER_ADMIN));
		if(superAdminRel != null) {
			return resultMap.addAttribute("success", true);
		}
		if((superAdminRel = buildNewUserRelRole(userId, Role.NAME_SUPER_ADMIN)) == null){
			return resultMap.addAttribute("success", false).addAttribute("message", "用户或角色不存在!");
		}
		authorityService.saveOrUpdateUserRelRole(superAdminRel);
		return resultMap.addAttribute("success", true);
		
	}
	
	private UserRelRole buildNewUserRelRole(Long userId, String roleName) {
		User user = userService.getUserById(userId);
		if(user == null) {
			return null;
		}
		Role role = authorityService.getRoleByName(roleName);
		if(role == null) {
			return null;
		}
		return new UserRelRole(user, role);
	}
	
	@RequestMapping(value="/changePassword/{userId}", method=RequestMethod.GET)
	public String changePassword(@PathVariable("userId") Long userId, Model model) {
		model.addAttribute("user", userService.getUserById(userId));
		return "admin/user/changePassword";
	}
	
	/**
	 * 超级管理员修改用户密码
	 */
	@RequestMapping(value="/changePassword", method=RequestMethod.POST)
	public @ResponseBody ModelMap changePassword(Long userId, String newPassword) {
		ModelMap resultMap = new ModelMap();
		if(!AuthUtil.isSuperAdmin()) {
			return resultMap.addAttribute("success", false).addAttribute("message", "没有权限!");
		}
		if(userId == null || userId <= 0 || StringUtils.isBlank(newPassword)) {
			return resultMap.addAttribute("success", false).addAttribute("message", "参数有误!");
		}
		User user = userService.getUserById(userId);
		if(user == null) {
			return resultMap.addAttribute("success", false).addAttribute("message", "用户不存在!");
		}
		user.setPassword(AuthUtil.hashPassword(user.getUsername(), newPassword));
		userService.saveOrUpdateUser(user);
		return resultMap.addAttribute("success", true).addAttribute("message", "修改成功!"); 
	}
	
}
