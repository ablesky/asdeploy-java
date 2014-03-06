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
	// 当前补丁组
	@Column(name="conflict_patch_group_id")
	private Long patchGroupId;
	@Transient
	private PatchGroup patchGroup;
	// 冲突文件信息
	@ManyToOne
	@JoinColumn(name="patch_file_id")
	private PatchFile patchFile;
	// 与其他的哪些补丁组冲突
	@Column(name="related_patch_group_id")
	private Long relatedPatchGroupId;
	@Transient
	private PatchGroup relatedPatchGroup;
	
	public ConflictInfo() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public PatchFile getPatchFile() {
		return patchFile;
	}

	public void setPatchFile(PatchFile patchFile) {
		this.patchFile = patchFile;
	}

	public Long getRelatedPatchGroupId() {
		return relatedPatchGroupId;
	}

	public void setRelatedPatchGroupId(Long relatedPatchGroupId) {
		this.relatedPatchGroupId = relatedPatchGroupId;
	}

	public PatchGroup getRelatedPatchGroup() {
		return relatedPatchGroup;
	}

	public void setRelatedPatchGroup(PatchGroup relatedPatchGroup) {
		this.relatedPatchGroup = relatedPatchGroup;
	}
	
	
}
