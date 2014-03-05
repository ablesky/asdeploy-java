package com.ablesky.asdeploy.pojo;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="deploy_item")
public class DeployItem extends AbstractModel {
	
	public static String DEPLOY_TYPE_PATCH = "patch";
	public static String DEPLOY_TYPE_WAR = "war";

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
	@ManyToOne
	@JoinColumn(name="patch_group_id")
	private PatchGroup patchGroup;
	@Column
	private String version;
	@Column(name="deploy_type")
	private String deployType;
	@Column(name="file_name")
	private String fileName;
	@Column(name="folder_path")
	private String folderPath;
	@Column(name="create_time")
	private Timestamp createTime;
	@Column(name="update_time")
	private Timestamp updateTime;
	
	public DeployItem() {}
	
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
	public PatchGroup getPatchGroup() {
		return patchGroup;
	}
	public void setPatchGroup(PatchGroup patchGroup) {
		this.patchGroup = patchGroup;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getDeployType() {
		return deployType;
	}
	public void setDeployType(String deployType) {
		this.deployType = deployType;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFolderPath() {
		return folderPath;
	}
	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public Timestamp getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}
	
}
