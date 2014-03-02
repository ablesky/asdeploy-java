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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.ablesky.asdeploy.pojo.Project;
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
	
	@RequestMapping(value="/edit/{id}", method=RequestMethod.GET)
	public String edit(@PathVariable Long id, Model model) {
		if(id != null && id > 0){
			model.addAttribute("project", projectService.getProjectById(id));
		}
		return "project/edit";
	}
	
	@RequestMapping(value="/edit", method=RequestMethod.GET)
	public String edit(Model model) {
		return edit(0L, model);
	}
	
	@RequestMapping(value="/edit", method=RequestMethod.POST)
	public @ResponseBody Map<String, Object> edit(
			@RequestParam(defaultValue="0") 
			Long id,
			String name,
			String warName) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Project project = null;
		if(id != null && id > 0) {
			project = projectService.getProjectById(id);
			if(project == null) {
				resultMap.put("success", false);
				resultMap.put("message", "项目不存在!");
				return resultMap;
			}
		} else {
			project = new Project();
		}
		project.setName(name);
		project.setWarName(warName);
		projectService.saveOrUpdateProject(project);
		resultMap.put("success", true);
		return resultMap;
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
