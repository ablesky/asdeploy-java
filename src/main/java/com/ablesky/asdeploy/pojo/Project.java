package com.ablesky.asdeploy.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="project")
public class Project extends AbstractModel {

	@Id
	@GeneratedValue
	@Column
	private Long id;
	@Column
	private String name;
	// warName是工程在tomcat中实际部署时ableskyapps下的目录名称
	// 此字段的值需要正确设置，否则无法正常发布war包
	@Column(name="war_name")
	private String warName;
	// 旧工程和新工程在发war包时使用的脚本有区别
	// 从考试系统开始，统一使用新脚本，此处类型参数为1
	// 以前的旧工程使用的脚本，此处类型参数为0
	@Column(name="deploy_script_type")
	private Integer deployScriptType;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getWarName() {
		return warName;
	}
	public void setWarName(String warName) {
		this.warName = warName;
	}
	public Integer getDeployScriptType() {
		return deployScriptType;
	}
	public void setDeployScriptType(Integer deployScriptType) {
		this.deployScriptType = deployScriptType;
	}
	
}
