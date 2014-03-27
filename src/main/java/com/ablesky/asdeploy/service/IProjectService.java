package com.ablesky.asdeploy.service;

import java.util.List;
import java.util.Map;

import com.ablesky.asdeploy.pojo.Project;

public interface IProjectService {

	void saveOrUpdateProject(Project project);

	Project getProjectById(Long id);

	void deleteProjectById(Long id);

	List<Project> getProjectListResult(int start, int limit, Map<String, Object> param);

	List<Project> getProjectListResult();

}
