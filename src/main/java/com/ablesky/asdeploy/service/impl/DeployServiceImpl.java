package com.ablesky.asdeploy.service.impl;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ablesky.asdeploy.dao.IDeployItemDao;
import com.ablesky.asdeploy.dao.IDeployLockDao;
import com.ablesky.asdeploy.dao.IDeployRecordDao;
import com.ablesky.asdeploy.dao.IPatchGroupDao;
import com.ablesky.asdeploy.dao.IProjectDao;
import com.ablesky.asdeploy.pojo.DeployItem;
import com.ablesky.asdeploy.pojo.DeployLock;
import com.ablesky.asdeploy.pojo.DeployRecord;
import com.ablesky.asdeploy.pojo.PatchGroup;
import com.ablesky.asdeploy.pojo.Project;
import com.ablesky.asdeploy.service.IDeployService;
import com.ablesky.asdeploy.util.AuthUtil;
import com.ablesky.asdeploy.util.CommonConstant;
import com.ablesky.asdeploy.util.DeployUtil;
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
	
	/**
	 * 检查发布流程是否被锁定
	 */
	@Override
	public DeployLock checkCurrentLock() {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("isLocked", Boolean.TRUE);
		param.put(CommonConstant.ORDER_BY, "id desc");
		List<DeployLock> lockList = deployLockDao.list(param);
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
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("isLocked", Boolean.TRUE);
		List<DeployLock> lockList = deployLockDao.list(param);
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
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("fileName__eq", fileName);
		param.put("version__eq", version);
		return deployItemDao.first(param);
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
	public void generateConflictDetailForDeployRecord(DeployRecord deployRecord, PatchGroup patchGroup) {
		// TODO
	}
	
	@Override
	public void deploy(DeployRecord deployRecord, String deployManner) {
		// TODO
	}
	
	
}
