package com.ablesky.asdeploy.pojo;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="deploy_record")
public class DeployRecord extends AbstractModel {
	
	public static final String STATUS_PREPARE = "prepare";
	public static final String STATUS_UPLOADED = "uploaded";
	public static final String STATUS_DEPLOYING = "deploying";
	public static final String STATUS_DEPLOY_SUCCESS = "deploy_success";
	public static final String STATUS_DEPLOY_FAILURE = "deploy_failure";
	public static final String STATUS_ROLLBACKING = "rollbacking";
	public static final String STATUS_ROLLBACK_SUCCESS = "rollback_success";
	public static final String STATUS_ROLLBACK_FAILURE = "rollback_failure";

	@Id
	@GeneratedValue
	@Column
	private Long id;
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
	@ManyToOne
	@JoinColumn(name="project_id")
	private Project project;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="deploy_item_id")
	private DeployItem deployItem;
	@Column
	private String status;
	@Column(name="create_time")
	private Timestamp createTime;
	@Column(name="is_conflict_with_others")
	private Boolean isConflictWithOthers;
	
	public DeployRecord() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public DeployItem getDeployItem() {
		return deployItem;
	}

	public void setDeployItem(DeployItem deployItem) {
		this.deployItem = deployItem;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Boolean getIsConflictWithOthers() {
		return isConflictWithOthers;
	}

	public void setIsConflictWithOthers(Boolean isConflictWithOthers) {
		this.isConflictWithOthers = isConflictWithOthers;
	}
	
}
