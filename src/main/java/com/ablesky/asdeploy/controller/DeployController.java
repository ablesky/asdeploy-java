package com.ablesky.asdeploy.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.io.FilenameUtils;
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
import org.springframework.web.multipart.MultipartFile;

import com.ablesky.asdeploy.dto.ConflictInfoDto;
import com.ablesky.asdeploy.pojo.DeployItem;
import com.ablesky.asdeploy.pojo.DeployLock;
import com.ablesky.asdeploy.pojo.DeployRecord;
import com.ablesky.asdeploy.pojo.PatchFileRelGroup;
import com.ablesky.asdeploy.pojo.PatchGroup;
import com.ablesky.asdeploy.pojo.Project;
import com.ablesky.asdeploy.service.IDeployService;
import com.ablesky.asdeploy.service.IPatchGroupService;
import com.ablesky.asdeploy.service.IProjectService;
import com.ablesky.asdeploy.util.AuthUtil;
import com.ablesky.asdeploy.util.DeployUtil;

@Controller
@RequestMapping("/deploy")
public class DeployController {
	
	@Autowired
	private IProjectService projectService;
	@Autowired
	private IDeployService deployService;
	@Autowired
	private IPatchGroupService patchGroupService;
	
	@RequestMapping("/initOption/{msg}")
	public String initOption(@PathVariable("msg") String msg, Model model) {
		if("paramsError".equals(msg)) {
			model.addAttribute("errorMessage", "输入参数有误!");
		}
		return initOption(model);
	}
	
	@RequestMapping("/initOption")
	public String initOption(Model model) {
		List<Project> projectList = projectService.getProjectListResult(0, 0, Collections.<String, Object>emptyMap());
		model.addAttribute("projectList", projectList);
		return "deploy/initOption";
	}
	
	@RequestMapping(value = "/toDeployPage", method = RequestMethod.POST)
	public String toDeployPage(
			String deployType,
			String version,
			Long projectId,
			@RequestParam(required=false)
			Long patchGroupId,
			Model model) {
		Project project = null;
		PatchGroup patchGroup = null;
		if(StringUtils.isBlank(deployType) || StringUtils.isBlank(version) 
				|| projectId == null || projectId == 0
				|| (project = projectService.getProjectById(projectId)) == null ) {
			return "redirect:/deploy/initOption/paramsError";
		}
		DeployLock lock = deployService.checkCurrentLock();
		if(lock != null) {
			return "redirect:/main";
		}
		if(patchGroupId != null && patchGroupId > 0) {
			patchGroup = patchGroupService.getPatchGroupById(patchGroupId);
		}
		
		DeployRecord deployRecord = buildNewDeployRecord(project);
		deployService.saveOrUpdateDeployRecord(deployRecord);
		DeployLock newLock = buildNewDeployLock(deployRecord);
		deployService.saveOrUpdateDeployLock(newLock);
		
		model.addAttribute("project", project)
			.addAttribute("deployType", deployType)
			.addAttribute("version", version)
			.addAttribute("patchGroup", patchGroup)
			.addAttribute("deployRecord", deployRecord);
		return "deploy/deployPage";
	}
	
	private DeployRecord buildNewDeployRecord(Project project) {
		DeployRecord deployRecord = new DeployRecord();
		deployRecord.setUser(AuthUtil.getCurrentUser());
		deployRecord.setProject(project);
		deployRecord.setCreateTime(new Timestamp(System.currentTimeMillis()));
		deployRecord.setIsConflictWithOthers(false);
		deployRecord.setStatus(DeployRecord.STATUS_PREPARE);
		return deployRecord;
	}
	
	private DeployLock buildNewDeployLock(DeployRecord deployRecord) {
		DeployLock lock = new DeployLock();
		lock.setUser(AuthUtil.getCurrentUser());
		lock.setDeployRecord(deployRecord);
		lock.setLockedTime(new Timestamp(System.currentTimeMillis()));
		lock.setIsLocked(Boolean.TRUE);
		return lock;
	}
	
	/**
	 * 在deployService.unlockDeploy方法中判断解锁权限
	 * 没有权限自然解不开
	 */
	@RequestMapping("/unlockDeploy")
	public @ResponseBody Map<String, Object> unlockDeploy() {
		deployService.unlockDeploy();
		ModelMap resultMap = new ModelMap();
		return resultMap.addAttribute("success", true);
	}
	
	@RequestMapping("/unlockDeployRedirect")
	public String unlockDeployRedirect() {
		deployService.unlockDeploy();
		return "redirect:/main";
	}
	
	@RequestMapping("/uploadItem")
	public @ResponseBody Map<String, Object> uploadItem(
			Long projectId,
			Long deployRecordId,
			@RequestParam(defaultValue = "0")
			Long patchGroupId,
			String deployType,
			String version,
			@RequestParam("deployItemField")
			MultipartFile deployItemFile
			) throws IllegalStateException, IOException {
		ModelMap resultMap = new ModelMap();
		String filename = deployItemFile.getOriginalFilename();
		Project project = projectService.getProjectById(projectId);
		DeployRecord deployRecord = deployService.getDeployRecordById(deployRecordId);
		PatchGroup patchGroup = null;
		if(patchGroupId != null && patchGroupId > 0) {
			patchGroup = patchGroupService.getPatchGroupById(patchGroupId);
			if(patchGroup == null) {
				return resultMap.addAttribute("success", false).addAttribute("message", "补丁组不存在!");
			}
			if(!filename.contains(patchGroup.getCheckCode())) {
				return resultMap.addAttribute("success", false).addAttribute("message", "补丁名称与补丁组的标识号不匹配!");
			}
		}
		deployService.persistDeployItem(deployItemFile, project, patchGroup, deployRecord, deployType, version);
		return resultMap
				.addAttribute("filename", filename)
				.addAttribute("size", deployItemFile.getSize())
				.addAttribute("success", true);
	}
	
	@RequestMapping(value="/decompressItem", method=RequestMethod.POST)
	public @ResponseBody Map<String, Object> decompressItem(
			Long deployRecordId,
			@RequestParam(defaultValue="0")
			Long patchGroupId
			) throws IOException {
		ModelMap resultMap = new ModelMap();
		DeployRecord deployRecord = deployService.getDeployRecordById(deployRecordId);
		DeployItem deployItem = deployRecord.getDeployItem();
		if(deployItem == null) {
			return resultMap.addAttribute("success", false).addAttribute("message", "压缩文件不存在!");
		}
		DeployUtil.unzipDeployItem(deployItem);
		String targetFolderPath = FilenameUtils.concat(deployItem.getFolderPath(), FilenameUtils.getBaseName(deployItem.getFileName()));
		List<String> filePathList = DeployUtil.getDeployItemFilePathList(targetFolderPath);
		List<ConflictInfoDto> conflictInfoList = Collections.emptyList();
		if(patchGroupId != null && patchGroupId > 0) {
			final PatchGroup patchGroup = patchGroupService.getPatchGroupById(patchGroupId);
			if(patchGroup != null) {
				List<PatchFileRelGroup> conflictRelList = patchGroupService.getPatchFileRelGroupListWhichConflictWith(patchGroup, filePathList);
				conflictInfoList = new ArrayList<ConflictInfoDto>(CollectionUtils.collect(conflictRelList, new Transformer<PatchFileRelGroup, ConflictInfoDto>() {
					@Override
					public ConflictInfoDto transform(PatchFileRelGroup conflictRel) {
						return new ConflictInfoDto().fillDto(patchGroup, conflictRel);
					}
				}));
			}
		}
		return resultMap
				.addAttribute("filePathList", filePathList)
				.addAttribute("conflictInfoList", conflictInfoList)
				.addAttribute("success", true);
	}
	
	/**
	 * @param deployRecordId
	 * @param deployManner	"发布(deploy)"或"回滚(rollback)"
	 * @return
	 */
	@RequestMapping(value="/startDeploy", method=RequestMethod.POST)
	public @ResponseBody Map<String, Object> startDeploy(
			Long deployRecordId, 
			@RequestParam(defaultValue = "0")
			Long patchGroupId,
			String deployManner) {
		ModelMap resultMap = new ModelMap();
		DeployRecord deployRecord = null;
		PatchGroup patchGroup = null;
		if(deployRecordId == null || deployRecordId <= 0 || (deployRecord = deployService.getDeployRecordById(deployRecordId)) == null) {
			return resultMap.addAttribute("success", false).addAttribute("message", "参数有误!");
		}
		DeployLock lock = deployService.checkCurrentLock();
		if(lock == null || !lock.getDeployRecord().getId().equals(deployRecordId)) {
			return resultMap.addAttribute("success", false).addAttribute("message", "本次发布已被解锁!");
		}
		if(DeployRecord.STATUS_PREPARE.equals(deployRecord.getStatus())) {
			return resultMap.addAttribute("success", false).addAttribute("message", "尚未上传文件");
		}
		if(Boolean.TRUE == null) { // TODO 发布仍在继续
			return resultMap.addAttribute("success", false).addAttribute("message", "发布仍在继续中...");
		}
		if(patchGroupId != null && patchGroupId > 0) {
			patchGroup = patchGroupService.getPatchGroupById(patchGroupId);
		}
		// 开始发布
		doDeploy(deployRecord, patchGroup, deployManner);
		return resultMap.addAttribute("success", true).addAttribute("message", "发布启动成功!");
	}
	
	private void doDeploy(DeployRecord deployRecord, PatchGroup patchGroup, String deployManner) {
		// 1. 记录补丁组及冲突信息
		DeployItem item = deployRecord.getDeployItem();
		String targetFolderPath = FilenameUtils.concat(item.getFolderPath(), FilenameUtils.getBaseName(item.getFileName()));
		List<String> filePathList = DeployUtil.getDeployItemFilePathList(targetFolderPath);
		deployService.persistInfoBeforeDeployStart(deployRecord, patchGroup, filePathList);
		deployService.deploy(deployRecord, deployManner);
	}
	
	@RequestMapping("/readDeployLogOnRealtime")
	public @ResponseBody Map<String, Object> readDeployLogOnRealtime(Long deployRecordId) {
		return new ModelMap()
				.addAttribute("logInfoList", Arrays.asList(new String[]{"发布完成了，呵呵"}))
				.addAttribute("isFinished", true)
				.addAttribute("deployResult", true);
	}
}
