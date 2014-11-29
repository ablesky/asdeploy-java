package com.ablesky.asdeploy.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ablesky.asdeploy.dao.IRoleDao;
import com.ablesky.asdeploy.dao.IUserDao;
import com.ablesky.asdeploy.dao.IUserRelRoleDao;
import com.ablesky.asdeploy.dao.base.QueryParamMap;
import com.ablesky.asdeploy.pojo.Role;
import com.ablesky.asdeploy.pojo.User;
import com.ablesky.asdeploy.pojo.UserRelRole;
import com.ablesky.asdeploy.service.IAuthorityService;

/**
 * 处理用户，角色和权限的相关问题
 * @author zyang
 */
@Service
public class AuthorityServiceImpl implements IAuthorityService {

	@Autowired
	private IUserDao userDao;
	@Autowired
	private IRoleDao roleDao;
	@Autowired
	private IUserRelRoleDao userRelRoleDao;
	
	@Override
	public List<Role> getRoleListResultByUserId(Long userId) {
		List<UserRelRole> relList = userRelRoleDao.list(new QueryParamMap().addParam("user_id", userId));
		return new ArrayList<Role>(CollectionUtils.collect(relList, new Transformer<UserRelRole, Role>() {
			@Override
			public Role transform(UserRelRole rel) {
				return rel.getRole();
			}
		}));
	}
	
	@Override
	public List<UserRelRole> getUserRelRoleListResultByParam(int start, int limit, Map<String, Object> param) {
		return userRelRoleDao.list(start, limit, param);
	}
	
	@Override
	public UserRelRole getUserRelRoleByParam(Map<String, Object> param) {
		return userRelRoleDao.first(param);
	}
	
	@Override
	public Role getRoleByName(String name) {
		return roleDao.first(new QueryParamMap().addParam("name", name));
	}
	
	@Override
	public void saveOrUpdateUserRelRole(UserRelRole rel) {
		userRelRoleDao.saveOrUpdate(rel);
	}
	
	@Override
	public void saveOrUpdateRole(Role role) {
		roleDao.saveOrUpdate(role);
	}
	
	@Override
	public void deleteUserRelRoleByUserIdAndRoleName(Long userId, String roleName) {
		List<UserRelRole> relList = userRelRoleDao.list(new QueryParamMap()
				.addParam("user_id", userId)
				.addParam("role_name", roleName));
		for(UserRelRole rel: relList) {
			userRelRoleDao.delete(rel);
		}
	}
	
	/**
	 * 添加用户与角色的关联
	 */
	@Override
	public UserRelRole addUserRelRoleByUserAndRole(User user, Role role) {
		if(user == null || role == null) {
			return null;
		}
		UserRelRole rel = getUserRelRoleByParam(new QueryParamMap()
			.addParam("user_id", user.getId())
			.addParam("role_id", role.getId()));
		if(rel != null) {
			return rel;
		}
		rel = new UserRelRole(user, role);
		saveOrUpdateUserRelRole(rel);
		return rel;
	}

}
