package com.ablesky.asdeploy.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name="patch_file")
public class PatchFile extends AbstractModel {

	// 已经记不清fileType还有没有用了
	public static final String FILE_TYPE_DYNAMIC = "dynamic";
	public static final String FILE_TYPE_STATIC = "static";
	
	@Id
	@GeneratedValue
	@Column
	private Long id;
	@Column(name="file_path")
	private String filePath;
	@Column(name="file_type")
	private String fileType;
	@ManyToOne
	@JoinColumn(name="project_id")
	private Project project;
	
	public PatchFile() {}
	
	public PatchFile(Project project, String filePath) {
		this.project = project;
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
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
	
}
