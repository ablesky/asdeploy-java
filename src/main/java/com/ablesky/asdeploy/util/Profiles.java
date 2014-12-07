package com.ablesky.asdeploy.util;

/**
 * Spring profile 常用方法与profile名称。
 * 
 * @author calvin
 */
public class Profiles {

	public static final String ACTIVE_PROFILE = "spring.profiles.active";
	public static final String DEFAULT_PROFILE = "spring.profiles.default";

	public static final String PRODUCTION = "production";
	public static final String DEVELOPMENT = "development";
	public static final String DEVELOPMENT_INIT = "development-init";
	public static final String UNIT_TEST = "test";
	public static final String FUNCTIONAL_TEST = "functional";

	/**
	 * 在Spring启动前，设置profile的环境变量。
	 */
	public static void setProfileAsSystemProperty(String profile) {
		System.setProperty(ACTIVE_PROFILE, profile);
	}
	
	public static String getProfile() {
		String profile = System.getProperty(ACTIVE_PROFILE);
		if(profile == null) {
			profile = System.getProperty(DEFAULT_PROFILE);
		}
		return profile;
	}
	
	public static boolean isProfile(String profile) {
		return profile.equals(getProfile());
	}
	
	public static boolean isDevelopment() {
		return isProfile(DEVELOPMENT);
	}
	
	public static boolean isProduction() {
		return isProfile(PRODUCTION);
	}
}
