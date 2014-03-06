package com.ablesky.asdeploy.dao.impl;

import java.util.List;

import org.apache.shiro.util.CollectionUtils;
import org.springframework.stereotype.Repository;

import com.ablesky.asdeploy.dao.IConflictDetailDao;
import com.ablesky.asdeploy.pojo.ConflictDetail;

@Repository
public class ConflictDetailDaoImpl extends AbstractDaoImpl<ConflictDetail> implements IConflictDetailDao {

	@Override
	public void batchSave(List<ConflictDetail> list) {
		if(CollectionUtils.isEmpty(list)) {
			return;
		}
		StringBuilder sqlBuff = new StringBuilder("insert into conflict_detail (deploy_record_id, conflict_info_id) values ");
		for(ConflictDetail conflictDetail: list) {
			if(conflictDetail.getId() != null) {
				throw new IllegalStateException("ConflictDetail should not have id before save operation!");
			}
			sqlBuff
				.append("(")
				.append(conflictDetail.getDeployRecordId())
				.append(",")
				.append(conflictDetail.getConflictInfoId())
				.append("),");
		}
		sqlBuff.deleteCharAt(sqlBuff.length() - 1);
		executeSql(sqlBuff.toString());
	}
}
