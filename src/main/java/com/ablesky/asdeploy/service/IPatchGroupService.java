package com.ablesky.asdeploy.service;

import java.util.List;
import java.util.Map;

import com.ablesky.asdeploy.pojo.PatchFileRelGroup;
import com.ablesky.asdeploy.pojo.PatchGroup;
import com.ablesky.asdeploy.util.Page;

public interface IPatchGroupService {

	List<PatchGroup> getPatchGroupListResult(int start, int limit, Map<String, Object> param);

	PatchGroup getPatchGroupById(Long id);

	void saveOrUpdatePatchGroup(PatchGroup patchGroup);

	Page<PatchGroup> getPatchGroupPaginateResult(int start, int limit, Map<String, Object> param);

	List<PatchFileRelGroup> getPatchFileRelGroupListByFilePathListAndStatus(List<String> filePathList, String status, Long... excludedPatchGroupIds);

}
