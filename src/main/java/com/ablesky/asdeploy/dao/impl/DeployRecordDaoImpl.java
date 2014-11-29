package com.ablesky.asdeploy.dao.impl;

import org.springframework.stereotype.Repository;

import com.ablesky.asdeploy.dao.IDeployRecordDao;
import com.ablesky.asdeploy.dao.base.AbstractHibernateDaoImpl;
import com.ablesky.asdeploy.pojo.DeployRecord;

@Repository
public class DeployRecordDaoImpl extends AbstractHibernateDaoImpl<DeployRecord> implements IDeployRecordDao {

}
