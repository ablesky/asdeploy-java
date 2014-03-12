package com.ablesky.asdeploy.service.impl;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;

import com.ablesky.asdeploy.dao.IConflictDetailDao;
import com.ablesky.asdeploy.dao.IConflictInfoDao;
import com.ablesky.asdeploy.dao.IDeployItemDao;
import com.ablesky.asdeploy.dao.IDeployLockDao;
import com.ablesky.asdeploy.dao.IDeployRecordDao;
import com.ablesky.asdeploy.dao.IPatchFileDao;
import com.ablesky.asdeploy.dao.IPatchFileRelGroupDao;
import com.ablesky.asdeploy.dao.IPatchGroupDao;
import com.ablesky.asdeploy.dao.IProjectDao;
import com.ablesky.asdeploy.pojo.ConflictDetail;
import com.ablesky.asdeploy.pojo.ConflictInfo;
import com.ablesky.asdeploy.pojo.DeployItem;
import com.ablesky.asdeploy.pojo.DeployLock;
import com.ablesky.asdeploy.pojo.DeployRecord;
import com.ablesky.asdeploy.pojo.PatchFile;
import com.ablesky.asdeploy.pojo.PatchFileRelGroup;
import com.ablesky.asdeploy.pojo.PatchGroup;
import com.ablesky.asdeploy.pojo.Project;
import com.ablesky.asdeploy.service.IDeployService;
import com.ablesky.asdeploy.service.IPatchGroupService;
import com.ablesky.asdeploy.util.AuthUtil;
import com.ablesky.asdeploy.util.CommonConstant;
import com.ablesky.asdeploy.util.DeployUtil;
import com.ablesky.asdeploy.util.Deployer;
import com.ablesky.asdeploy.util.Page;

@Service
public class DeployServiceImpl implements IDeployService {
	
	@Autowired
	private IProjectDao projectDao;
	@Autowired
	private IPatchGroupDao patchGroupDao;
	@Autowired
	private IDeployLockDao deployLockDao;
	@Autowired
	private IDeployRecordDao deployRecordDao;
	@Autowired
	private IDeployItemDao deployItemDao;
	@Autowired
	private IPatchFileDao patchFileDao;
	@Autowired
	private IConflictInfoDao conflictInfoDao;
	@Autowired
	private IPatchFileRelGroupDao patchFileRelGroupDao;
	@Autowired
	private IConflictDetailDao conflictDetailDao;
	
	@Autowired
	private IPatchGroupService patchGroupService;
	
	/**
	 * 检查发布流程是否被锁定
	 */
	@Override
	public DeployLock checkCurrentLock() {
		List<DeployLock> lockList = deployLockDao.list(new ModelMap()
				.addAttribute("isLocked", Boolean.TRUE)
				.addAttribute(CommonConstant.ORDER_BY, "id desc")
		);
		long ts = System.currentTimeMillis();
		for(DeployLock lock: lockList) {
			if(lock == null || !lock.getIsLocked()) {
				continue;
			}
			if(ts - lock.getLockedTime().getTime() > DeployLock.LOCK_EXPIRY_TIME) {
				lock.setIsLocked(Boolean.FALSE);
				saveOrUpdateDeployLock(lock);
			} else {
				return lock;
			}
		}
		return null;
	}
	
	@Override
	public void unlockDeploy() {
		List<DeployLock> lockList = deployLockDao.list(new ModelMap()
				.addAttribute("isLocked", Boolean.TRUE)
		);
		boolean isSuperAdmin = AuthUtil.isSuperAdmin();
		for(DeployLock lock: lockList) {
			if(lock == null || !lock.getIsLocked()) {
				continue;
			}
			if(isSuperAdmin || lock.getUser().getId() == AuthUtil.getCurrentUser().getId()) {
				lock.setIsLocked(Boolean.FALSE);
				saveOrUpdateDeployLock(lock);
			}
		}
	}
	
	@Override
	public void saveOrUpdateDeployLock(DeployLock lock) {
		deployLockDao.saveOrUpdate(lock);
	}
	
	@Override
	public void saveOrUpdateDeployRecord(DeployRecord record) {
		deployRecordDao.saveOrUpdate(record);
	}
	
	@Override
	public void saveOrUpdateDeployItem(DeployItem item) {
		deployItemDao.saveOrUpdate(item);
	}
	
	/**
	 * 存储上传文件，并更新deployItem和deployRecord
	 */
	@Override
	public DeployItem persistDeployItem(
			MultipartFile deployItemFile, 
			Project project, 
			PatchGroup patchGroup, 
			DeployRecord deployRecord,
			String deployType, 
			String version) throws IllegalStateException, IOException {
		String filename = deployItemFile.getOriginalFilename();
		File itemUploadFolder = new File(DeployUtil.getDeployItemUploadFolder(project.getName(), version));
		if(!itemUploadFolder.exists()) {
			itemUploadFolder.mkdirs();
		}
		deployItemFile.transferTo(new File(DeployUtil.getDeployItemUploadFolder(project.getName(), version) + filename));
		DeployItem deployItem = getDeployItemByFileNameAndVersion(filename, version);
		if(deployItem == null) {
			deployItem = buildNewDeployItem(project, patchGroup, deployType, version, filename);
		} else {
			deployItem.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		}
		deployItemDao.saveOrUpdate(deployItem);
		deployRecord.setDeployItem(deployItem);
		deployRecord.setStatus(DeployRecord.STATUS_UPLOADED);
		deployRecordDao.saveOrUpdate(deployRecord);
		return deployItem;
	}
	
	private DeployItem buildNewDeployItem(Project project, PatchGroup patchGroup, String deployType, String version, String filename) {
		DeployItem item = new DeployItem();
		item.setDeployType(deployType);
		item.setVersion(version);
		item.setFileName(filename);
		item.setFolderPath(DeployUtil.getDeployItemUploadFolder(project.getName(), version));
		item.setProject(project);
		item.setPatchGroup(patchGroup);
		item.setUser(AuthUtil.getCurrentUser());
		long ts = System.currentTimeMillis();
		item.setCreateTime(new Timestamp(ts));
		item.setUpdateTime(new Timestamp(ts));
		return item;
	}
	
	@Override
	public DeployItem getDeployItemByFileNameAndVersion(String fileName, String version) {
		return deployItemDao.first(new ModelMap()
				.addAttribute("fileName__eq", fileName)
				.addAttribute("version__eq", version)
		);
	}
	
	@Override
	public DeployRecord getDeployRecordById(Long id) {
		return deployRecordDao.getById(id);
	}
	
	@Override
	public Page<DeployRecord> getDeployRecordPaginateResult(int start, int limit, Map<String, Object> param) {
		return deployRecordDao.paginate(start, limit, param);
	}
	
	@Override
	public void persistInfoBeforeDeployStart(DeployRecord deployRecord, PatchGroup patchGroup, List<String> filePathList, String deployManner) {
		DeployItem item = deployRecord.getDeployItem();
		if(patchGroup != null && DeployItem.DEPLOY_TYPE_PATCH.equals(deployRecord.getDeployItem().getDeployType())) {
			item.setPatchGroup(patchGroup);
			saveOrUpdateDeployItem(item);
		}
		if(DeployItem.DEPLOY_TYPE_PATCH.equals(item.getDeployType())) {
			// 持久化文件列表
			batchSaveUnexistedPatchFile(deployRecord.getProject(), filePathList);
			if(patchGroup != null) {
				// 持久化patchFile与patchGroup的关联
				batchSaveUnexistedPatchFileRelGroup(patchGroup, filePathList);
				// 根据文件列表检测并持久化冲突信息
				List<PatchFileRelGroup> conflictRelList = patchGroupService.getPatchFileRelGroupListWhichConflictWith(patchGroup, filePathList);
				batchSaveUnexistedConflictInfo(patchGroup, deployRecord, conflictRelList, filePathList);
				if(CollectionUtils.isNotEmpty(conflictRelList)) {
					deployRecord.setIsConflictWithOthers(true);
				}
			}
		}
		// 将deployRecord的状态置为"发布中"
		renewDeployingStatus(deployRecord, deployManner);
	}
	
	private void renewDeployingStatus(DeployRecord deployRecord, String deployManner) {
		if(Deployer.DEPLOY_MANNER_DEPLOY.equalsIgnoreCase(deployManner)) {
			deployRecord.setStatus(DeployRecord.STATUS_DEPLOYING);
		} else if (Deployer.DEPLOY_MANNER_ROLLBACK.equalsIgnoreCase(deployManner)) {
			deployRecord.setStatus(DeployRecord.STATUS_ROLLBACKING);
		} else {
			return;
		}
		saveOrUpdateDeployRecord(deployRecord);
	}
	
	public void batchSaveUnexistedPatchFileRelGroup(final PatchGroup patchGroup, List<String> filePathList) {
		if(CollectionUtils.isEmpty(filePathList)) {
			return;
		}
		final Project project = patchGroup.getProject();
		List<PatchFile> patchFileList = patchFileDao.list(new ModelMap()
				.addAttribute("projectId", project.getId())
				.addAttribute("filePath__in", filePathList)
		);
		Map<String, PatchFile> patchFileMap = new HashMap<String, PatchFile>();
		for(PatchFile patchFile: patchFileList) {
			patchFileMap.put(patchFile.getFilePath(), patchFile);
		}
		
		List<PatchFileRelGroup> existedRelList = patchFileRelGroupDao.list(new ModelMap()
				.addAttribute("patchGroupId", patchGroup.getId())
		);
		for(PatchFileRelGroup existedRel: existedRelList) {
			patchFileMap.remove(existedRel.getPatchFile().getFilePath());
		}
		List<PatchFileRelGroup> unexistedRelList = new ArrayList<PatchFileRelGroup>();
		Timestamp createTime = new Timestamp(System.currentTimeMillis());
		for(PatchFile patchFile: patchFileMap.values()) {
			unexistedRelList.add(new PatchFileRelGroup(patchGroup.getId(), patchFile, createTime));
		}
		patchFileRelGroupDao.batchSave(unexistedRelList);
	}
	
	public void batchSaveUnexistedConflictInfo(final PatchGroup patchGroup, DeployRecord deployRecord, List<PatchFileRelGroup> conflictRelList, List<String> filePathList) {
		if(CollectionUtils.isEmpty(conflictRelList)) {
			return;
		}
		Map<String, PatchFileRelGroup> conflictRelMap = new HashMap<String, PatchFileRelGroup>();
		for(PatchFileRelGroup conflictRel: conflictRelList) {	// key的形式相当于relatedPathGroupId_filePath
			conflictRelMap.put(conflictRel.getPatchGroupId() + "_" + conflictRel.getPatchFile().getFilePath(), conflictRel);
		}
		List<PatchGroup> underTestingPatchGroupList = patchGroupDao.list(new ModelMap()
				.addAttribute("project_id", patchGroup.getProject().getId())
				.addAttribute("status", PatchGroup.STATUS_TESTING)
				.addAttribute("id__ne", patchGroup.getId())
		);
		List<Long> underTestingPatchGroupIdList = new ArrayList<Long>(CollectionUtils.collect(underTestingPatchGroupList, new Transformer<PatchGroup, Long>() {
			@Override
			public Long transform(PatchGroup patchGroup) {
				return patchGroup.getId();
			}
		}));
		underTestingPatchGroupIdList.add(0L);
		List<ConflictInfo> existedConflictInfoList = conflictInfoDao.list(new ModelMap()
				.addAttribute("patchGroupId", patchGroup.getId())
				.addAttribute("relatedPatchGroupId__in", underTestingPatchGroupIdList)
		);
		List<ConflictInfo> currentExistedConflictInfoList = new ArrayList<ConflictInfo>();
		for(ConflictInfo existedConflictInfo: existedConflictInfoList) {
			String keyToRemove = existedConflictInfo.getRelatedPatchGroupId() + "_" + existedConflictInfo.getPatchFile().getFilePath();
			if(conflictRelMap.remove(keyToRemove) != null) {
				currentExistedConflictInfoList.add(existedConflictInfo);
			}
		}
		List<ConflictInfo> unexistedConflictInfoList = new ArrayList<ConflictInfo>(CollectionUtils.collect(conflictRelMap.values(), new Transformer<PatchFileRelGroup, ConflictInfo>() {
			@Override
			public ConflictInfo transform(PatchFileRelGroup conflictRel) {
				return new ConflictInfo(conflictRel.getPatchFile(), patchGroup.getId(), conflictRel.getPatchGroupId());
			}
		}));
		batchSaveOrUpdateConflictInfo(unexistedConflictInfoList);	// 批量保存后，后续需要pojo里的id，所以只能用for循环去save。后续看看是否有办法优化
		// 此处调用下面这个方法，已经有些ugly了
		batchSaveConflictDetail(deployRecord, new ArrayList<ConflictInfo>(CollectionUtils.union(currentExistedConflictInfoList, unexistedConflictInfoList)));
	}
	
	public void batchSaveConflictDetail(DeployRecord deployRecord, List<ConflictInfo> conflictInfoList) {
		if(CollectionUtils.isEmpty(conflictInfoList)) {
			return;
		}
		Map<Long, ConflictInfo> conflictInfoMap = new HashMap<Long, ConflictInfo>();
		List<Long> conflictInfoIdList = new ArrayList<Long>();
		for(ConflictInfo conflictInfo: conflictInfoList) {
			conflictInfoMap.put(conflictInfo.getId(), conflictInfo);
			conflictInfoIdList.add(conflictInfo.getId());
		}
		List<ConflictDetail> existedConflictDetailList = conflictDetailDao.list(new ModelMap()
				.addAttribute("deployRecordId", deployRecord.getId())
				.addAttribute("conflictInfoId__in", conflictInfoIdList)
		);
		for(ConflictDetail existedConflictDetail: existedConflictDetailList) {
			conflictInfoMap.remove(existedConflictDetail.getConflictInfoId());
		}
		List<ConflictDetail> unexistedConflictDetailList = new ArrayList<ConflictDetail>();
		for(ConflictInfo conflictInfo: conflictInfoMap.values()) {
			unexistedConflictDetailList.add(new ConflictDetail(deployRecord.getId(), conflictInfo.getId()));
		}
		conflictDetailDao.batchSave(unexistedConflictDetailList);
	}
	
	@Deprecated
	public void batchSaveOrUpdateConflictInfo(List<ConflictInfo> conflictInfoList) {
		for(ConflictInfo conflictInfo: conflictInfoList) {
			conflictInfoDao.saveOrUpdate(conflictInfo);
		}
	}
	
	public void batchSaveUnexistedPatchFile(Project project, List<String> filePathList) {
		if(CollectionUtils.isEmpty(filePathList)) {
			return;
		}
		List<PatchFile> existedPatchFileList = patchFileDao.list(new ModelMap()
				.addAttribute("filePath__in", filePathList)
		);
		List<String> existedFilePathList = new ArrayList<String>(CollectionUtils.collect(existedPatchFileList, new Transformer<PatchFile, String>() {
			@Override
			public String transform(PatchFile patchFile) {
				return patchFile.getFilePath();
			}
		}));
		List<String> unexistedFilePathList = new ArrayList<String>(CollectionUtils.subtract(filePathList, existedFilePathList));
		List<PatchFile> unexistedPatchFileList = new ArrayList<PatchFile>();
		for(String unexistedFilePath: unexistedFilePathList) {
			unexistedPatchFileList.add(new PatchFile(project.getId(), unexistedFilePath));
		}
		patchFileDao.batchSave(unexistedPatchFileList);
	}
	
	@Override
	public List<ConflictDetail> getConflictDetailListResultByParam(Map<String, Object> param) {
		return getConflictDetailListResultByParam(0, 0, param);
	}
	
	@Override
	public List<ConflictDetail> getConflictDetailListResultByParam(int start, int limit, Map<String, Object> param){
		List<ConflictDetail> list = conflictDetailDao.list(start, limit, param);
		if(CollectionUtils.isEmpty(list)) {
			return list;
		}
		List<Long> conflictInfoIdList = new ArrayList<Long>(CollectionUtils.collect(list, new Transformer<ConflictDetail, Long>() {
			@Override
			public Long transform(ConflictDetail conflictDetail) {
				return conflictDetail.getConflictInfoId();
			}
		}));
		List<ConflictInfo> conflictInfoList = patchGroupService.getConflictInfoListResultByParam(0, 0, new ModelMap().addAttribute("id__in", conflictInfoIdList));
		Map<Long, ConflictInfo> conflictInfoMap = new HashMap<Long, ConflictInfo>();
		for(ConflictInfo conflictInfo: conflictInfoList) {
			conflictInfoMap.put(conflictInfo.getId(), conflictInfo);
		}
		for(ConflictDetail conflictDetail: list) {
			conflictDetail.setConflictInfo(conflictInfoMap.get(conflictDetail.getConflictInfoId()));
		}
		return list;
	}
	
}
