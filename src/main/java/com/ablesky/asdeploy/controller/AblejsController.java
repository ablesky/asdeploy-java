package com.ablesky.asdeploy.controller;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.aspectj.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ablesky.asdeploy.service.IProjectService;
import com.ablesky.asdeploy.util.cmd.AblejsCmd;

@Controller
@RequestMapping("/ablejs")
public class AblejsController {
	
	@Autowired
	private IProjectService projectService;

	@RequestMapping("/index")
	public String index(Model model) {
		model.addAttribute("projectList", projectService.getProjectListResult());
		return "ablejs/index";
	}
	
	@RequestMapping("/query")
	public @ResponseBody String query(
			@RequestParam(defaultValue="as-web")
			String projectName, 
			String queryType, 
			String queryValue) {
		String result = "";
		AblejsCmd ablejs = new AblejsCmd(projectName);
		InputStream in = null;
		try {
			if("relativePath".equals(queryType)) {
				in = ablejs.path().param(queryValue).exec().getInputStream();
				result = new String(FileUtil.readAsByteArray(in));
			} else if("hashcode".equals(queryType)) {
				in = ablejs.fingerprint().param(queryValue).exec().getInputStream();
				result = new String(FileUtil.readAsByteArray(in));
			}
		} catch (IOException e) {
			e.printStackTrace();
			result = "{}";
		} finally {
			IOUtils.closeQuietly(in);
		}
		if("undefined".equals(result) || "null".equals(result)) {
			result = "{}";
		}
		return result;
	}
	
}
