package com.ablesky.asdeploy.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ablesky.asdeploy.dao.IPatchFileRelGroupDao;
import com.ablesky.asdeploy.dao.IPatchGroupDao;
import com.ablesky.asdeploy.pojo.PatchFileRelGroup;
import com.ablesky.asdeploy.pojo.PatchGroup;
import com.ablesky.asdeploy.service.IPatchGroupService;
import com.ablesky.asdeploy.util.Page;

@Service
public class PatchGroupServiceImpl implements IPatchGroupService {
	
	@Autowired
	private IPatchGroupDao patchGroupDao;
	@Autowired
	private IPatchFileRelGroupDao patchFileRelGroupDao;
	
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
	
	/**
	 * 根据补丁组状态(testing)和文件路径列表，来获取潜在的冲突文件信息
	 */
	@Override
	public List<PatchFileRelGroup> getPatchFileRelGroupListByFilePathListAndStatus(List<String> filePathList, String status, Long... excludedPatchGroupIds) {
		if(StringUtils.isBlank(status) || CollectionUtils.isEmpty(filePathList)) {
			return Collections.emptyList();
		}
		Map<String, Object> patchGroupParam = new HashMap<String, Object>();
		patchGroupParam.put("status", status);
		patchGroupParam.put("id__not_in", excludedPatchGroupIds);
		List<PatchGroup> patchGroupList = getPatchGroupListResult(0, 0, patchGroupParam);
		if(CollectionUtils.isEmpty(patchGroupList)) {
			return Collections.emptyList();
		}
		Collection<Long> patchGroupIdList = CollectionUtils.collect(patchGroupList, new Transformer<PatchGroup, Long>() {
			@Override
			public Long transform(PatchGroup patchGroup) {
				return patchGroup.getId();
			}
		});
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("patchGroupId__in", patchGroupIdList);
		param.put("patchFile_filePath__in", filePathList);
		List<PatchFileRelGroup> relList = patchFileRelGroupDao.list(0, 0, param);
		Map<Long, PatchGroup> patchGroupMap = new HashMap<Long, PatchGroup>();
		for(PatchGroup patchGroup: patchGroupList) {
			patchGroupMap.put(patchGroup.getId(), patchGroup);
		}
		for(PatchFileRelGroup rel: relList) {
			rel.setPatchGroup(patchGroupMap.get(rel.getPatchGroupId()));
		}
		return relList;
	}

}
