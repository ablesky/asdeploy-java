package com.ablesky.asdeploy.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

/**
 * ZIP压缩与解压工具类
 */
public class ZipUtil {
	
	public static final String DEFAULT_ENCODING = "UTF-8";

	private ZipUtil() {}

	public static void zip(String[] sourceFilePaths, String zipPath) {
		zip(sourceFilePaths, zipPath, DEFAULT_ENCODING);
	}

	public static void zip(String[] sourceFilePaths, String zipPath, String encoding) {
		File[] sourceFiles = new File[sourceFilePaths.length];
		for (int i = 0; i < sourceFilePaths.length; i++) {
			sourceFiles[i] = new File(sourceFilePaths[i]);
		}
		zip(sourceFiles, zipPath, encoding);
	}

	public static void zip(File[] files, String zipPath) {
		zip(files, zipPath, DEFAULT_ENCODING);
	}

	public static void zip(File[] sourceFiles, String zipPath, String encoding) {
		ZipOutputStream zipOut = null;
		try {
			zipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipPath)));
			zipOut.setEncoding(encoding);
			for (File sourceFile: sourceFiles) {
				doZip(zipOut, sourceFile, sourceFile.getParent());
			}
			zipOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(zipOut);
		}
	}

	private static void doZip(ZipOutputStream zipOut, File sourceFile, String sourceRootPath) {
		if(sourceFile.isDirectory()) {
			File[] subFiles = sourceFile.listFiles();
			for (File subFile : subFiles) {
				doZip(zipOut, subFile, sourceRootPath);
			}
			return;
		}
		if(!sourceFile.isFile()) {
			return;
		}
		BufferedInputStream bufferedInput = null;
		try {
			bufferedInput = new BufferedInputStream(new FileInputStream(sourceFile));
			String zipEntryName = sourceFile.getPath().substring(sourceRootPath.length());
			while (zipEntryName.charAt(0) == '\\' || zipEntryName.charAt(0) == '/') {
				zipEntryName = zipEntryName.substring(1);
			}
			ZipEntry entry = new ZipEntry(zipEntryName);
			zipOut.putNextEntry(entry);
			IOUtils.copy(bufferedInput, zipOut);
			zipOut.closeEntry();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(bufferedInput);
		}
	}

	public static void unzip(String zipFilePath, String unzipPath) throws IOException {
		unzip(new File(zipFilePath), unzipPath);
	}

	public static void unzip(File zipFile, String unzipPath) throws IOException {
		File unzipRootDir = new File(unzipPath);
		unzipRootDir.mkdirs();
		ZipFile zip = new ZipFile(zipFile);
		Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.getEntries();
		while (entries.hasMoreElements()) {
			ZipEntry zipEntry = entries.nextElement();
			String destPath = FilenameUtils.concat(unzipPath, zipEntry.getName());
			File destFile = new File(destPath);
			if(zipEntry.isDirectory()) {
				destFile.mkdirs();
				continue;
			}
			FileUtils.copyInputStreamToFile(zip.getInputStream(zipEntry), destFile);
		}
		closeQuietly(zip);
	}
	
	public static void closeQuietly(ZipFile zipFile) {
		try {
			if(zipFile != null) {
				zipFile.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
