package com.ablesky.asdeploy.dto;

import com.ablesky.asdeploy.pojo.ConflictInfo;
import com.ablesky.asdeploy.pojo.PatchFileRelGroup;

public class ConflictInfoDto {

	private Long id;
	private Long patchGroupId;
	private String patchGroupName;
	private String filePath;
	
	public ConflictInfoDto() {}
	
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
	public String getPatchGroupName() {
		return patchGroupName;
	}
	public void setPatchGroupName(String patchGroupName) {
		this.patchGroupName = patchGroupName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	/**
	 * 发布前解压缩补丁时，尚未保存conflictInfo，所以页面显示的冲突信息只能根据patchFileRelGroup来取
	 */
	public ConflictInfoDto fillDto(PatchFileRelGroup conflictRel) {
		patchGroupId = conflictRel.getPatchGroupId();
		if(conflictRel.getPatchGroup() != null) {
			patchGroupName = conflictRel.getPatchGroup().getName();
		}
		filePath = conflictRel.getPatchFile().getFilePath();
		return this;
	}
	
	/**
	 * conflictInfo是发布开始时进行记录的，用于后续patchGroup详情页面
	 */
	public ConflictInfoDto fillDto(ConflictInfo conflictInfo) {
		id = conflictInfo.getId();
		patchGroupId = conflictInfo.getConflictPatchGroupId();
		if(conflictInfo.getPatchGroup() != null) {
			patchGroupName = conflictInfo.getPatchGroup().getName();
		}
		filePath = conflictInfo.getPatchFile().getFilePath();
		return this;
	}
	
	
	
}
