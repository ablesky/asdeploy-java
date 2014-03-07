package com.ablesky.asdeploy.util.cmd;

import java.io.File;
import java.util.Map;

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
			return cmdBuff.toString();
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
	
	public static void main(String[] args) {
		ShellCmd.ShellOperation exec = new ShellCmd().oper(ShellCmd.ShellOperationType.EXEC);
		CmdUtil.consume(exec.param("test.sh").exec(), new CmdCallback<String>() {
			@Override
			protected String process(String line, Map<String, String> resultMap) {
				System.out.println(line);
				return line;
			}
		});
	}
}
