package com.ablesky.asdeploy.service;

import java.util.List;

import com.ablesky.asdeploy.pojo.Role;

public interface IAuthorityService {

	List<Role> getRoleListResultByUserId(Long userId);

}
