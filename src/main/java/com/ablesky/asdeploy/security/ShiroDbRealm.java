package com.ablesky.asdeploy.security;


import java.util.List;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.SimpleByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.ablesky.asdeploy.pojo.Role;
import com.ablesky.asdeploy.pojo.User;
import com.ablesky.asdeploy.service.IAuthorityService;
import com.ablesky.asdeploy.service.IUserService;

public class ShiroDbRealm extends AuthorizingRealm {
	
	public static final String DB_REALM_NAME = "shiro_db_realm";
	
	@Autowired
	@Override
	public void  setCredentialsMatcher(CredentialsMatcher credentialsMatcher) {
		super.setCredentialsMatcher(credentialsMatcher);
	}

	@Autowired
	private IUserService userService;
	@Autowired
	private IAuthorityService authorityService;
	
	public ShiroDbRealm() {
		super();
		setName(DB_REALM_NAME);
	}
	
	/**
	 * 认证回调函数,登录时调用.
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
		UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
		User user = userService.getUserByUsername(token.getUsername());
		if (user == null) {
			return null;
		}
		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user.getUsername(), user.getPassword(), getName());
		// 以username作为salt，之后password会被MD5迭代3次
		info.setCredentialsSalt(new SimpleByteSource(user.getUsername()));
		SimplePrincipalCollection principals = (SimplePrincipalCollection) info.getPrincipals();
		principals.add(user, getName());
		return info;
	}

	/**
	 * 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用.
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		User user = principals.oneByType(User.class);
		if(user != null) {
			List<Role> roleList = authorityService.getRoleListResultByUserId(user.getId());
			for(Role role: roleList) {
				info.addRole(role.getName());
			}
		}
		return info;
	}

}
