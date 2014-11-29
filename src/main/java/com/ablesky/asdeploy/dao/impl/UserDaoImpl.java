package com.ablesky.asdeploy.dao.impl;

import org.springframework.stereotype.Repository;

import com.ablesky.asdeploy.dao.IUserDao;
import com.ablesky.asdeploy.dao.base.AbstractHibernateDaoImpl;
import com.ablesky.asdeploy.pojo.User;

@Repository
public class UserDaoImpl extends AbstractHibernateDaoImpl<User> implements IUserDao{

}
