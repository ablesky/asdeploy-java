package com.ablesky.asdeploy.pojo;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="patch_file_rel_group")
public class PatchFileRelGroup extends AbstractModel {

	@Id
	@GeneratedValue
	@Column
	private Long id;
	@ManyToOne
	@JoinColumn(name="patch_file_id")
	private PatchFile patchFile;
	@Column(name="patch_group_id")
	private Long patchGroupId;
	@Column(name="create_time")
	private Timestamp createTime;
	@Transient
	private PatchGroup patchGroup;
	
	public PatchFileRelGroup() {}
	
	public PatchFileRelGroup(Long patchGroupId, PatchFile patchFile, Timestamp createTime) {
		this.patchGroupId = patchGroupId;
		this.patchFile = patchFile;
		this.createTime = createTime;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public PatchFile getPatchFile() {
		return patchFile;
	}
	public void setPatchFile(PatchFile patchFile) {
		this.patchFile = patchFile;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public Long getPatchGroupId() {
		return patchGroupId;
	}
	public void setPatchGroupId(Long patchGroupId) {
		this.patchGroupId = patchGroupId;
	}
	public PatchGroup getPatchGroup() {
		return patchGroup;
	}
	public void setPatchGroup(PatchGroup patchGroup) {
		this.patchGroup = patchGroup;
	}
	
}
