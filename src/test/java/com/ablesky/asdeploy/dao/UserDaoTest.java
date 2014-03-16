package com.ablesky.asdeploy.dao;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.ablesky.asdeploy.test.SpringTransactionalTestCase;

@ContextConfiguration(locations = {"classpath:/applicationContext-shiro.xml", "classpath:/applicationContext.xml"})
public class UserDaoTest extends SpringTransactionalTestCase {

	@Autowired
	private IUserDao userDao;
	
	@Test
	public void getById() {
		Assert.assertEquals(1L, 1L);
		Assert.assertEquals(0L, userDao.count(Collections.<String, Object>emptyMap()));
	}
	
}
