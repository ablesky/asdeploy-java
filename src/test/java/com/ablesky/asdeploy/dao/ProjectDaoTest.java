package com.ablesky.asdeploy.dao;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ablesky.asdeploy.dao.base.QueryParamMap;
import com.ablesky.asdeploy.test.SpringTransactionalTestCase;

public class ProjectDaoTest extends SpringTransactionalTestCase {
	
	@Autowired
	private IProjectDao projectDao;

	@Test
	public void getById() {
		assertEquals(1L, projectDao.getById(1L).getId().longValue());
	}
	
	@Test
	public void count() {
		assertEquals(7L, projectDao.count(Collections.<String, Object>emptyMap()));
		assertEquals(1L, projectDao.count(new QueryParamMap().addParam("id", 1L)));
		assertEquals(6L, projectDao.count(new QueryParamMap().addParam("id__gt", 1L)));
	}
	
}
