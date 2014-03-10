package com.ablesky.asdeploy.util.cmd;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ablesky.asdeploy.util.ClassUtil;

public abstract class AbstractCmd <O extends AbstractCmd.Operation<T>, T extends AbstractCmd.OperationType> {
	
	protected File dir;
	private Class<O> operationClass;
	private Class<T> operationTypeClass;
	
	@SuppressWarnings("unchecked")
	public AbstractCmd(File dir) {
		if(dir == null) {
			throw new NullPointerException("Dir should not be null!");
		}
		if(!dir.isDirectory()) {
			throw new IllegalArgumentException("File path [" + dir.getAbsolutePath() + "] does not exist or is not a directory!");
		}
		this.dir = dir;
		Type[] types = ClassUtil.getSuperClassGenericTypes(this.getClass());
		operationClass = (Class<O>) types[0];
		operationTypeClass = (Class<T>) types[1];
	}
	
	public O oper(T operType) {
		try {
			return operationClass.getConstructor(File.class, this.operationTypeClass).newInstance(dir, operType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static abstract class Operation<T extends OperationType> {
		
		protected final Logger logger = Logger.getLogger(this.getClass());
		
		protected File dir;
		protected T operType;
		protected Map<String, String> params = new LinkedHashMap<String, String>();
		
		public Operation(File dir, T operType) {
			this.dir = dir;
			this.operType = operType;
		}

		@SuppressWarnings("unchecked")
		public <O extends AbstractCmd.Operation<T>> O param(String key, Object value){
			if(key == null || key.trim().isEmpty()) {
				throw new IllegalArgumentException("The key of param should not by null!");
			}
			String val = value != null? String.valueOf(value).trim(): "";
			params.put(key, val);
			return (O) this;
		}
		
		@SuppressWarnings("unchecked")
		public <O extends AbstractCmd.Operation<T>> O param(String key) {
			param(key, null);
			return (O) this;
		}
		
		public Process exec(){
			String cmd = this.toString();
			Runtime runtime = Runtime.getRuntime();
			Process process = null;
			try {
				process = runtime.exec(cmd, null, this.dir);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			logger.info(cmd);
			return process;
		}
		
		@SuppressWarnings("unchecked")
		public <O extends AbstractCmd.Operation<T>> O clearParams(){
			params.clear();
			return (O) this;
		}
		
		@Override
		public abstract String toString();
		
	}
	
	public interface OperationType {
		String getOperName();
	}
	
}
