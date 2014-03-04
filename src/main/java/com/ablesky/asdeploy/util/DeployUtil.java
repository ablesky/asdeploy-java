package com.ablesky.asdeploy.util;

public class DeployUtil {
	
	private static final DeployConfiguration CONFIG = DeployConfiguration.getInstance();

	private DeployUtil() {}
	
	public static String getDeployItemUploadFolder(String projectName, String version) {
		return CONFIG.getItemRootPath() + projectName + "-" + version + "/";
	}
}
