package com.ablesky.asdeploy.util;


public class DeployConfiguration {
	
	public static final DeployConfiguration INSTANCE = new DeployConfiguration();
	
	public static DeployConfiguration getInstance() {
		return DeployConfiguration.INSTANCE;
	}
	
	// 版本
	private String version = "1.4";
	// 根路径
	private String rootPath = "/d/content/web-app-bak/";
	// 上传文件根路径
	private String itemRootPath = rootPath + "ableskyapps/";
	// 脚本根路径
	private String scriptRootPath = rootPath + "deployment/";
	// 服务器的hostname
	private String hostname;
	// 环境名称
	private String environment;
	
	public DeployConfiguration() {
		this.hostname = "localhost";
		this.environment = "LOCALHOST";
	}

	public String getVersion() {
		return version;
	}

	public String getRootPath() {
		return rootPath;
	}

	public String getItemRootPath() {
		return itemRootPath;
	}

	public String getScriptRootPath() {
		return scriptRootPath;
	}

	public String getHostname() {
		return hostname;
	}

	public String getEnvironment() {
		return environment;
	}
	
	
}
