package com.ablesky.asdeploy.dao.impl;

import java.util.List;

import org.apache.shiro.util.CollectionUtils;
import org.springframework.stereotype.Repository;

import com.ablesky.asdeploy.dao.IConflictInfoDao;
import com.ablesky.asdeploy.dao.base.AbstractDaoImpl;
import com.ablesky.asdeploy.pojo.ConflictInfo;

@Repository
public class ConflictInfoDaoImpl extends AbstractDaoImpl<ConflictInfo> implements IConflictInfoDao {

	@Override
	public void batchSave(List<ConflictInfo> list) {
		if(CollectionUtils.isEmpty(list)) {
			return;
		}
		StringBuilder sqlBuff = new StringBuilder("insert into conflict_info (conflict_patch_group_id, conflict_patch_file_id, related_patch_group_id) values ");
		for(ConflictInfo conflictInfo: list) {
			if(conflictInfo.getId() != null) {
				throw new IllegalStateException("ConflictInfo should not have id before save operation!");
			}
			sqlBuff
				.append("(")
				.append(conflictInfo.getPatchGroupId())
				.append(",")
				.append(conflictInfo.getPatchFile().getId())
				.append(",")
				.append(conflictInfo.getRelatedPatchGroupId())
				.append("),");
		}
		sqlBuff.deleteCharAt(sqlBuff.length() - 1);
		executeSql(sqlBuff.toString());
	}
}
