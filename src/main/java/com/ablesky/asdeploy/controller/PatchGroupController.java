package com.ablesky.asdeploy.controller;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ablesky.asdeploy.pojo.PatchGroup;
import com.ablesky.asdeploy.pojo.Project;
import com.ablesky.asdeploy.pojo.User;
import com.ablesky.asdeploy.service.IPatchGroupService;
import com.ablesky.asdeploy.service.IProjectService;
import com.ablesky.asdeploy.service.IUserService;
import com.ablesky.asdeploy.util.AuthUtil;
import com.ablesky.asdeploy.util.CommonConstant;

@Controller
@RequestMapping("/patchGroup")
public class PatchGroupController {
	
	@Autowired
	private IPatchGroupService patchGroupService;
	@Autowired
	private IProjectService projectService;
	@Autowired
	private IUserService userService;

	@RequestMapping("/list")
	public String list(
			@RequestParam(defaultValue=CommonConstant.DEFAUTL_START_STR)
			Integer start,
			@RequestParam(defaultValue=CommonConstant.DEFAULT_LIMIT_STR)
			Integer limit,
			@RequestParam(required=false)
			String creatorName,
			@RequestParam(required=false)
			String patchGroupName,
			@RequestParam(defaultValue="0")
			Long projectId,
			@RequestParam(required=false)
			String status,
			Model model) {
		if(start == null) {
			start = CommonConstant.DEFAUTL_START;
		}
		if(limit == null) {
			limit = CommonConstant.DEFAULT_LIMIT;
		}
		Map<String, Object> param = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(creatorName)) {
			param.put("creator_username__contain", creatorName);
		}
		if(StringUtils.isNotBlank(patchGroupName)) {
			param.put("name__contain", patchGroupName);
		}
		if(projectId != null && projectId > 0) {
			param.put("project_id", projectId);
		}
		if(StringUtils.isNotBlank(status)) {
			param.put("status", status);
		}
		model.addAttribute("projectList", projectService.getProjectListResult(0, 0, Collections.<String, Object>emptyMap()));
		model.addAttribute("page", patchGroupService.getPatchGroupPaginateResult(start, limit, param));
		return "patchGroup/list";
	}
	
	@RequestMapping("/detail")
	public String toDetail() {
		return "patchGroup/detail";
	}
	
	@RequestMapping(value="/edit/{id}", method=RequestMethod.GET)
	public String edit(@PathVariable("id") Long id, Model model) {
		if(id != null && id > 0) {
			model.addAttribute("patchGroup", patchGroupService.getPatchGroupById(id));
		}
		model.addAttribute("projectList", projectService.getProjectListResult(0, 0, Collections.<String, Object>emptyMap()));
		return "patchGroup/edit";
	}
	
	@RequestMapping(value="/edit", method=RequestMethod.GET)
	public String edit(Model model) {
		return edit(0L, model);
	}
	
	// TODO authz
	@RequestMapping(value="/edit", method=RequestMethod.POST)
	public @ResponseBody Map<String, Object> edit(
			@RequestParam(defaultValue="0") 
			Long id,
			@RequestParam(defaultValue="0")
			Long projectId,
			String name,
			String checkCode,
			String status
			) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		PatchGroup patchGroup = null;
		Project project = null;
		User user = null;
		String username = AuthUtil.getCurrentUsername();
		if(StringUtils.isBlank(username) || (user = userService.getUserByUsername(username)) == null) {
			resultMap.put("success", false);
			resultMap.put("message", "用户未登录或不存在!");
			return resultMap;
		}
		if(projectId == null && projectId == 0 || (project = projectService.getProjectById(projectId)) == null) {
			resultMap.put("success", false);
			resultMap.put("message", "项目不存在!");
			return resultMap;
		}
		if(id != null && id > 0) {
			patchGroup = patchGroupService.getPatchGroupById(id);
			if(patchGroup == null) {
				resultMap.put("success", false);
				resultMap.put("message", "补丁组不存在!");
				return resultMap;
			}
		} else {
			patchGroup = new PatchGroup();
			patchGroup.setCreator(user);
			patchGroup.setProject(project);
			patchGroup.setCreateTime(new Timestamp(System.currentTimeMillis()));
		}
		patchGroup.setName(name);
		patchGroup.setCheckCode(checkCode);
		patchGroup.setStatus(status);
		if(PatchGroup.STATUS_FINISHED.equals(status)) {
			patchGroup.setFinishTime(new Timestamp(System.currentTimeMillis()));
		}
		patchGroupService.saveOrUpdatePatchGroup(patchGroup);
		resultMap.put("success", true);
		resultMap.put("message", "补丁组保存成功!");
		return resultMap;
	}
	
	@RequestMapping(value="/listData")
	public @ResponseBody Map<String, Object> listData(
			@RequestParam(defaultValue="0") Long projectId,
			@RequestParam(defaultValue="") String status,
			@RequestParam(defaultValue=CommonConstant.DEFAUTL_START_STR) int start,
			@RequestParam(defaultValue=CommonConstant.DEFAULT_LIMIT_STR) int limit) {
		Map<String, Object> param = new HashMap<String, Object>();
		if(projectId > 0){
			param.put("project_id", projectId);
		}
		if(StringUtils.isNotBlank(status)) {
			param.put("status", status);
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("list", patchGroupService.getPatchGroupListResult(start, limit, param));
		resultMap.put("success", true);
		return resultMap;
	}
}
