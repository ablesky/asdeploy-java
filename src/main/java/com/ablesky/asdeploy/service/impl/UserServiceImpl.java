package com.ablesky.asdeploy.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ablesky.asdeploy.dao.IUserDao;
import com.ablesky.asdeploy.pojo.User;
import com.ablesky.asdeploy.service.IUserService;
import com.ablesky.asdeploy.util.Page;

@Service
public class UserServiceImpl implements IUserService {

	@Autowired
	private IUserDao userDao;
	
	@Override
	public void saveOrUpdateUser(User user) {
		userDao.saveOrUpdate(user);
	}
	
	@Override
	public void deleteUser(User user) {
		userDao.delete(user);
	}
	
	@Override
	public User getUserById(Long id) {
		return userDao.getById(id);
	}
	
	@Override
	public User getUserByUsername(String username) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("username", username);
		List<User> list = userDao.list(0, 1, param);
		return CollectionUtils.isEmpty(list)? null: list.get(0);
	}
	
	@Override
	public Page<User> getUserPageResult(int start, int limit, Map<String, Object> param) {
		return userDao.paginate(start, limit, param);
	}
	
}
