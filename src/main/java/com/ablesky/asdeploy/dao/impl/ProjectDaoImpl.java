package com.ablesky.asdeploy.dao.impl;

import org.springframework.stereotype.Repository;

import com.ablesky.asdeploy.dao.IProjectDao;
import com.ablesky.asdeploy.dao.base.AbstractHibernateDaoImpl;
import com.ablesky.asdeploy.pojo.Project;

@Repository
public class ProjectDaoImpl extends AbstractHibernateDaoImpl<Project> implements IProjectDao {

}
