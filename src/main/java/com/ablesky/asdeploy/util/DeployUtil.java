package com.ablesky.asdeploy.util;

public class DeployUtil {

	private DeployUtil() {}
	
	public static String getDeployItemUploadFolder(String projectName, String version) {
		return DeployConfiguration.getInstance().getItemRootPath() + projectName + "-" + version + "/";
	}
}
