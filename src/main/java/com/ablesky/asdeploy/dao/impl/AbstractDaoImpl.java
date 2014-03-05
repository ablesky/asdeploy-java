package com.ablesky.asdeploy.dao.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.ablesky.asdeploy.dao.IAbstractDao;
import com.ablesky.asdeploy.pojo.AbstractModel;
import com.ablesky.asdeploy.util.CommonConstant;
import com.ablesky.asdeploy.util.Page;

public abstract class AbstractDaoImpl<E extends AbstractModel> implements IAbstractDao<E> {

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
	public void deleteById(Long id) {
		basicHibernateDaoImpl.deleteById(entityClass, id);;
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
		return "from " + this.entityClassName + generateWhereByParam(param) + generateOrderByByParam(param);
	}
	
	protected String generateWhereByParam(Map<String, Object> param) {
		StringBuilder buff = new StringBuilder(" where 1 = 1 ");
		for(Entry<String, Object> entry: param.entrySet()) {
			if(CommonConstant.ORDER_BY.equals(entry.getKey())) {
				continue;
			}
			String[] parsedResult = parseOperation(entry.getKey());
			if(parsedResult == null) {
				continue;
			}
			buff.append(" and ")
				.append(parsedResult[0])
				.append(parsedResult[1])
				.append(parsedResult[2]);
		}
		return buff.toString();
	}
	
	protected String generateOrderByByParam(Map<String, Object> param) {
		String orderBy = (String) param.get(CommonConstant.ORDER_BY);
		if(StringUtils.isBlank(orderBy)) {
			return "";
		}
		return " order by " + orderBy;
	}
	
	protected String[] parseOperation(String key) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		int index = key.indexOf("__");
		if (index == -1) {
			return new String[] { key.replaceAll("_", "."), " = ", ":" + key };
		}
		String oriKey = key.substring(0, index);
		String operName = key.substring(index + 2);
		String oper = " = ", placeholder = ":" + key;
		if ("eq".equalsIgnoreCase(operName)) {
			oper = " = ";
		} else if ("gt".equalsIgnoreCase(operName)) {
			oper = " > ";
		} else if ("ge".equalsIgnoreCase(operName)) {
			oper = " >= ";
		} else if ("lt".equalsIgnoreCase(operName)) {
			oper = " < ";
		} else if ("le".equalsIgnoreCase(operName)) {
			oper = " <= ";
		} else if ("contain".equalsIgnoreCase(operName)) {
			oper = " like ";
			placeholder = "concat('%', " + placeholder + ", '%')";
		} else if ("start_with".equalsIgnoreCase(operName)) {
			oper = " like ";
			placeholder = "concat(" + placeholder + ", '%')";
		} else if ("end_with".equalsIgnoreCase(operName)) {
			oper = " like ";
			placeholder = " concat('%', " + placeholder + ")";
		} else if("in".equalsIgnoreCase(operName)) {
			oper = " in ";
			placeholder = " ( " + placeholder + " ) ";
		} else if("not_in".equalsIgnoreCase(operName)) {
			oper = " not in ";
			placeholder = " ( " + placeholder + " ) ";
		}
		return new String[] { oriKey.replaceAll("_", "."), oper, placeholder };
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
