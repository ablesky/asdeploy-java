package com.ablesky.asdeploy.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import org.apache.commons.io.IOUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

/**
 * ZIP压缩与解压工具类
 */
public class ZipUtil {
	public static final String ENCODING_DEFAULT = "UTF-8";

	public static final int BUFFER_SIZE_DIFAULT = 1024;

	public static void zip(String[] inFilePaths, String zipPath) {
		zip(inFilePaths, zipPath, ENCODING_DEFAULT);
	}

	public static void zip(String[] inFilePaths, String zipPath, String encoding) {
		File[] inFiles = new File[inFilePaths.length];
		for (int i = 0; i < inFilePaths.length; i++) {
			inFiles[i] = new File(inFilePaths[i]);
		}
		zip(inFiles, zipPath, encoding);
	}

	public static void zip(File[] inFiles, String zipPath) {
		zip(inFiles, zipPath, ENCODING_DEFAULT);
	}

	public static void zip(File[] inFiles, String zipPath, String encoding) {
		ZipOutputStream zipOut = null;
		try {
			zipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipPath)));
			zipOut.setEncoding(encoding);
			for (int i = 0; i < inFiles.length; i++) {
				File file = inFiles[i];
				doZip(zipOut, file, file.getParent());
			}
			zipOut.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(zipOut);
		}
	}

	private static void doZip(ZipOutputStream zipOut, File file, String dirPath) {
		if (file.isFile()) {
			BufferedInputStream bis = null;
			try {
				bis = new BufferedInputStream(new FileInputStream(file));
				String zipName = file.getPath().substring(dirPath.length());
				while (zipName.charAt(0) == '\\' || zipName.charAt(0) == '/') {
					zipName = zipName.substring(1);
				}
				ZipEntry entry = new ZipEntry(zipName);
				zipOut.putNextEntry(entry);
				byte[] buff = new byte[BUFFER_SIZE_DIFAULT];
				int size;
				while ((size = bis.read(buff, 0, buff.length)) != -1) {
					zipOut.write(buff, 0, size);
				}
				zipOut.closeEntry();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(bis);
			}
		} else if(file.isDirectory()) {
			File[] subFiles = file.listFiles();
			for (File subFile : subFiles) {
				doZip(zipOut, subFile, dirPath);
			}
		}
	}

	public static void unzip(String zipFilePath, String storePath) throws IOException {
		unzip(new File(zipFilePath), storePath);
	}

	public static void unzip(File zipFile, String storePath) throws IOException {
		File storeFolder = new File(storePath);
		if (storeFolder.exists()) {
			storeFolder.delete();
		}
		storeFolder.mkdirs();

		ZipFile zip = new ZipFile(zipFile);
		Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.getEntries();
		while (entries.hasMoreElements()) {
			ZipEntry zipEntry = entries.nextElement();
			String zipEntryName = zipEntry.getName();
			File file = new File(storePath + File.separator + zipEntryName);
			if (zipEntry.isDirectory()) {
				file.mkdirs();
				continue;
			}
			if (zipEntryName.indexOf(File.separator) > 0) {
				String zipEntryDir = zipEntryName.substring(0, zipEntryName.lastIndexOf(File.separator) + 1);
				String unzipFileDir = storePath + File.separator + zipEntryDir;
				File unzipFileDirFile = new File(unzipFileDir);
				if (!unzipFileDirFile.exists()) {
					unzipFileDirFile.mkdirs();
				}
			}
			
			InputStream is = null;
			FileOutputStream fos = null;
			try {
				is = zip.getInputStream(zipEntry);
				File parentDir = file.getParentFile();
				if (!parentDir.exists()) {
					parentDir.mkdirs();
				}
				fos = new FileOutputStream(new File(storePath + File.separator + zipEntryName));
				byte[] buff = new byte[BUFFER_SIZE_DIFAULT];
				int size;
				while ((size = is.read(buff)) > 0) {
					fos.write(buff, 0, size);
				}
				fos.flush();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(fos);
				IOUtils.closeQuietly(is);
			}
		}
		try {
			zip.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
