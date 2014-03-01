package com.ablesky.asdeploy.pojo;

import java.sql.Timestamp;

public class PatchFileRelGroup extends AbstractModel {

	private Long id;
	private PatchFile patchFile;
	private PatchGroup patchGroup;
	private Timestamp createTime;
	
	public PatchFileRelGroup() {}
	
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
	public PatchGroup getPatchGroup() {
		return patchGroup;
	}
	public void setPatchGroup(PatchGroup patchGroup) {
		this.patchGroup = patchGroup;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	
	
}
