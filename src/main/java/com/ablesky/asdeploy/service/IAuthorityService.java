package com.ablesky.asdeploy.service;

import java.util.List;
import java.util.Map;

import com.ablesky.asdeploy.pojo.Role;
import com.ablesky.asdeploy.pojo.User;
import com.ablesky.asdeploy.pojo.UserRelRole;

public interface IAuthorityService {

	List<Role> getRoleListResultByUserId(Long userId);

	List<UserRelRole> getUserRelRoleListResultByParam(int start, int limit, Map<String, Object> param);

	UserRelRole getUserRelRoleByParam(Map<String, Object> param);

	Role getRoleByName(String name);

	void saveOrUpdateUserRelRole(UserRelRole rel);

	void deleteUserRelRoleByUserIdAndRoleName(Long userId, String roleName);

	UserRelRole addUserRelRoleByUserAndRole(User user, Role role);

	void saveOrUpdateRole(Role role);

}
