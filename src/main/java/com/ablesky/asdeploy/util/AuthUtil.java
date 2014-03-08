package com.ablesky.asdeploy.util;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import com.ablesky.asdeploy.pojo.User;

public class AuthUtil {
	
	private AuthUtil() {}
	
	public static Subject getCurrentSubject() {
		return SecurityUtils.getSubject();
	}
	
	/**
	 * 判断是否已登录
	 */
	public static boolean isAuthenticated() {
		return getCurrentSubject().isAuthenticated();
	}
	
	public static String getCurrentUsername() {
		return (String) getCurrentSubject().getPrincipal();
	}
	
	public static User getCurrentUser() {
		return getCurrentSubject().getPrincipals().byType(User.class).iterator().next();
	}
	
	public static void login(String username, String password) {
		login(username, password, true);
	}
	
	public static void login(String username, String password, boolean rememberMe) {
		UsernamePasswordToken token = new UsernamePasswordToken(username, password);
		token.setRememberMe(rememberMe);
		getCurrentSubject().login(token);
	}
	
	public static void logout() {
		getCurrentSubject().logout();
	}
	
	public static boolean isSuperAdmin() {
		return "zyang".equals(getCurrentUsername());
	}

}
