package com.ablesky.asdeploy.dao;

import java.util.List;

import com.ablesky.asdeploy.pojo.PatchFileRelGroup;

public interface IPatchFileRelGroupDao extends IAbstractDao<PatchFileRelGroup> {

	void batchSave(List<PatchFileRelGroup> list);

}
