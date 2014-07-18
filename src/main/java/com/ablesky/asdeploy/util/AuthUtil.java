package com.ablesky.asdeploy.util;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.DefaultHashService;
import org.apache.shiro.crypto.hash.HashRequest;
import org.apache.shiro.crypto.hash.HashService;
import org.apache.shiro.crypto.hash.SimpleHashRequest;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.util.SimpleByteSource;

import com.ablesky.asdeploy.pojo.Role;
import com.ablesky.asdeploy.pojo.User;
import com.ablesky.asdeploy.security.ShiroDbRealm;

public class AuthUtil {
	
	private AuthUtil() {}
	
	public static Subject getCurrentSubject() {
		return SecurityUtils.getSubject();
	}
	
	public static boolean isAuthenticated() {
		return getCurrentSubject().isAuthenticated();
	}
	
	public static boolean isRemembered() {
		return getCurrentSubject().isRemembered();
	}
	
	public static boolean isUser() {
		return isAuthenticated() || isRemembered();
	}
	
	public static boolean isGuest() {
		return !isUser();
	}
	
	public static String getCurrentUsername() {
		return (String) getCurrentSubject().getPrincipal();
	}
	
	public static User getCurrentUser() {
		Collection<?> shiroDbPrincipals = getCurrentSubject().getPrincipals().fromRealm(ShiroDbRealm.DB_REALM_NAME);
		for(Object obj: shiroDbPrincipals) {
			if(obj instanceof User) {
				return (User) obj;
			}
		}
		return null;
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
		return getCurrentSubject().hasRole(Role.NAME_SUPER_ADMIN);
	}
	
	/**
	 * 对公盐私盐这套东西不怎么理解，先暂时这样写上吧
	 */
	public static String hashPassword(String username, String password) {
		if(StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
			throw new IllegalArgumentException("Neither username nor password should be null!");
		}
		HashService hashService = new DefaultHashService();
		HashRequest hashRequest = new SimpleHashRequest("MD5", new SimpleByteSource(password), new SimpleByteSource(username), 3);
		return hashService.computeHash(hashRequest).toHex();
	}

}
