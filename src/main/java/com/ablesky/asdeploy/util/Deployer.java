package com.ablesky.asdeploy.util;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.log4j.Logger;

import com.ablesky.asdeploy.pojo.DeployItem;
import com.ablesky.asdeploy.pojo.DeployRecord;
import com.ablesky.asdeploy.service.IDeployService;
import com.ablesky.asdeploy.util.cmd.ShellCmd;

public class Deployer implements Callable<Boolean> {
	
	private static final Logger logger = Logger.getLogger(Deployer.class);
	
	public static final String DEPLOY_MANNER_DEPLOY = "deploy";
	public static final String DEPLOY_MANNER_ROLLBACK = "rollback";
	
	private static final ConcurrentHashMap<String, Object> deployCache = new ConcurrentHashMap<String, Object>();
	
	public static final ExecutorService executor = Executors.newSingleThreadExecutor();
	
	private static final String DEPLOY_RESULT_KEY_PREFIX = "deploy_result_";
	private static final String LOG_IS_WRITING_KEY_PREFIX = "log_is_writing_";
	private static final String LOG_LAST_READ_POS_KEY_PREFIX = "log_last_read_pos_";
	
	public static final String DEPLOY_LOG_PATH = FilenameUtils.concat(SystemUtils.USER_DIR, "deploy.log");
	
	private DeployRecord deployRecord;
	private String deployManner;
	private String serverGroupParam;
	
	public Deployer(DeployRecord deployRecord, String deployManner, String serverGroupParam) {
		this.deployRecord = deployRecord;
		this.deployManner = deployManner;
		this.serverGroupParam = serverGroupParam;
	}

	@Override
	public Boolean call() throws Exception {
		clearCache();
		Long deployRecordId = deployRecord.getId();
		setLogIsWriting(deployRecordId, Boolean.TRUE);
		setLogLastReadPos(deployRecordId, 0L);
		Boolean result = deploy(deployRecord, deployManner, serverGroupParam);
		setDeployResult(deployRecordId, result);
		setLogIsWriting(deployRecordId, Boolean.FALSE);
		renewDeployResultStatus(deployRecord, deployManner, result);
		return result;
	}
	
	private void renewDeployResultStatus(DeployRecord  deployRecord, String deployManner, Boolean result) {
		String status = null;
		if(DEPLOY_MANNER_DEPLOY.equals(deployManner)) {
			status = Boolean.TRUE.equals(result)? DeployRecord.STATUS_DEPLOY_SUCCESS: DeployRecord.STATUS_DEPLOY_FAILURE;
		} else if (DEPLOY_MANNER_ROLLBACK.equals(deployManner)) {
			status = Boolean.TRUE.equals(result)? DeployRecord.STATUS_ROLLBACK_SUCCESS: DeployRecord.STATUS_ROLLBACK_FAILURE;
		} else {
			return;
		}
		deployRecord.setStatus(status);
		SpringUtil.getBean(IDeployService.class).saveOrUpdateDeployRecord(deployRecord);
	}
	
	private static boolean deploy(DeployRecord deployRecord, String deployManner, String serverGroupParam) {
		boolean result = false;
		DeployItem item = deployRecord.getDeployItem();
		if(DeployItem.DEPLOY_TYPE_PATCH.equals(item.getDeployType())) {
			result = deployPatch(item, deployManner, serverGroupParam);
		} else if(DeployItem.DEPLOY_TYPE_WAR.equals(item.getDeployType())) {
			result = deployWar(item, serverGroupParam, deployRecord.getId());
		}
		if(result) {
			logger.info("Deploy success and deployRecord id is [" + deployRecord.getId() + "]");
		} else {
			logger.info("Deploy failed and deployRecord id is [" + deployRecord.getId() + "]");
		}
		return result;
		
	}
	
	private static boolean deployPatch(DeployItem item, String deployManner, String serverGroupParam) {
		String scriptPath = DeployUtil.getDeployPatchScriptPath();
		File scriptParentFolder = new File(FilenameUtils.getFullPath(scriptPath));
		String itemPatchFolder = DeployUtil.getDeployItemPatchFolder(item, deployManner);
		if(!"a".equals(serverGroupParam) && !"b".equals(serverGroupParam)) {
			serverGroupParam = "ab";
		}
		ShellCmd.ShellOperation sh = new ShellCmd(scriptParentFolder)
				.oper(ShellCmd.ShellOperationType.EXEC)
				.param(scriptPath)
				.param(itemPatchFolder)
				.param(DeployConfiguration.getInstance().getNeedSendEmail())
				.param(serverGroupParam);
		return executeCmdAndOutputLog(sh.exec(), new File(Deployer.DEPLOY_LOG_PATH));
	}
	
	private static boolean deployWar(DeployItem item, String serverGroupParam, Long deployRecordId) {
		File deployLog = new File(Deployer.DEPLOY_LOG_PATH);
		String scriptPath = DeployUtil.getDeployWarScriptPath(item.getProject());
		File scriptParentFolder = new File(FilenameUtils.getFullPath(scriptPath));
		if(serverGroupParam.contains("a")) {
			ShellCmd.ShellOperation shSideA = new ShellCmd(scriptParentFolder)
					.oper(ShellCmd.ShellOperationType.EXEC)
					.param(scriptPath)
					.param(item.getProject().getName() + "-" + item.getVersion())
					.param("a");
			Deployer.setLogLastReadPos(deployRecordId, 0L);
			if(!executeCmdAndOutputLog(shSideA.exec(), deployLog)) {
				return false;
			}
		}
		
		try {
			TimeUnit.SECONDS.sleep(3L);	// 睡3秒，好让前端有机会把之前的日志读完
		} catch (InterruptedException e) {}
		
		if(serverGroupParam.contains("b")) {
			ShellCmd.ShellOperation shSideB = new ShellCmd(scriptParentFolder)
					.oper(ShellCmd.ShellOperationType.EXEC)
					.param(scriptPath)
					.param(item.getProject().getName() + "-" + item.getVersion())
					.param("b");
			Deployer.setLogLastReadPos(deployRecordId, 0L);
			if(!executeCmdAndOutputLog(shSideB.exec(), deployLog)) {
				return false;
			}
		}
		return true;
	}
	
	private static boolean executeCmdAndOutputLog(Process process, File logFile) {
		if(logFile == null || process == null) {
			return false;
		}
		if(logFile.exists()) {
			logFile.delete();
		}
		try {
			FileUtils.copyInputStreamToFile(process.getInputStream(), logFile);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} 
		try {
			return process.waitFor() == 0;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static void setDeployResult(Long deployRecordId, Boolean result) {
		deployCache.put(DEPLOY_RESULT_KEY_PREFIX + deployRecordId, result);
	}
	
	public static Boolean getDeployResult(Long deployRecordId) {
		return (Boolean) deployCache.get(DEPLOY_RESULT_KEY_PREFIX + deployRecordId);
	}
	
	public static void deleteDeployResult(Long deployRecordId) {
		deployCache.remove(DEPLOY_RESULT_KEY_PREFIX + deployRecordId);
	}
	
	public static void setLogLastReadPos(Long deployRecordId, Long pos) {
		deployCache.put(LOG_LAST_READ_POS_KEY_PREFIX + deployRecordId, pos);
	}
	
	public static Long getLogLastReadPos(Long deployRecordId) {
		return (Long) deployCache.get(LOG_LAST_READ_POS_KEY_PREFIX + deployRecordId);
	}
	
	public static void deleteLogLastReadPos(Long deployRecordId) {
		deployCache.remove(LOG_LAST_READ_POS_KEY_PREFIX + deployRecordId);
	}
	
	public static void setLogIsWriting(Long deployRecordId, Boolean isWriting) {
		deployCache.put(LOG_IS_WRITING_KEY_PREFIX + deployRecordId, isWriting);
	}
	
	public static Boolean getLogIsWriting(Long deployRecordId) {
		return (Boolean) deployCache.get(LOG_IS_WRITING_KEY_PREFIX + deployRecordId);
	}
	
	public static void deleteLogIsWriting(Long deployRecordId) {
		deployCache.remove(LOG_IS_WRITING_KEY_PREFIX + deployRecordId);
	}
	
	public static void clearCache() {
		deployCache.clear();
	}

}
