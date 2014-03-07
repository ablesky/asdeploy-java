package com.ablesky.asdeploy.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ablesky.asdeploy.pojo.ConflictDetail;
import com.ablesky.asdeploy.pojo.DeployItem;
import com.ablesky.asdeploy.pojo.DeployRecord;
import com.ablesky.asdeploy.service.IDeployService;
import com.ablesky.asdeploy.service.IProjectService;
import com.ablesky.asdeploy.util.CommonConstant;
import com.ablesky.asdeploy.util.DeployUtil;

@Controller
@RequestMapping("/deployRecord")
public class DeployRecordController {
	
	@Autowired
	private IProjectService projectService;
	@Autowired
	private IDeployService deployService;
	
	@RequestMapping("/list")
	public String list(
			@RequestParam(defaultValue=CommonConstant.DEFAUTL_START_STR)
			Integer start,
			@RequestParam(defaultValue=CommonConstant.DEFAULT_LIMIT_STR)
			Integer limit,
			@RequestParam(required=false)
			String username,
			@RequestParam(defaultValue="0")
			Long projectId,
			@RequestParam(required=false)
			String deployType,
			@RequestParam(required=false)
			String version,
			Model model) {
		if(start == null) {
			start = CommonConstant.DEFAUTL_START;
		}
		if(limit == null) {
			limit = CommonConstant.DEFAULT_LIMIT;
		}
		Map<String, Object> param = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(username)) {
			param.put("user_username__contain", username);
		}
		if(projectId != null && projectId > 0) {
			param.put("project_id", projectId);
		}
		if(StringUtils.isNotBlank(deployType)) {
			param.put("deployItem_deployType", deployType);
		}
		if(StringUtils.isNotBlank(version)) {
			param.put("deployItem_version", version);
		}
		param.put(CommonConstant.ORDER_BY, "id desc");
		model.addAttribute("projectList", projectService.getProjectListResult(0, 0, Collections.<String, Object>emptyMap()));
		model.addAttribute("page", deployService.getDeployRecordPaginateResult(start, limit, param));
		return "deployRecord/list";
	}
	
	@RequestMapping("/detail/{id}")
	public String detail(@PathVariable("id") Long id, Model model) {
		DeployRecord deployRecord = deployService.getDeployRecordById(id);
		DeployItem deployItem = deployRecord.getDeployItem();
		List<String> filePathList = Collections.emptyList();
		String readme = "";
		if(deployItem != null) {
			String targetFolderPath = FilenameUtils.concat(deployItem.getFolderPath(), FilenameUtils.getBaseName(deployItem.getFileName()));
			filePathList = DeployUtil.getDeployItemFilePathList(targetFolderPath);
			readme = DeployUtil.loadReadmeContent(targetFolderPath);
		}
		List<ConflictDetail> conflictDetailList = deployRecord.getIsConflictWithOthers()
				? deployService.getConflictDetailListResultByParam(new ModelMap().addAttribute("deployRecordId", id))
				: Collections.<ConflictDetail>emptyList();
		
		model.addAttribute("deployRecord", deployRecord)
			.addAttribute("filePathList", filePathList)
			.addAttribute("readme", readme)
			.addAttribute("conflictDetailList", conflictDetailList);
		return "deployRecord/detail";
	}
	

}
