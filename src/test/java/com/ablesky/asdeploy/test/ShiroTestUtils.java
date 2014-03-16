/**
 * Copyright (c) 2005-2012 springside.org.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.ablesky.asdeploy.test;

import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.SubjectThreadState;
import org.apache.shiro.util.ThreadState;
import org.mockito.Mockito;

import com.ablesky.asdeploy.pojo.Role;
import com.ablesky.asdeploy.pojo.User;

/**
 * 在单元测试中的Shiro工具类，
 * 
 * @author calvin
 */
public class ShiroTestUtils {

	private static ThreadState threadState;

	/**
	 * 用Mockito快速創建一個已認證的用户.
	 * @deprecated 这个是springside原生的，并不适用于当前工程
	 */
	public static void mockSubject(Object principal) {
		Subject subject = Mockito.mock(Subject.class);
		Mockito.when(subject.isAuthenticated()).thenReturn(true);
		Mockito.when(subject.getPrincipal()).thenReturn(principal);
		bindSubject(subject);
	}
	
	public static void mockCurrentUser(User user, boolean isSuperAdmin) {
		Subject subject = Mockito.mock(Subject.class);
		Mockito.when(subject.isAuthenticated()).thenReturn(true);
		Mockito.when(subject.getPrincipal()).thenReturn(user.getUsername());
		SimplePrincipalCollection principals = new SimplePrincipalCollection(user.getUsername(), "testRealm");
		principals.add(user, "testRealm");
		Mockito.when(subject.getPrincipals()).thenReturn(principals);
		Mockito.when(subject.hasRole(Role.NAME_SUPER_ADMIN)).thenReturn(isSuperAdmin);
		bindSubject(subject);
	}

	/**
	 * 綁定Subject到當前線程.
	 */
	protected static void bindSubject(Subject subject) {
		clearSubject();
		threadState = new SubjectThreadState(subject);
		threadState.bind();
	}

	/**
	 * 清除當前線程中的Subject.
	 */
	public static void clearSubject() {
		if (threadState != null) {
			threadState.clear();
			threadState = null;
		}
	}
}
