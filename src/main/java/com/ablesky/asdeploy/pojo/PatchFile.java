package com.ablesky.asdeploy.pojo;

import javax.persistence.Entity;
import javax.persistence.Table;

//@Entity
//@Table(name="patch_file")
public class PatchFile extends AbstractModel {

	private Long id;
	private String filePath;
	private String fileType;
	
	public PatchFile() {}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	
}
