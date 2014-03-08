package com.ablesky.asdeploy.pojo;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="user")
public class User extends AbstractModel implements Serializable {
	// 注意，如果准备将user写入authenticationInfo的话，则user必须实现Serializable接口
	// 否则无法使用rememberMe功能(生成rememberMe的cookie时，需要对authenticationInfo中的信息进行序列化)
	private static final long serialVersionUID = 8647343192652652694L;
	
	@Id
	@GeneratedValue
	@Column
	private Long id;
	@Column
	private String username;
	@Column
	private String password;
	@Column(name="create_time")
	private Timestamp createTime;
	@Column(name="update_time")
	private Timestamp updateTime;
	
	public User() {}
	
	public User(String username, String password, Timestamp createTime, Timestamp updateTime) {
		this.username = username;
		this.password = password;
		this.createTime = createTime;
		this.updateTime = updateTime;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
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
