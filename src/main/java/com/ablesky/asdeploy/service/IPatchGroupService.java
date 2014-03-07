package com.ablesky.asdeploy.service;

import java.util.List;
import java.util.Map;

import com.ablesky.asdeploy.pojo.ConflictInfo;
import com.ablesky.asdeploy.pojo.PatchFile;
import com.ablesky.asdeploy.pojo.PatchFileRelGroup;
import com.ablesky.asdeploy.pojo.PatchGroup;
import com.ablesky.asdeploy.util.Page;

public interface IPatchGroupService {

	List<PatchGroup> getPatchGroupListResult(int start, int limit, Map<String, Object> param);

	PatchGroup getPatchGroupById(Long id);

	void saveOrUpdatePatchGroup(PatchGroup patchGroup);

	Page<PatchGroup> getPatchGroupPaginateResult(int start, int limit, Map<String, Object> param);

	List<PatchFileRelGroup> getPatchFileRelGroupListWhichConflictWith(PatchGroup patchGroup, List<String> filePathList);

	List<PatchFile> getPatchFileListResult(int start, int limit, Map<String, Object> param);

	List<PatchFileRelGroup> getPatchFileRelGroupListResult(int start, int limit, Map<String, Object> param);

	List<ConflictInfo> getConflictInfoListResultByPatchGroupId(Long patchGroupId);

}
