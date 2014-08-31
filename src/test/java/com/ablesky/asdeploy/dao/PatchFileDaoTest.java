package com.ablesky.asdeploy.dao;

import org.apache.shiro.util.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import com.ablesky.asdeploy.pojo.PatchFile;
import com.ablesky.asdeploy.test.SpringTransactionalTestCase;

public class PatchFileDaoTest extends SpringTransactionalTestCase {
	
	@Autowired
	private IPatchFileDao patchFileDao;

	@Test
	public void batchSave() {
		PatchFile patchFile1 = new PatchFile();
		patchFile1.setProjectId(1L);
		patchFile1.setFilePath("com.ablesky.asdeploy.service.DeployService");
		
		PatchFile patchFile2 = new PatchFile();
		patchFile2.setProjectId(2L);
		patchFile2.setFilePath("'; select 'sql injection';");
		
		patchFileDao.batchSave(CollectionUtils.asList(patchFile1, patchFile2));
		Assert.assertEquals(
				patchFile1.getProjectId().longValue(), 
				patchFileDao.first(new ModelMap().addAttribute("projectId", 1L)).getProjectId().longValue()
		);
		Assert.assertEquals(
				patchFile2.getFilePath(), 
				patchFileDao.first(new ModelMap().addAttribute("projectId", 2L)).getFilePath()
		);
	}
}
