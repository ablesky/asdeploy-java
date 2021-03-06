package com.ablesky.asdeploy.dao.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.stereotype.Repository;

import com.ablesky.asdeploy.dao.IPatchFileDao;
import com.ablesky.asdeploy.dao.base.AbstractHibernateDaoImpl;
import com.ablesky.asdeploy.pojo.PatchFile;

@Repository
public class PatchFileDaoImpl extends AbstractHibernateDaoImpl<PatchFile> implements IPatchFileDao {

	@Override
	public void batchSave(List<PatchFile> list) {
		if(CollectionUtils.isEmpty(list)) {
			return;
		}
		StringBuilder sqlBuff = new StringBuilder("insert into patch_file (project_id, file_path) values ");
		for(PatchFile patchFile: list) {
			if(patchFile.getId() != null) {
				throw new IllegalStateException("ConflictDetail should not have id before save operation!");
			}
			sqlBuff
				.append("(")
				.append(patchFile.getProjectId())
				.append(",")
				.append("'").append(StringUtils.replace(patchFile.getFilePath(), "'", "''")).append("'")
				.append("),");
		}
		sqlBuff.deleteCharAt(sqlBuff.length() - 1);
		executeSql(sqlBuff.toString());
	}
}
