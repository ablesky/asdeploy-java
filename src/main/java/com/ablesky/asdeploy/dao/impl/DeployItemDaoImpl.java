package com.ablesky.asdeploy.dao.impl;

import org.springframework.stereotype.Repository;

import com.ablesky.asdeploy.dao.IDeployItemDao;
import com.ablesky.asdeploy.dao.base.AbstractHibernateDaoImpl;
import com.ablesky.asdeploy.pojo.DeployItem;

@Repository
public class DeployItemDaoImpl extends AbstractHibernateDaoImpl<DeployItem> implements IDeployItemDao {

}
