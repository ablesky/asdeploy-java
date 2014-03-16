package com.ablesky.asdeploy.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.ablesky.asdeploy.dao.IAbstractDao;
import com.ablesky.asdeploy.pojo.AbstractModel;
import com.ablesky.asdeploy.util.ClassUtil;
import com.ablesky.asdeploy.util.CommonConstant;
import com.ablesky.asdeploy.util.Page;

public abstract class AbstractDaoImpl<E extends AbstractModel> implements IAbstractDao<E> {

	@Autowired
	private BasicHibernateDaoImpl basicHibernateDaoImpl;
	
	@SuppressWarnings("unchecked")
	private Class<E> entityClass = ClassUtil.getSuperClassGenericType(this.getClass(), 0);
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
	public E first(Map<String, Object> param) {
		List<E> list = list(0, 1, param);
		return CollectionUtils.isNotEmpty(list)? list.get(0): null;
	}
	
	@Override
	public E unique(Map<String, Object> param) {
		List<E> list = list(0, 2, param);
		int len = list.size();
		if(len > 1) {
			throw new IllegalStateException("The query result should be unique!");
		}
		return len > 0? list.get(0): null;
	}
	
	@Override
	public List<E> list(int start, int limit, Map<String, Object> param) {
		return basicHibernateDaoImpl.list(start, limit, generateHqlByParam(param), param);
	}
	
	@Override
	public List<E> list(Map<String, Object> param) {
		return list(0, 0, param);
	}
	
	@Override
	public int count(Map<String, Object> param) {
		return basicHibernateDaoImpl.count(generateHqlByParam(param), param);
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
		if (StringUtils.isBlank(operName) || "eq".equalsIgnoreCase(operName)) {
			oper = " = ";
		} else if ("ne".equalsIgnoreCase(operName)) {
			oper = " != ";
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
	
	public void executeSql(String sql) {
		basicHibernateDaoImpl.executeSql(sql);
	}
	
	public void executeSql(String sql, Map<String, Object> param) {
		basicHibernateDaoImpl.executeSql(sql, param);
	}
}
