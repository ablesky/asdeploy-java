package com.ablesky.asdeploy.dao;

import java.util.List;

import com.ablesky.asdeploy.pojo.PatchFile;

public interface IPatchFileDao extends IAbstractDao<PatchFile> {

	void batchSave(List<PatchFile> list);

}
