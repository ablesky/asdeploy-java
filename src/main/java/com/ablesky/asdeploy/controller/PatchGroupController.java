package com.ablesky.asdeploy.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ablesky.asdeploy.pojo.ConflictInfo;
import com.ablesky.asdeploy.pojo.PatchFile;
import com.ablesky.asdeploy.pojo.PatchFileRelGroup;
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
		param.put(CommonConstant.ORDER_BY, "id desc");
		model.addAttribute("projectList", projectService.getProjectListResult(0, 0, Collections.<String, Object>emptyMap()))
				.addAttribute("page", patchGroupService.getPatchGroupPaginateResult(start, limit, param))
				.addAttribute("isSuperAdmin", AuthUtil.isSuperAdmin())
				.addAttribute("currentUser", AuthUtil.getCurrentUser());
		return "patchGroup/list";
	}
	
	@RequestMapping("/detail/{id}")
	public String detail(@PathVariable("id") Long id, Model model) {
		PatchGroup patchGroup = patchGroupService.getPatchGroupById(id);
		List<PatchFileRelGroup> relList = patchGroupService.getPatchFileRelGroupListResult(0, 0, new ModelMap()
				.addAttribute("patchGroupId", id)
		);
		List<PatchFile> patchFileList = new ArrayList<PatchFile>(CollectionUtils.collect(relList, new Transformer<PatchFileRelGroup, PatchFile>() {
			@Override
			public PatchFile transform(PatchFileRelGroup rel) {
				return rel.getPatchFile();
			}
		}));
		List<ConflictInfo> conflictInfoList = patchGroupService.getConflictInfoListResultByParam(new ModelMap().addAttribute("patchGroupId", id));
		Collections.sort(conflictInfoList, new Comparator<ConflictInfo>() {
			@Override
			public int compare(ConflictInfo info1, ConflictInfo info2) {
				Long patchGroupId1 = info1.getRelatedPatchGroupId();
				Long patchGroupId2 = info2.getRelatedPatchGroupId();
				if(patchGroupId1 == patchGroupId2) {
					String filePath1 = info1.getPatchFile().getFilePath();
					String filePath2 = info2.getPatchFile().getFilePath();
					return filePath1.compareTo(filePath2);
				}
				return patchGroupId1.compareTo(patchGroupId2);
			}
		});
		model.addAttribute("patchGroup", patchGroup)
			.addAttribute("patchFileList", patchFileList)
			.addAttribute("conflictInfoList", conflictInfoList)
			.addAttribute("isSuperAdmin", AuthUtil.isSuperAdmin())
			.addAttribute("currentUser", AuthUtil.getCurrentUser());
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
		ModelMap resultMap = new ModelMap();
		PatchGroup patchGroup = null;
		Project project = null;
		User currentUser = AuthUtil.getCurrentUser();
		if(currentUser == null) {
			return resultMap.addAttribute("success", false).addAttribute("message", "用户未登录或不存在!");
		}
		if(projectId == null || projectId == 0 || (project = projectService.getProjectById(projectId)) == null) {
			return resultMap.addAttribute("success", false).addAttribute("message", "项目不存在!");
		}
		if(id != null && id > 0) {
			patchGroup = patchGroupService.getPatchGroupById(id);
			if(patchGroup == null) {
				return resultMap.addAttribute("success", false).addAttribute("message", "补丁组不存在!");
			}
		} else {
			patchGroup = new PatchGroup();
			patchGroup.setCreator(currentUser);
			patchGroup.setProject(project);
			patchGroup.setCreateTime(new Timestamp(System.currentTimeMillis()));
		}
		if(!AuthUtil.isSuperAdmin() && !currentUser.getId().equals(patchGroup.getCreator().getId())) {
			return resultMap.addAttribute("success", false).addAttribute("message", "没有权限执行此操作!");
		}
		patchGroup.setName(name);
		patchGroup.setCheckCode(checkCode);
		patchGroup.setStatus(status);
		if(PatchGroup.STATUS_FINISHED.equals(status)) {
			patchGroup.setFinishTime(new Timestamp(System.currentTimeMillis()));
		}
		patchGroupService.saveOrUpdatePatchGroup(patchGroup);
		return resultMap.addAttribute("success", true).addAttribute("message", "补丁组保存成功!");
	}
	
	@RequestMapping(value="/listData")
	public @ResponseBody Map<String, Object> listData(
			@RequestParam(defaultValue="0") Long projectId,
			@RequestParam(defaultValue="") String status,
			@RequestParam(defaultValue=CommonConstant.DEFAUTL_START_STR) int start,
			@RequestParam(defaultValue=CommonConstant.DEFAULT_LIMIT_STR) int limit) {
		ModelMap param = new ModelMap();
		if(projectId > 0){
			param.put("project_id", projectId);
		}
		if(StringUtils.isNotBlank(status)) {
			param.put("status", status);
		}
		param.put(CommonConstant.ORDER_BY, " id desc ");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("list", patchGroupService.getPatchGroupListResult(start, limit, param));
		resultMap.put("success", true);
		return resultMap;
	}
}
