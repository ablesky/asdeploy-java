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
@Table(name="deploy_lock")
public class DeployLock extends AbstractModel {
	
	public static final long LOCK_EXPIRY_TIME = 2 * 3600 * 1000; 

	@Id
	@GeneratedValue
	@Column
	private Long id;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User user;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="deploy_record_id")
	private DeployRecord deployRecord;
	@Column(name="is_locked")
	private Boolean isLocked;
	@Column(name="locked_time")
	private Timestamp lockedTime;
	
	public DeployLock() {}
	
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
	public DeployRecord getDeployRecord() {
		return deployRecord;
	}
	public void setDeployRecord(DeployRecord deployRecord) {
		this.deployRecord = deployRecord;
	}
	public Boolean getIsLocked() {
		return isLocked;
	}
	public void setIsLocked(Boolean isLocked) {
		this.isLocked = isLocked;
	}
	public Timestamp getLockedTime() {
		return lockedTime;
	}
	public void setLockedTime(Timestamp lockedTime) {
		this.lockedTime = lockedTime;
	}
	
}
