package com.ablesky.asdeploy.test;

import javax.sql.DataSource;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.ablesky.asdeploy.util.Profiles;

/**
 * 用注解替换掉对AbstractTransactionalJUnit4SpringContextTests的继承
 */
@Ignore
@ActiveProfiles(Profiles.UNIT_TEST)
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(transactionManager = "txManager")
@ContextConfiguration(locations = {"/applicationContext.xml", "/applicationContext-test.xml"})
public class SpringTransactionalTestCase {

	@Autowired
	protected DataSource dataSource;
	
}
