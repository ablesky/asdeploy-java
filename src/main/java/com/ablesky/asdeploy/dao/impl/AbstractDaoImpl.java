package com.ablesky.asdeploy.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.ablesky.asdeploy.dao.IAbstractDao;
import com.ablesky.asdeploy.pojo.AbstractModel;
import com.ablesky.asdeploy.util.ClassUtil;
import com.ablesky.asdeploy.util.CommonConstant;
import com.ablesky.asdeploy.util.Page;

public abstract class AbstractDaoImpl<E extends AbstractModel> implements IAbstractDao<E> {

	@Autowired
	protected BasicHibernateDaoImpl basicHibernateDao;
	
	@SuppressWarnings("unchecked")
	private Class<E> entityClass = ClassUtil.getSuperClassGenericType(this.getClass(), 0);
	private String entityClassName = entityClass.getSimpleName();
	
	@Override
	public void saveOrUpdate(E entity) {
		basicHibernateDao.saveOrUpdate(entity);
	}
	
	@Override
	public void delete(E entity) {
		basicHibernateDao.delete(entity);
	}
	
	@Override
	public void deleteById(Long id) {
		basicHibernateDao.deleteById(entityClass, id);;
	}
	
	@Override
	public E getById(Long id) {
		return basicHibernateDao.getById(entityClass, id);
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
		return basicHibernateDao.list(start, limit, generateHqlByParam(param), param);
	}
	
	@Override
	public List<E> list(Map<String, Object> param) {
		return list(0, 0, param);
	}
	
	@Override
	public int count(Map<String, Object> param) {
		return basicHibernateDao.count(generateHqlByParam(param), param);
	}
	
	@Override
	public Page<E> paginate(int start, int limit, Map<String, Object> param) {
		return basicHibernateDao.paginate(start, limit, generateHqlByParam(param), param);
	}
	
	protected String generateHqlByParam(Map<String, Object> param) {
		return "from " + this.entityClassName + generateWhereByParam(param) + generateOrderByByParam(param);
	}
	
	/**
	 * 有一定的局限性，尤其是or的时候，key也不能重复(有些情形可以用in作变通)
	 * 但这机制本身就是为了提高简单场景下的开发效率的，所以不用考虑特别复杂的情形
	 */
	@SuppressWarnings("unchecked")
	protected String generateWhereByParam(Map<String, Object> param) {
		List<Map<String, Object>> orParamList = new ArrayList<Map<String,Object>>();
		List<String> keyList = new ArrayList<String>(param.keySet());
		for(String key: keyList) {
			if(key == null || !key.toLowerCase().endsWith("__or")) {
				continue;
			}
			Map<String, Object> orParam = (Map<String, Object>)param.remove(key);
			if(MapUtils.isEmpty(orParam)) {
				continue;
			}
			orParamList.add(orParam);
		}
		StringBuilder buff = new StringBuilder(" where ")
			.append(generateAndConditionsByParam(param));
		for(Map<String, Object> orParam: orParamList) {
			buff.append(" and (").append(generateOrConditionsByParam(orParam)).append(")");
			param.putAll(orParam);
		}
		return buff.toString();
	}
	
	private String generateAndConditionsByParam(Map<String, Object> param) {
		StringBuilder buff = new StringBuilder(" 1 = 1 ");
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
	
	private String generateOrConditionsByParam(Map<String, Object> param) {
		StringBuilder buff = new StringBuilder(" 1 = 2 ");
		for(Entry<String, Object> entry: param.entrySet()) {
			if(CommonConstant.ORDER_BY.equals(entry.getKey())) {
				continue;
			}
			String[] parsedResult = parseOperation(entry.getKey());
			if(parsedResult == null) {
				continue;
			}
			buff.append(" or ")
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
		} else if ("not_contain".equalsIgnoreCase(operName)) {
			oper = " not like ";
			placeholder = "concat('%', " + placeholder + ", '%')";
		} else if ("start_with".equalsIgnoreCase(operName)) {
			oper = " like ";
			placeholder = "concat(" + placeholder + ", '%')";
		} else if ("not_start_with".equalsIgnoreCase(operName)) {
			oper = " not like ";
			placeholder = "concat(" + placeholder + ", '%')";
		} else if ("end_with".equalsIgnoreCase(operName)) {
			oper = " like ";
			placeholder = " concat('%', " + placeholder + ")";
		} else if ("not_end_with".equalsIgnoreCase(operName)) {
			oper = " not like ";
			placeholder = " concat('%', " + placeholder + ")";
		} else if("in".equalsIgnoreCase(operName)) {
			oper = " in ";
			placeholder = " ( " + placeholder + " ) ";
		} else if("not_in".equalsIgnoreCase(operName)) {
			oper = " not in ";
			placeholder = " ( " + placeholder + " ) ";
		} else if("is_null".equalsIgnoreCase(operName)) {
			oper = " is null ";
			placeholder = "";
		}
		return new String[] { oriKey.replaceAll("_", "."), oper, placeholder };
	}
	
	public void executeSql(String sql) {
		basicHibernateDao.executeSql(sql);
	}
	
	public void executeSql(String sql, Map<String, Object> param) {
		basicHibernateDao.executeSql(sql, param);
	}
}
