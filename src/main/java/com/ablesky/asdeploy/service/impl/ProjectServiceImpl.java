package com.ablesky.asdeploy.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ablesky.asdeploy.dao.IProjectDao;
import com.ablesky.asdeploy.dao.base.QueryParamMap;
import com.ablesky.asdeploy.pojo.Project;
import com.ablesky.asdeploy.service.IProjectService;

@Service
public class ProjectServiceImpl implements IProjectService {

	@Autowired
	private IProjectDao projectDao;
	
	@Override
	public void saveOrUpdateProject(Project project) {
		projectDao.saveOrUpdate(project);
	}
	
	@Override
	public Project getProjectById(Long id) {
		return projectDao.getById(id);
	}
	
	@Override
	public void deleteProjectById(Long id) {
		projectDao.deleteById(id);
	}
	
	@Override
	public List<Project> getProjectListResult(int start, int limit, Map<String, Object> param) {
		return projectDao.list(start, limit, param);
	}
	
	@Override
	public List<Project> getProjectListResult() {
		return projectDao.list(QueryParamMap.EMPTY_MAP);
	}
	
}
