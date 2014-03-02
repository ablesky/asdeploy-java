package com.ablesky.asdeploy.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ablesky.asdeploy.pojo.Project;
import com.ablesky.asdeploy.service.IProjectService;

@Controller
@RequestMapping("/deploy")
public class DeployController {
	
	@Autowired
	private IProjectService projectService;
	
	@RequestMapping("/initOption")
	public String initOption(Model model) {
		List<Project> projectList = projectService.getProjectListResult(0, 0, Collections.<String, Object>emptyMap());
		model.addAttribute("projectList", projectList);
		return "deploy/initOption";
	}
}
