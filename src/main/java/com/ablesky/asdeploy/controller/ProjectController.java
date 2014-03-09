package com.ablesky.asdeploy.controller;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
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
		ModelMap resultMap = new ModelMap();
		if(StringUtils.isBlank(name) || StringUtils.isBlank(warName)) {
			return resultMap.addAttribute("success", false).addAttribute("message", "项目名称和包名称不能为空!");
		}
		Project project = null;
		if(id != null && id > 0) {
			project = projectService.getProjectById(id);
			if(project == null) {
				return resultMap.addAttribute("success", false)
						.addAttribute("message", "项目不存在!");
			}
		} else {
			project = new Project();
		}
		project.setName(name);
		project.setWarName(warName);
		projectService.saveOrUpdateProject(project);
		return resultMap.addAttribute("success", true);
	}
	
	
	@RequestMapping(value="/delete/{id}", method=RequestMethod.POST)
	public @ResponseBody Map<String, Object> delete(@PathVariable("id") Long id) {
		projectService.deleteProjectById(id);
		ModelMap resultMap = new ModelMap();
		return resultMap
				.addAttribute("success", true)
				.addAttribute("message", "删除成功!");
	}
	
	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody Map<String, Object> runtimeExceptionHandler(RuntimeException runtimeException) {
		runtimeException.printStackTrace();
		return new ModelMap()
				.addAttribute("success", false)
				.addAttribute("message", "操作失败!");
	}
}
