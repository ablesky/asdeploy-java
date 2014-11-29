package com.ablesky.asdeploy.dao.impl;

import java.util.List;

import org.apache.shiro.util.CollectionUtils;
import org.springframework.stereotype.Repository;

import com.ablesky.asdeploy.dao.IPatchFileRelGroupDao;
import com.ablesky.asdeploy.dao.base.AbstractDaoImpl;
import com.ablesky.asdeploy.pojo.PatchFileRelGroup;

@Repository
public class PatchFileRelGroupDaoImpl extends AbstractDaoImpl<PatchFileRelGroup> implements IPatchFileRelGroupDao {

	@Override
	public void batchSave(List<PatchFileRelGroup> list) {
		if(CollectionUtils.isEmpty(list)) {
			return;
		}
		StringBuilder sqlBuff = new StringBuilder("insert into patch_file_rel_group (patch_group_id, patch_file_id, create_time) values ");
		for(PatchFileRelGroup rel: list) {
			if(rel.getId() != null) {
				throw new IllegalStateException("PatchFileRelGroup should not have id before save operation!");
			}
			sqlBuff
				.append("(")
				.append(rel.getPatchGroupId())
				.append(",")
				.append(rel.getPatchFile().getId())
				.append(",")
				.append(rel.getCreateTime().getTime())
				.append("),");
		}
		sqlBuff.deleteCharAt(sqlBuff.length() - 1);
		executeSql(sqlBuff.toString());
	}
	
}
