package com.ablesky.asdeploy.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemUtils;

import com.ablesky.asdeploy.pojo.DeployRecord;
import com.ablesky.asdeploy.service.IDeployService;

public class Deployer implements Callable<Boolean> {
	
	private static final ConcurrentHashMap<String, Object> deployCache = new ConcurrentHashMap<String, Object>();
	
	public static final ExecutorService executor = Executors.newSingleThreadExecutor();
	
	private static IDeployService deployService;
	
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
		Boolean result = ensureDeployService().deploy(deployRecord, deployManner, serverGroupParam);
		setDeployResult(deployRecordId, result);
		setLogIsWriting(deployRecordId, Boolean.FALSE);
		return result;
	}

	private static IDeployService ensureDeployService() {
		if(deployService == null) {
			deployService = SpringUtil.getBean(IDeployService.class);
		}
		return deployService;
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
