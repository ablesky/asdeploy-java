package com.ablesky.asdeploy.util.cmd;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.SystemUtils;

public class ShellCmd extends AbstractCmd<ShellCmd.ShellOperation, ShellCmd.ShellOperationType> {
	
	public ShellCmd() {
		// windows下仅用于测试，需要配个sh命令到环境变量里
		super(new File(SystemUtils.IS_OS_WINDOWS? "c:/": "/home"));
	}
	
	public ShellCmd(File dir) {
		super(dir);
	}

	public static class ShellOperation extends AbstractCmd.Operation<ShellOperationType> {

		public ShellOperation(File dir, ShellOperationType operType) {
			super(dir, operType);
		}

		@Override
		public String toString() {
			StringBuilder cmdBuff  = new StringBuilder()
				.append("sh").append(" ").append(operType.getOperName());
			for(String param: params.keySet()) {
				cmdBuff.append(" ").append(param);
			}
			cmdBuff.append(" ;");
			return cmdBuff.toString();
		}
		
		public Process exec(String cmd){
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
	}
	
	public enum ShellOperationType implements AbstractCmd.OperationType {
		
		EXEC(""), COMMAND("-c");
		
		private String option;
		
		private ShellOperationType(String option) {
			this.option = option;
		}
		
		@Override
		public String getOperName() {
			return option;
		}
		
	}
	
}
