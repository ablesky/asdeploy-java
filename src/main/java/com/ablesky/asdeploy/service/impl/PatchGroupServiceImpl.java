package com.ablesky.asdeploy.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.ablesky.asdeploy.dao.IConflictInfoDao;
import com.ablesky.asdeploy.dao.IPatchFileDao;
import com.ablesky.asdeploy.dao.IPatchFileRelGroupDao;
import com.ablesky.asdeploy.dao.IPatchGroupDao;
import com.ablesky.asdeploy.pojo.ConflictInfo;
import com.ablesky.asdeploy.pojo.PatchFile;
import com.ablesky.asdeploy.pojo.PatchFileRelGroup;
import com.ablesky.asdeploy.pojo.PatchGroup;
import com.ablesky.asdeploy.pojo.Project;
import com.ablesky.asdeploy.service.IPatchGroupService;
import com.ablesky.asdeploy.util.Page;

@Service
public class PatchGroupServiceImpl implements IPatchGroupService {
	
	@Autowired
	private IPatchGroupDao patchGroupDao;
	@Autowired
	private IPatchFileRelGroupDao patchFileRelGroupDao;
	@Autowired
	private IPatchFileDao patchFileDao;
	@Autowired
	private IConflictInfoDao conflictInfoDao;
	
	@Override
	public void saveOrUpdatePatchGroup(PatchGroup patchGroup) {
		patchGroupDao.saveOrUpdate(patchGroup);
	}
	
	@Override
	public PatchGroup getPatchGroupById(Long id) {
		return patchGroupDao.getById(id);
	}
	
	@Override
	public List<PatchGroup> getPatchGroupListResult(int start, int limit, Map<String, Object> param) {
		return patchGroupDao.list(start, limit, param);
	}
	
	@Override
	public Page<PatchGroup> getPatchGroupPaginateResult(int start, int limit, Map<String, Object> param) {
		return patchGroupDao.paginate(start, limit, param);
	}
	
	@Override
	public List<PatchFile> getPatchFileListResult(int start, int limit, Map<String, Object> param) {
		return patchFileDao.list(start, limit, param);
	}
	
	@Override
	public List<PatchFileRelGroup> getPatchFileRelGroupListResult(int start, int limit, Map<String, Object> param) {
		return patchFileRelGroupDao.list(start, limit, param);
	}
	
	/**
	 * 根据补丁组状态(testing)和文件路径列表，来获取潜在的冲突文件信息
	 */
	@Override
	public List<PatchFileRelGroup> getPatchFileRelGroupListWhichConflictWith(PatchGroup patchGroup, List<String> filePathList) {
		if(patchGroup == null || CollectionUtils.isEmpty(filePathList)) {
			return Collections.emptyList();
		}
		Project project = patchGroup.getProject();
		List<PatchGroup> otherPatchGroupList = patchGroupDao.list(new ModelMap()
				.addAttribute("status", PatchGroup.STATUS_TESTING)
				.addAttribute("id__ne", patchGroup.getId())
				.addAttribute("project_id", project.getId())
		);
		if(CollectionUtils.isEmpty(otherPatchGroupList)) {
			return Collections.emptyList();
		}
		Collection<Long> otherPatchGroupIdList = CollectionUtils.collect(otherPatchGroupList, new Transformer<PatchGroup, Long>() {
			@Override
			public Long transform(PatchGroup patchGroup) {
				return patchGroup.getId();
			}
		});
		List<PatchFileRelGroup> conflictRelList = patchFileRelGroupDao.list(new ModelMap()
				.addAttribute("patchGroupId__in", otherPatchGroupIdList)
				.addAttribute("patchFile_filePath__in", filePathList)
		);
		Map<Long, PatchGroup> otherPatchGroupMap = new HashMap<Long, PatchGroup>();
		for(PatchGroup otherPatchGroup: otherPatchGroupList) {
			otherPatchGroupMap.put(otherPatchGroup.getId(), otherPatchGroup);
		}
		for(PatchFileRelGroup conflictRel: conflictRelList) {
			conflictRel.setPatchGroup(otherPatchGroupMap.get(conflictRel.getPatchGroupId()));
		}
		return conflictRelList;
	}
	
	@Override
	public List<ConflictInfo> getConflictInfoListResultByParam(Map<String, Object> param) {
		return getConflictInfoListResultByParam(0, 0, param);
	}
	
	@Override
	public List<ConflictInfo> getConflictInfoListResultByParam(int start, int limit, Map<String, Object> param) {
		List<ConflictInfo> list = conflictInfoDao.list(start, limit, param);
		if(CollectionUtils.isEmpty(list)) {
			return list;
		}
		List<Long> relatedPatchGroupIdList = new ArrayList<Long>(CollectionUtils.collect(list, new Transformer<ConflictInfo, Long>() {
			@Override
			public Long transform(ConflictInfo conflictInfo) {
				return conflictInfo.getRelatedPatchGroupId();
			}
		}));
		List<PatchGroup> relatedPatchGroupList = patchGroupDao.list(new ModelMap().addAttribute("id__in", relatedPatchGroupIdList));
		Map<Long, PatchGroup> relatedPatchGroupMap = new HashMap<Long, PatchGroup>();
		for(PatchGroup relatedPatchGroup: relatedPatchGroupList) {
			relatedPatchGroupMap.put(relatedPatchGroup.getId(), relatedPatchGroup);
		}
		for(ConflictInfo conflictInfo: list) {
			conflictInfo.setRelatedPatchGroup(relatedPatchGroupMap.get(conflictInfo.getRelatedPatchGroupId()));
		}
		return list;
	}

}
