package com.ablesky.asdeploy.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;

import org.apache.shiro.util.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.ModelMap;

import com.ablesky.asdeploy.dao.IConflictDetailDao;
import com.ablesky.asdeploy.dao.IConflictInfoDao;
import com.ablesky.asdeploy.dao.IDeployItemDao;
import com.ablesky.asdeploy.dao.IDeployLockDao;
import com.ablesky.asdeploy.dao.IDeployRecordDao;
import com.ablesky.asdeploy.dao.IPatchFileDao;
import com.ablesky.asdeploy.dao.IPatchFileRelGroupDao;
import com.ablesky.asdeploy.dao.IPatchGroupDao;
import com.ablesky.asdeploy.dao.IProjectDao;
import com.ablesky.asdeploy.pojo.DeployLock;
import com.ablesky.asdeploy.pojo.User;
import com.ablesky.asdeploy.service.impl.DeployServiceImpl;
import com.ablesky.asdeploy.test.ShiroTestUtils;
import com.ablesky.asdeploy.util.CommonConstant;

public class DeployServiceTest {

	@InjectMocks
	private DeployServiceImpl deployService;
	
	@Mock
	private IProjectDao projectDao;
	@Mock
	private IPatchGroupDao patchGroupDao;
	@Mock
	private IDeployLockDao deployLockDao;
	@Mock
	private IDeployRecordDao deployRecordDao;
	@Mock
	private IDeployItemDao deployItemDao;
	@Mock
	private IPatchFileDao patchFileDao;
	@Mock
	private IConflictInfoDao conflictInfoDao;
	@Mock
	private IPatchFileRelGroupDao patchFileRelGroupDao;
	@Mock
	private IConflictDetailDao conflictDetailDao;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void saveOrUpdateDeployLock() {
		DeployLock lock = new DeployLock();
		deployService.saveOrUpdateDeployLock(lock);
		verify(deployLockDao, times(1)).saveOrUpdate(lock);
	}
	
	@Test
	public void checkCurrentLock() {
		long ts = System.currentTimeMillis();
		// 已解开的锁
		DeployLock lock1 = new DeployLock();
		lock1.setId(1L);
		lock1.setIsLocked(Boolean.FALSE);
		// 已锁定且未超时的锁
		DeployLock lock2 = new DeployLock();
		lock2.setId(2L);
		lock2.setIsLocked(Boolean.TRUE);
		lock2.setLockedTime(new Timestamp(ts - DeployLock.LOCK_EXPIRY_TIME + 1000));
		// 已锁定且已超时的锁(正常情况下，这个锁的id不可能大于未超时的锁，此处仅为测试用)
		DeployLock lock3 = new DeployLock();
		lock3.setId(3L);
		lock3.setIsLocked(Boolean.TRUE);
		lock3.setLockedTime(new Timestamp(ts - DeployLock.LOCK_EXPIRY_TIME - 1000));
		when(deployLockDao.list(new ModelMap()
			.addAttribute("isLocked", Boolean.TRUE)
			.addAttribute(CommonConstant.ORDER_BY, "id desc")
		)).thenReturn(CollectionUtils.asList(lock3, lock2, lock1));	// 倒序返回
	
		DeployLock lock = deployService.checkCurrentLock();
		assertEquals(lock2, lock);	// 获取的是未超时的锁
		verify(deployLockDao, times(1)).saveOrUpdate(lock3);	// 已超时的锁被自动解锁了
	}
	
	@Test
	public void unlockDeploy() {
		User zyang = new User();
		zyang.setId(1L);
		zyang.setUsername("zyang");
		User hdu = new User();
		hdu.setId(2L);
		hdu.setUsername("hdu");
		DeployLock lock1 = new DeployLock();
		lock1.setUser(zyang);
		DeployLock lock2 = new DeployLock();
		lock2.setUser(hdu);
		
		when(deployLockDao.list(new ModelMap().addAttribute("isLocked", Boolean.TRUE)))
			.thenReturn(CollectionUtils.asList(lock1, lock2));
		
		// 普通用户只能解锁自己的锁
		lock1.setIsLocked(true);
		lock2.setIsLocked(true);
		ShiroTestUtils.mockCurrentUser(hdu, false);
		deployService.unlockDeploy();
		assertTrue(lock1.getIsLocked());
		assertFalse(lock2.getIsLocked());
		// 超级管理员可以解锁所有人的锁
		lock1.setIsLocked(true);
		lock2.setIsLocked(true);
		ShiroTestUtils.mockCurrentUser(zyang, true);
		deployService.unlockDeploy();
		assertFalse(lock1.getIsLocked());
		assertFalse(lock2.getIsLocked());
	}
	
	// 复杂的方法完全不知道该怎么测
	
}
