package com.ablesky.asdeploy.dao.impl;

import org.springframework.stereotype.Repository;

import com.ablesky.asdeploy.dao.IRoleDao;
import com.ablesky.asdeploy.dao.base.AbstractHibernateDaoImpl;
import com.ablesky.asdeploy.pojo.Role;

@Repository
public class RoleDaoImpl extends AbstractHibernateDaoImpl<Role> implements IRoleDao {

}
