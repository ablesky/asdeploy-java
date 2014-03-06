package com.ablesky.asdeploy.dao;

import java.util.List;

import com.ablesky.asdeploy.pojo.ConflictInfo;

public interface IConflictInfoDao extends IAbstractDao<ConflictInfo> {

	void batchSave(List<ConflictInfo> list);

}
