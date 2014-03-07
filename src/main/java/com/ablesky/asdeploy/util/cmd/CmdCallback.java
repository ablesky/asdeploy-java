package com.ablesky.asdeploy.util.cmd;

import java.util.Map;

public abstract class CmdCallback<T> {

	protected abstract T process(String line, Map<String, T> resultMap);
	
	protected void afterProcess(Map<String, T> resultMap) {
		// defaultly do nothing
	}
	
}
