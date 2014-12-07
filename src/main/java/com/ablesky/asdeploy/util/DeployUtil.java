package com.ablesky.asdeploy.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.mozilla.universalchardet.UniversalDetector;

import com.ablesky.asdeploy.pojo.DeployItem;
import com.ablesky.asdeploy.pojo.Project;

public class DeployUtil {
	
	public static final String README_FILENAME = "readme.txt";
	
	private static final DeployConfiguration CONFIG = DeployConfiguration.getInstance();

	private DeployUtil() {}
	
	/**
	 * 获取上传补丁的目录(补丁目录的父目录)
	 */
	public static String getDeployItemUploadFolder(String projectName, String version) {
		return CONFIG.getItemRootPath() + projectName + "-" + version + "/";
	}
	
	/**
	 * 获取补丁目录
	 */
	public static String getDeployItemPatchFolder(DeployItem deployItem, String deployManner) {
		String deployItemName = FilenameUtils.getBaseName(deployItem.getFileName());
		if("rollback".equalsIgnoreCase(deployManner)) {
			deployItemName = deployItemName.replace("-todo", "-bakup");
		}
		return FilenameUtils.concat(getDeployItemUploadFolder(deployItem.getProject().getName(), deployItem.getVersion()), deployItemName);
	}
	
	/**
	 * 获取发布补丁的脚本目录
	 */
	public static String getDeployPatchScriptPath() {
		if(Profiles.isDevelopment()) { // 返回假的发布脚本，供开发环境使用
			return CONFIG.getScriptPathForDevTest();
		}
		return FilenameUtils.concat(CONFIG.getScriptRootPath(), "patch-shell/start_patch_main.sh");
	}
	
	/**
	 * 获取发布版本的脚本目录
	 */
	public static String getDeployWarScriptPath(Project project) {
		if(Profiles.isDevelopment()) { // 返回假的发布脚本，供开发环境使用
			return CONFIG.getScriptPathForDevTest();
		}
		Integer deployScriptType = project.getDeployScriptType();
		String projectName = project.getName();
		String scriptPath = null;
		// war包发布脚本的机制产生了变更，也许需要待机制成熟后做进一步的更改
		if(Integer.valueOf(0).equals(deployScriptType)) {
			scriptPath = FilenameUtils.concat(CONFIG.getScriptRootPath(), projectName + "-deploy/" + projectName + "-deploy.sh");
		} else {
			scriptPath = FilenameUtils.concat(CONFIG.getScriptRootPath(),  "deploy-as/deploy.sh");
		}
		return scriptPath;
	}
	
	/**
	 * 解压缩文件
	 */
	public static void unzipDeployItem(DeployItem deployItem) throws IOException {
		String sourceFilePath = FilenameUtils.concat(deployItem.getFolderPath(), deployItem.getFileName());
		String targetFolderPath = FilenameUtils.concat(deployItem.getFolderPath(), FilenameUtils.getBaseName(deployItem.getFileName()));
		String parentFolderPath = deployItem.getFolderPath();
		File targetFolder = new File(targetFolderPath);
		FileUtils.deleteDirectory(targetFolder);
		ZipUtil.unzip(sourceFilePath, parentFolderPath);
	}
	
	/**
	 * 获取解压后的文件列表(按发布格式)
	 */
	public static List<String> getDeployItemFilePathList(String targetFolderPath) {
		targetFolderPath = FilenameUtils.normalize(targetFolderPath);
		File parentFolder = null;
		if(StringUtils.isBlank(targetFolderPath) || !(parentFolder = new File(targetFolderPath)).exists()) {
			return Collections.emptyList();
		}
		Collection<File> fileList = FileUtils.listFiles(parentFolder, null, true);
		List<String> filePathList = new ArrayList<String>(fileList.size());
		targetFolderPath = targetFolderPath.substring(FilenameUtils.getPrefixLength(targetFolderPath));
		for(File file: fileList) {
			if(README_FILENAME.equalsIgnoreCase(file.getName())) {
				continue;
			}
			String filePath = FilenameUtils.normalize(file.getAbsolutePath());
			filePath = filePath.substring(FilenameUtils.getPrefixLength(filePath));
			filePath = filePath.replace(targetFolderPath + File.separator, "");
			filePathList.add(filePath.replaceAll("\\" + File.separator, "."));
		}
		return filePathList;
	}
	
	/**
	 * 读取readme的文本内容
	 */
	public static String loadReadmeContent(String targetFolderPath) {
		File targetFolder = new File(targetFolderPath);
		if(!targetFolder.exists()) {
			return "";
		}
		Collection<File> list = FileUtils.listFiles(new File(targetFolderPath), new NameFileFilter(README_FILENAME, IOCase.INSENSITIVE), (IOFileFilter)null);
		if(CollectionUtils.isEmpty(list)) {
			return "";
		}
		File readmeFile = list.iterator().next();
		try {
			String charset = detectCharset(readmeFile);
			if(StringUtils.isBlank(charset)) {
				charset = "UTF-8";
			}
			return FileUtils.readFileToString(readmeFile, charset);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 探测文本的字符编码
	 */
	public static String detectCharset(File file) {
		if(file == null || !file.exists() || !file.isFile()) {
			throw new IllegalArgumentException("The argument should be an existed file!");
		}
		byte[] buf = new byte[4096];
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			UniversalDetector detector = new UniversalDetector(null);
			int len;
			while((len = fis.read(buf)) > 0 && !detector.isDone()) {
				detector.handleData(buf, 0, len);
			}
			detector.dataEnd();
			return detector.getDetectedCharset();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(fis);
		}
		return null;
	}
	
	/**
	 * 获取服务器的hostname
	 */
	public static String getHostname() {
		try {
			InetAddress localhost =InetAddress.getLocalHost();
			return localhost.getHostName();
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return "unknown";
	}
	
	public static String readDeployLogContent(Long deployRecordId) {
		Long startPos = Deployer.getLogLastReadPos(deployRecordId);
		if(deployRecordId == null || startPos == null) {
			return "";
		}
		StringBuilder buff = new StringBuilder();
		File deployLog = new File(Deployer.DEPLOY_LOG_PATH);
		if(!deployLog.exists()) {
			return "";
		}
		RandomAccessFile deployLogReader = null;
		try {
			deployLogReader = new RandomAccessFile(deployLog, "r");
			deployLogReader.seek(startPos);
			String line = "";
			while((line = deployLogReader.readLine()) != null) {
				buff.append(line).append(SystemUtils.LINE_SEPARATOR);
			}
			Deployer.setLogLastReadPos(deployRecordId, deployLogReader.getFilePointer());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(deployLogReader);
		}
		return buff.toString();
	}
	
}
