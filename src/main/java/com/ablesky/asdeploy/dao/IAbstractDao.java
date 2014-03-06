package com.ablesky.asdeploy.dao;

import java.util.List;
import java.util.Map;

import com.ablesky.asdeploy.pojo.AbstractModel;
import com.ablesky.asdeploy.util.Page;

public interface IAbstractDao<E extends AbstractModel> {

	void saveOrUpdate(E entity);

	void delete(E entity);

	E getById(Long id);

	List<E> list(int start, int limit, Map<String, Object> param);

	List<E> list(Map<String, Object> param);
	
	int count(String hql, Map<String, Object> param);

	Page<E> paginate(int start, int limit, Map<String, Object> param);

	void deleteById(Long id);

	E first(Map<String, Object> param);

	E unique(Map<String, Object> param);

}
