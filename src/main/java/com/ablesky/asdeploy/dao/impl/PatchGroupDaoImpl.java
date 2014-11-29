package com.ablesky.asdeploy.dao.impl;

import org.springframework.stereotype.Repository;

import com.ablesky.asdeploy.dao.IPatchGroupDao;
import com.ablesky.asdeploy.dao.base.AbstractHibernateDaoImpl;
import com.ablesky.asdeploy.pojo.PatchGroup;

@Repository
public class PatchGroupDaoImpl extends AbstractHibernateDaoImpl<PatchGroup> implements IPatchGroupDao {

}
