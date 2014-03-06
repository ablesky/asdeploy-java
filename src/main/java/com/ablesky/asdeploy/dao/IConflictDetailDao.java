package com.ablesky.asdeploy.dao;

import java.util.List;

import com.ablesky.asdeploy.pojo.ConflictDetail;

public interface IConflictDetailDao extends IAbstractDao<ConflictDetail> {

	void batchSave(List<ConflictDetail> list);

}
