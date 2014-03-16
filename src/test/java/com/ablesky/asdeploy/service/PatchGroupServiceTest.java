package com.ablesky.asdeploy.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.ModelMap;

import com.ablesky.asdeploy.dao.IPatchFileRelGroupDao;
import com.ablesky.asdeploy.dao.IPatchGroupDao;
import com.ablesky.asdeploy.pojo.PatchFile;
import com.ablesky.asdeploy.pojo.PatchFileRelGroup;
import com.ablesky.asdeploy.pojo.PatchGroup;
import com.ablesky.asdeploy.pojo.Project;
import com.ablesky.asdeploy.service.impl.PatchGroupServiceImpl;

public class PatchGroupServiceTest {

	@InjectMocks
	private PatchGroupServiceImpl patchGroupService;
	
	@Mock
	private IPatchGroupDao patchGroupDao;
	
	@Mock
	private IPatchFileRelGroupDao patchFileRelGroupDao;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	/**
	 * 话说这真的有用么?
	 */
	@Test
	public void getPatchFileRelGroupListWhichConflictWith() {
		PatchGroup patchGroup = new PatchGroup();
		List<String> filePathList = Arrays.asList(new String[]{"com.ablesky.asdeploy.service.IUserService", "com.ablesky.asdeploy.service.IDeployService"});
		patchGroup.setId(1L);
		prepareForGetPatchFileRelGroupListWhichConflictWith(patchGroup, filePathList);
		Assert.assertEquals(8, patchGroupService.getPatchFileRelGroupListWhichConflictWith(patchGroup, filePathList).size());
		Assert.assertEquals(0, patchGroupService.getPatchFileRelGroupListWhichConflictWith(patchGroup, Collections.<String>emptyList()).size());
		patchGroup.setId(6L);
		prepareForGetPatchFileRelGroupListWhichConflictWith(patchGroup, filePathList);
		Assert.assertEquals(10, patchGroupService.getPatchFileRelGroupListWhichConflictWith(patchGroup, filePathList).size());
		Assert.assertEquals(0, patchGroupService.getPatchFileRelGroupListWhichConflictWith(patchGroup, Collections.<String>emptyList()).size());
	}
	
	private void prepareForGetPatchFileRelGroupListWhichConflictWith (PatchGroup patchGroup, List<String> filePathList) {
		Project project = new Project();
		project.setId(1L);
		patchGroup.setProject(project);
		List<PatchGroup> otherPatchGroupList = new ArrayList<PatchGroup>();
		List<Long> otherPatchGroupIdList = new ArrayList<Long>();
		for(Long id=1L; id<=5L; id++) {
			if(patchGroup.getId().equals(id)) {
				continue;
			}
			PatchGroup pg = new PatchGroup();
			pg.setId(id);
			otherPatchGroupList.add(pg);
			otherPatchGroupIdList.add(id);
		}
		Mockito.when(patchGroupDao.list(new ModelMap()
			.addAttribute("status", PatchGroup.STATUS_TESTING)
			.addAttribute("id__ne", patchGroup.getId())
			.addAttribute("project_id", project.getId())
		)).thenReturn(otherPatchGroupList);
		
		Map<String, PatchFile> patchFileMap = new HashMap<String, PatchFile>();
		for(int i=0; i<filePathList.size(); i++) {
			PatchFile patchFile = new PatchFile(project.getId(), filePathList.get(i));
			patchFile.setId(1L + i);
			patchFileMap.put(patchFile.getFilePath(), patchFile);
		}
		List<PatchFileRelGroup> conflictRelList = new ArrayList<PatchFileRelGroup>();
		for(PatchGroup pg: otherPatchGroupList) {
			for(String filePath: filePathList) {
				PatchFileRelGroup pfrg = new PatchFileRelGroup();
				pfrg.setPatchGroupId(pg.getId());
				pfrg.setPatchGroup(pg);
				pfrg.setPatchFile(patchFileMap.get(filePath));
				conflictRelList.add(pfrg);
			}
		}
		Mockito.when(patchFileRelGroupDao.list(new ModelMap()
				.addAttribute("patchGroupId__in", otherPatchGroupIdList)
				.addAttribute("patchFile_filePath__in", filePathList)
		)).thenReturn(conflictRelList);
	}
	
}
