package com.ablesky.asdeploy.dao.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;

import com.ablesky.asdeploy.dao.IAbstractDao;
import com.ablesky.asdeploy.util.CommonConstant;
import com.ablesky.asdeploy.util.Page;

public abstract class AbstractDaoImpl<E> implements IAbstractDao<E> {

	@Autowired
	private BasicHibernateDaoImpl basicHibernateDaoImpl;
	
	@SuppressWarnings("unchecked")
	private Class<E> entityClass = getSuperClassGenericType(this.getClass());
	private String entityClassName = entityClass.getSimpleName();
	
	@Override
	public void saveOrUpdate(E entity) {
		basicHibernateDaoImpl.saveOrUpdate(entity);
	}
	
	@Override
	public void delete(E entity) {
		basicHibernateDaoImpl.delete(entity);
	}
	
	@Override
	public E getById(Long id) {
		return basicHibernateDaoImpl.getById(entityClass, id);
	}
	
	@Override
	public List<E> list(int start, int limit, Map<String, Object> param) {
		return basicHibernateDaoImpl.list(start, limit, generateHqlByParam(param), param);
	}
	
	@Override
	public int count(String hql, Map<String, Object> param) {
		return basicHibernateDaoImpl.count(hql, param);
	}
	
	@Override
	public Page<E> paginate(int start, int limit, Map<String, Object> param) {
		return basicHibernateDaoImpl.paginate(start, limit, generateHqlByParam(param), param);
	}
	
	protected String generateHqlByParam(Map<String, Object> param) {
		return "from " + this.entityClassName + generateWhereByParam(param);
	}
	
	protected String generateWhereByParam(Map<String, Object> param) {
		StringBuilder buff = new StringBuilder(" where 1 = 1 ");
		for(Entry<String, Object> entry: param.entrySet()) {
			if(CommonConstant.ORDER_BY.equals(entry.getKey())) {
				continue;
			}
			buff.append(" and ")
				.append(entry.getKey())
				.append("=")
				.append(":").append(entry.getKey());
		}
		String orderBy = (String) param.get(CommonConstant.ORDER_DESC);
		if(orderBy != null) {
			buff.append(" order by " + 
					(CommonConstant.ORDER_DESC.equalsIgnoreCase(orderBy)
					? CommonConstant.ORDER_DESC
					: CommonConstant.ORDER_ASC)
			);
		}
		return buff.toString();
	}
	
	@SuppressWarnings("rawtypes")
	private static Class getSuperClassGenericType(Class<?> clazz){
		return getSuperClassGenericType(clazz, 0);
	}
	
	@SuppressWarnings("rawtypes")
	private static Class getSuperClassGenericType(Class clazz, int index){
		Type genType = clazz.getGenericSuperclass();
		if(!(genType instanceof ParameterizedType)){
			return Object.class;
		}
		Type[] params = ((ParameterizedType)genType).getActualTypeArguments();
		if(index < 0 || index >= params.length){
			return Object.class;
		}
		return (Class)params[index];
	}
}
