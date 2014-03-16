package com.ablesky.asdeploy.dao;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.ui.ModelMap;

import com.ablesky.asdeploy.test.SpringTransactionalTestCase;
import com.ablesky.asdeploy.util.AuthUtil;

@ContextConfiguration(locations = {"classpath:/applicationContext-shiro.xml", "classpath:/applicationContext.xml"})
public class ProjectDaoTest extends SpringTransactionalTestCase {
	
	@Autowired
	private IProjectDao projectDao;

	@Test
	public void getById() {
		Assert.assertEquals(1L, projectDao.getById(1L).getId().longValue());
	}
	
	@Test
	public void count() {
		Assert.assertEquals(7L, projectDao.count(Collections.<String, Object>emptyMap()));
		Assert.assertEquals(1L, projectDao.count(new ModelMap().addAttribute("id", 1L)));
		Assert.assertEquals(6L, projectDao.count(new ModelMap().addAttribute("id__gt", 1L)));
	}
	
}
