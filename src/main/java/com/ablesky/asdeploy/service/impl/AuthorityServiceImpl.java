package com.ablesky.asdeploy.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.ablesky.asdeploy.dao.IRoleDao;
import com.ablesky.asdeploy.dao.IUserDao;
import com.ablesky.asdeploy.dao.IUserRelRoleDao;
import com.ablesky.asdeploy.pojo.Role;
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
		List<UserRelRole> relList = userRelRoleDao.list(new ModelMap().addAttribute("user_id", userId));
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
		return roleDao.first(new ModelMap().addAttribute("name", name));
	}
	
	@Override
	public void saveOrUpdateUserRelRole(UserRelRole rel) {
		userRelRoleDao.saveOrUpdate(rel);
	}
	
	@Override
	public void deleteUserRelRoleByUserIdAndRoleName(Long userId, String roleName) {
		List<UserRelRole> relList = userRelRoleDao.list(new ModelMap()
				.addAttribute("user_id", userId)
				.addAttribute("role_name", roleName));
		for(UserRelRole rel: relList) {
			userRelRoleDao.delete(rel);
		}
	}
}
