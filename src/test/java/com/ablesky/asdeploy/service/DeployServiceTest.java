package com.ablesky.asdeploy.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;

import org.apache.commons.lang3.SystemUtils;
import org.apache.shiro.util.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;

import com.ablesky.asdeploy.dao.IConflictDetailDao;
import com.ablesky.asdeploy.dao.IConflictInfoDao;
import com.ablesky.asdeploy.dao.IDeployItemDao;
import com.ablesky.asdeploy.dao.IDeployLockDao;
import com.ablesky.asdeploy.dao.IDeployRecordDao;
import com.ablesky.asdeploy.dao.IPatchFileDao;
import com.ablesky.asdeploy.dao.IPatchFileRelGroupDao;
import com.ablesky.asdeploy.dao.IPatchGroupDao;
import com.ablesky.asdeploy.dao.IProjectDao;
import com.ablesky.asdeploy.dao.base.DaoConstant;
import com.ablesky.asdeploy.pojo.DeployItem;
import com.ablesky.asdeploy.pojo.DeployLock;
import com.ablesky.asdeploy.pojo.DeployRecord;
import com.ablesky.asdeploy.pojo.PatchGroup;
import com.ablesky.asdeploy.pojo.Project;
import com.ablesky.asdeploy.pojo.User;
import com.ablesky.asdeploy.service.impl.DeployServiceImpl;
import com.ablesky.asdeploy.test.ShiroTestUtils;
import com.ablesky.asdeploy.util.DeployUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DeployUtil.class})
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
		PowerMockito.mockStatic(DeployUtil.class);
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
			.addAttribute(DaoConstant.ORDER_BY, "id desc")
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
	
	// 感觉下面这样的测试用例，实际上并没有什么作用
	// 另外，良好的测试用例应该是简洁的，清晰的。所以下面这些测试用例貌似是不良好的。
	// 还有，测试用例总比被测试的代码本身要长，这个是正常现象么?
	@Test
	public void persistDeployItemWhichExisted() throws IllegalStateException, IOException {
		// patchFolderPath改写到java.io.tmpdir下
		// 如果直接使用实际路径，在linux上持续集成测试时，有可能遇到文件写权限的问题
		String patchFolderPath = SystemUtils.JAVA_IO_TMPDIR + "/d/content/web-app-bak/ableskyapps/as-web-6.1/";
		String patchFilename = "20140317-website-patch-zyang-upgrade-todo.zip";
		File patchDest = new File(patchFolderPath + patchFilename);
		
		MultipartFile deployItemFile = Mockito.mock(MultipartFile.class);
		Mockito.when(deployItemFile.getOriginalFilename()).thenReturn(patchFilename);
		
		Project project = new Project();
		project.setId(1L);
		project.setName("as-web");
		
		PatchGroup patchGroup = new PatchGroup();
		patchGroup.setProject(project);
		
		DeployRecord deployRecord = new DeployRecord();
		deployRecord.setStatus(DeployRecord.STATUS_PREPARE);
		deployRecord.setProject(project);
		
		String version = "6.1";
		
		DeployItem deployItem = new DeployItem();
		deployItem.setId(1L);
		deployItem.setDeployType(DeployItem.DEPLOY_TYPE_PATCH);
		deployItem.setFileName(patchFilename);
		deployItem.setFolderPath(patchFolderPath);
		deployItem.setPatchGroup(patchGroup);
		deployItem.setProject(project);
		
		Mockito.when(deployItemDao.first(new ModelMap()
				.addAttribute("fileName__eq", patchFilename)
				.addAttribute("project_id__eq", project.getId())
				.addAttribute("version__eq", version)
		)).thenReturn(deployItem);
		
		Mockito.when(DeployUtil.getDeployItemUploadFolder(project.getName(), version))
			.thenReturn(patchFolderPath);
		
		assertEquals(deployItem, deployService.persistDeployItem(deployItemFile, project, patchGroup, deployRecord, DeployItem.DEPLOY_TYPE_PATCH, version));
		assertTrue(new File(patchFolderPath).isDirectory());
		Mockito.verify(deployItemFile, Mockito.times(1)).transferTo(patchDest);
		Mockito.verify(deployItemDao, Mockito.times(1)).saveOrUpdate(deployItem);
		Mockito.verify(deployRecordDao, Mockito.times(1)).saveOrUpdate(deployRecord);
		assertEquals(deployItem, deployRecord.getDeployItem());
		assertEquals(DeployRecord.STATUS_UPLOADED, deployRecord.getStatus());
	}
	
	@Test
	public void persistDeployItemWhichNotExisted() throws IllegalStateException, IOException {
		// patchFolderPath改写到java.io.tmpdir下
		// 如果直接使用实际路径，在linux上持续集成测试时，有可能遇到文件写权限的问题
		String patchFolderPath = SystemUtils.JAVA_IO_TMPDIR + "/d/content/web-app-bak/ableskyapps/as-web-6.1/";
		String patchFilename = "20140317-website-patch-zyang-upgrade-todo.zip";
		File patchDest = new File(patchFolderPath + patchFilename);
		
		MultipartFile deployItemFile = Mockito.mock(MultipartFile.class);
		Mockito.when(deployItemFile.getOriginalFilename()).thenReturn(patchFilename);
		
		User user = new User();
		user.setUsername("zyang");
		ShiroTestUtils.mockCurrentUser(user, false);
		
		Project project = new Project();
		project.setId(1L);
		project.setName("as-web");
		
		PatchGroup patchGroup = new PatchGroup();
		patchGroup.setProject(project);
		
		DeployRecord deployRecord = new DeployRecord();
		deployRecord.setStatus(DeployRecord.STATUS_PREPARE);
		deployRecord.setProject(project);
		
		String version = "6.1";
		
		Mockito.when(deployItemDao.first(new ModelMap()
				.addAttribute("fileName__eq", patchFilename)
				.addAttribute("project_id__eq", project.getId())
				.addAttribute("version__eq", version)
		)).thenReturn(null);
		
		Mockito.when(DeployUtil.getDeployItemUploadFolder(project.getName(), version))
			.thenReturn(patchFolderPath);
		
		DeployItem deployItem = deployService.persistDeployItem(deployItemFile, project, patchGroup, deployRecord, DeployItem.DEPLOY_TYPE_PATCH, version);
		assertTrue(new File(patchFolderPath).isDirectory());
		Mockito.verify(deployItemFile, Mockito.times(1)).transferTo(patchDest);
		Mockito.verify(deployItemDao, Mockito.times(1)).saveOrUpdate(deployItem);
		Mockito.verify(deployRecordDao, Mockito.times(1)).saveOrUpdate(deployRecord);
		assertEquals(user, deployItem.getUser());
		assertEquals(deployItem, deployRecord.getDeployItem());
		assertEquals(project, deployItem.getProject());
		assertEquals(DeployRecord.STATUS_UPLOADED, deployRecord.getStatus());
	}
	
	// 复杂的方法完全不知道该怎么测
	
}
