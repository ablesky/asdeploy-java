package com.ablesky.asdeploy.util.cmd;

import java.io.File;

import org.apache.commons.lang3.SystemUtils;


public class AblejsCmd extends AbstractCmd<AblejsCmd.AblejsOperation, AblejsCmd.AblejsOperationType> {

	public AblejsCmd() {
		super(new File(SystemUtils.IS_OS_WINDOWS? "c:/": "/home"));
	}
	
	public AblejsCmd(File dir) {
		super(dir);
	}
	
	public AblejsOperation path() {
		return oper(AblejsOperationType.PATH);
	}
	
	public AblejsOperation fingerprint() {
		return oper(AblejsOperationType.FINGERPRINT);
	}

	public static class AblejsOperation extends AbstractCmd.Operation<AblejsOperationType> {

		public AblejsOperation(File dir, AblejsOperationType operType) {
			super(dir, operType);
		}

		@Override
		public String toString() {
			StringBuilder cmdBuff = new StringBuilder()
				.append("ablejs").append(" ")
				.append("--cli").append(" ")
				.append(operType.getOperName());
			for(String param: params.keySet()) {
				cmdBuff.append(" ").append(param);
			}
			return cmdBuff.toString();
		}
		
	}
	
	public enum AblejsOperationType implements AbstractCmd.OperationType {
		
		PATH("--filemap-path"), FINGERPRINT("--filemap-fingerprint");
		
		private String option;
		
		private AblejsOperationType(String option) {
			this.option = option;
		}

		@Override
		public String getOperName() {
			return option;
		}
	}
}
