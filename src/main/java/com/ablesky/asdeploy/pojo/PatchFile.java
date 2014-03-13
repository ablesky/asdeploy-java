package com.ablesky.asdeploy.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="patch_file")
public class PatchFile extends AbstractModel {

	@Id
	@GeneratedValue
	@Column
	private Long id;
	@Column(name="file_path")
	private String filePath;
	@Column(name="project_id")
	private Long projectId;
	
	public PatchFile() {}
	
	public PatchFile(Long projectId, String filePath) {
		this.projectId = projectId;
		this.filePath = filePath;
	}
	
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
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	
}
