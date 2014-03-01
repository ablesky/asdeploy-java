package com.ablesky.asdeploy.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.ablesky.asdeploy.service.IProjectService;

@Controller
@RequestMapping("/project")
public class ProjectController {
	
	@Autowired
	private IProjectService projectService;

	@RequestMapping("/list")
	public String list(Model model) {
		model.addAttribute("list", projectService.getProjectListResult(0, 0, Collections.<String, Object>emptyMap()));
		return "project/list";
	}
	
	@RequestMapping(value="/edit", method=RequestMethod.GET)
	public String edit() {
		return "project/edit";
	}
	
	@RequestMapping(value="/edit", method=RequestMethod.POST)
	public String edit(String name, String warName) {
		return null;
	}
	
	
	@RequestMapping(value="/delete/{id}", method=RequestMethod.POST)
	public @ResponseBody Map<String, Object> delete(@PathVariable("id") Long id) {
		projectService.deleteProjectById(id);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("success", true);
		resultMap.put("message", "删除成功!");
		return resultMap;
	}
	
	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody Map<String, Object> runtimeExceptionHandler(RuntimeException runtimeException) {
		runtimeException.printStackTrace();
		Map<String, Object> exceptionMap = new HashMap<String, Object>();
		exceptionMap.put("success", false);
		exceptionMap.put("message", "操作失败!");
		return exceptionMap;
	}
}
