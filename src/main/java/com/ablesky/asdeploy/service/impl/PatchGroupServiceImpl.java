package com.ablesky.asdeploy.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ablesky.asdeploy.dao.IPatchGroupDao;
import com.ablesky.asdeploy.pojo.PatchGroup;
import com.ablesky.asdeploy.service.IPatchGroupService;
import com.ablesky.asdeploy.util.Page;

@Service
public class PatchGroupServiceImpl implements IPatchGroupService {
	
	@Autowired
	private IPatchGroupDao patchGroupDao;
	
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

}
