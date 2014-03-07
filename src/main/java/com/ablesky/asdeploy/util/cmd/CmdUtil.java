package com.ablesky.asdeploy.util.cmd;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

public class CmdUtil {
	
	private CmdUtil(){}
	
	public static <T> Map<String, T> consume(Process process, CmdCallback<T> callback) {
		if(process == null) {
			return Collections.emptyMap();
		}
		InputStream in = process.getInputStream();
		BufferedReader reader = null;
		Map<String, T> resultMap = new LinkedHashMap<String, T>();
		String line = "";
		try {
			reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			while((line = reader.readLine()) != null) {
				callback.process(line, resultMap);
			}
			callback.afterProcess(resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(in);
		}
		return resultMap;
	}
	
	public static int waitForProcess(Process process) {
		if(process != null) {
			try {
				return process.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}
	
}
