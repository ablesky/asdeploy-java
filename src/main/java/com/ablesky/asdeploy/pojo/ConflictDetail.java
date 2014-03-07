package com.ablesky.asdeploy.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * ConflictDetail用于在DeployRecord的详情页显示当次发布的冲突情况
 * @author zyang
 */
@Entity
@Table(name="conflict_detail")
public class ConflictDetail extends AbstractModel {
	
	@Id
	@GeneratedValue
	@Column
	private Long id;
	@Column(name="deploy_record_id")
	private Long deployRecordId;
	@Column(name="conflict_info_id")
	private Long conflictInfoId;
	
	@Transient
	private ConflictInfo conflictInfo;
	
	public ConflictDetail() {}
	
	public ConflictDetail(Long deployRecordId, Long conflictInfoId) {
		this.deployRecordId = deployRecordId;
		this.conflictInfoId = conflictInfoId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getDeployRecordId() {
		return deployRecordId;
	}

	public void setDeployRecordId(Long deployRecordId) {
		this.deployRecordId = deployRecordId;
	}

	public Long getConflictInfoId() {
		return conflictInfoId;
	}

	public void setConflictInfoId(Long conflictInfoId) {
		this.conflictInfoId = conflictInfoId;
	}

	public ConflictInfo getConflictInfo() {
		return conflictInfo;
	}

	public void setConflictInfo(ConflictInfo conflictInfo) {
		this.conflictInfo = conflictInfo;
	}
	
	
}
