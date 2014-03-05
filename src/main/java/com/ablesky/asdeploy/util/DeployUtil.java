package com.ablesky.asdeploy.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
import org.mozilla.universalchardet.UniversalDetector;

public class DeployUtil {
	
	private static final DeployConfiguration CONFIG = DeployConfiguration.getInstance();

	private DeployUtil() {}
	
	public static String getDeployItemUploadFolder(String projectName, String version) {
		return CONFIG.getItemRootPath() + projectName + "-" + version + "/";
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
		Collection<File> list = FileUtils.listFiles(new File(targetFolderPath), new NameFileFilter("readme.txt", IOCase.INSENSITIVE), (IOFileFilter)null);
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
}
