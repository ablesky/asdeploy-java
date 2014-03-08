package com.ablesky.asdeploy.service;

import java.util.List;
import java.util.Map;

import com.ablesky.asdeploy.pojo.User;
import com.ablesky.asdeploy.util.Page;

public interface IUserService {

	void saveOrUpdateUser(User user);

	void deleteUser(User user);

	User getUserById(Long id);

	Page<User> getUserPageResult(int start, int limit, Map<String, Object> param);

	User getUserByUsername(String username);

	List<User> getUserListResult(int start, int limit, Map<String, Object> param);

	void createNewUser(String username, String rawPassword);

}
