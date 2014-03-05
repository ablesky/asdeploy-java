package com.ablesky.asdeploy.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="conflict_info")
public class ConflictInfo extends AbstractModel {

	@Id
	@GeneratedValue
	@Column
	private Long id;
	@Column(name="conflict_patch_group_id")
	private Long conflictPatchGroupId;
	@ManyToOne
	@JoinColumn(name="patch_file_id")
	private PatchFile patchFile;
	@Transient
	private PatchGroup patchGroup;
	
	public ConflictInfo() {}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getConflictPatchGroupId() {
		return conflictPatchGroupId;
	}
	public void setConflictPatchGroupId(Long conflictPatchGroupId) {
		this.conflictPatchGroupId = conflictPatchGroupId;
	}
	public PatchFile getPatchFile() {
		return patchFile;
	}
	public void setPatchFile(PatchFile patchFile) {
		this.patchFile = patchFile;
	}
	public PatchGroup getPatchGroup() {
		return patchGroup;
	}
	public void setPatchGroup(PatchGroup patchGroup) {
		this.patchGroup = patchGroup;
	}
	
}
