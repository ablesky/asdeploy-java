package com.ablesky.asdeploy.dao.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ablesky.asdeploy.dao.IAbstractDao;
import com.ablesky.asdeploy.pojo.AbstractModel;
import com.ablesky.asdeploy.util.ClassUtil;
import com.ablesky.asdeploy.util.Page;

public abstract class AbstractDaoImpl<E extends AbstractModel> implements IAbstractDao<E> {

	protected BaseHibernateDao baseHibernateDao = BaseHibernateDao.singletonInstance();
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@SuppressWarnings("unchecked")
	private Class<E> entityClass = ClassUtil.getSuperClassGenericType(this.getClass(), 0);
	private String entityClassName = entityClass.getSimpleName();
	
	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	@Override
	public void saveOrUpdate(E entity) {
		baseHibernateDao.saveOrUpdate(getSession(), entity);
	}
	
	@Override
	public void delete(E entity) {
		baseHibernateDao.delete(getSession(), entity);
	}
	
	@Override
	public void deleteById(Long id) {
		baseHibernateDao.deleteById(getSession(), entityClass, id);;
	}
	
	@Override
	public E getById(Long id) {
		return baseHibernateDao.getById(getSession(), entityClass, id);
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
		return baseHibernateDao.list(getSession(), start, limit, generateHqlByParam(param), param);
	}
	
	@Override
	public List<E> list(Map<String, Object> param) {
		return list(0, 0, param);
	}
	
	@Override
	public int count(Map<String, Object> param) {
		return baseHibernateDao.count(getSession(), generateHqlByParam(param), param);
	}
	
	@Override
	public Page<E> paginate(int start, int limit, Map<String, Object> param) {
		return baseHibernateDao.paginate(getSession(), start, limit, generateHqlByParam(param), param);
	}

	@Override
	public void refresh(E entity) {
		getSession().refresh(entity);
	}
	
	@Override
	public int executeSql(String sql) {
		return baseHibernateDao.executeSql(getSession(), sql);
	}
	
	@Override
	public int executeSql(String sql, Map<String, Object> param) {
		return baseHibernateDao.executeSql(getSession(), sql, param);
	}
	
	@Override
	public void flush() {
		getSession().flush();
	}
	
	//-------------- 以下为内部方法 ----------------//
	
	protected String generateHqlByParam(Map<String, Object> param) {
		Object leftJoinFetchParam = param.remove(DaoConstant.LEFT_JOIN_FETCH);
		String entityAlias = StringUtils.uncapitalize(this.entityClassName);
		StringBuilder hqlBuff = new StringBuilder()
			.append(" from ")
			.append(this.entityClassName).append(" ")
		;
		String keyPrefix = "";
		// leftJoinFetchParam需要形如new String[]{"content c", "course course"}
		if(leftJoinFetchParam != null && leftJoinFetchParam instanceof String[]) {
			hqlBuff.append(entityAlias).append(" ");	// 带leftJoinFetch的情形，hql中的pojo都要加别名
			for(String str: (String[])leftJoinFetchParam) {
				hqlBuff.append(" left join fetch ")
					.append(entityAlias).append(".").append(str)
					.append(" ");
			}
			keyPrefix = entityAlias + ".";
		}
		return hqlBuff.append(generateWhereByParam(keyPrefix, param)).append(generateOrderByByParam(keyPrefix, param)).toString();
	}
	
	/**
	 * 有一定的局限性，尤其是or的时候，key也不能重复(有些情形可以用in作变通)
	 * 但这机制本身就是为了提高简单场景下的开发效率的，所以不用考虑特别复杂的情形
	 */
	
	@SuppressWarnings("unchecked")
	protected String generateWhereByParam(String keyPrefix, Map<String, Object> param) {
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
			.append(generateAndConditionsByParam(keyPrefix, param));
		for(Map<String, Object> orParam: orParamList) {
			buff.append(" and (").append(generateOrConditionsByParam(keyPrefix, orParam)).append(")");
			param.putAll(orParam);
		}
		return buff.toString();
	}
	
	private String generateAndConditionsByParam(String keyPrefix, Map<String, Object> param) {
		StringBuilder buff = new StringBuilder(" 1 = 1 ");
		for(Entry<String, Object> entry: new ArrayList<Entry<String, Object>>(param.entrySet())) {
			if(DaoConstant.ORDER_BY.equals(entry.getKey())) {
				continue;
			}
			OperationParsedResult parsedResult = parseOperation(entry.getKey());
			if(parsedResult == null) {
				continue;
			}
			parsedResult = processParsedResultWithOwnerFlag(keyPrefix, parsedResult, param);
			buff.append(" and ")
				.append(parsedResult.getKey())
				.append(parsedResult.getOper())
				.append(parsedResult.getPlaceholder());
		}
		return buff.toString();
	}
	
	private String generateOrConditionsByParam(String keyPrefix, Map<String, Object> param) {
		StringBuilder buff = new StringBuilder(" 1 = 2 ");
		for(Entry<String, Object> entry: new ArrayList<Entry<String, Object>>(param.entrySet())) {
			if(DaoConstant.ORDER_BY.equals(entry.getKey())) {
				continue;
			}
			OperationParsedResult parsedResult = parseOperation(entry.getKey());
			if(parsedResult == null) {
				continue;
			}
			parsedResult = processParsedResultWithOwnerFlag(keyPrefix, parsedResult, param);
			buff.append(" or ")
				.append(parsedResult.getKey())
				.append(parsedResult.getOper())
				.append(parsedResult.getPlaceholder());
		}
		return buff.toString();
	}
	
	private OperationParsedResult processParsedResultWithOwnerFlag(String keyPrefix, OperationParsedResult parsedResult, Map<String, Object> param) {
		String key = parsedResult.getKey();
		String placeHolder = parsedResult.getPlaceholder();
		String originParamKey = parsedResult.getKeyWithOper();
		if(key.contains(DaoConstant.OWNER_FLAG)) {	// 如果key中包含"->"，则不会自动添加当前dao所对应的entity的className做前缀了
			key = key.replaceAll(DaoConstant.OWNER_FLAG, ".");
		} else if(StringUtils.isNotBlank(keyPrefix)) {
			key = keyPrefix + key;
		}
		if(placeHolder.contains(DaoConstant.OWNER_FLAG)) {
			placeHolder = placeHolder.replaceAll(DaoConstant.OWNER_FLAG, "_");
			param.put(originParamKey.replaceAll(DaoConstant.OWNER_FLAG, "_"), param.get(originParamKey));
		}
		return new OperationParsedResult(key, parsedResult.getOper(), placeHolder, parsedResult.getKeyWithOper());
	}
	
	private String generateOrderBy(String orderByStr) {
		if(StringUtils.isBlank(orderByStr)) {
			return "";
		}
		return " order by " + orderByStr;
	}
	
	private String generateOrderBy(String keyPrefix, Map<?, ?> orderByParam) {
		if(MapUtils.isEmpty(orderByParam)) {
			return "";
		}
		StringBuilder orderByBuff = new StringBuilder();
		boolean isFirst = true;
		for(Entry<?, ?> entry: orderByParam.entrySet()) {
			String key = entry.getKey() != null? entry.getKey().toString(): "";
			String value = entry.getValue() != null? entry.getValue().toString(): "";
			if(StringUtils.isBlank(key) && StringUtils.isBlank(value)) {
				continue;
			}
			if(isFirst) {
				orderByBuff.append(" order by ");
				isFirst = false;
			} else {
				orderByBuff.append(", ");
			}
			if(key.contains(DaoConstant.OWNER_FLAG)) {
				key = key.replaceAll(DaoConstant.OWNER_FLAG, ".");
			} else if (StringUtils.isNotBlank(keyPrefix)) {
				key = keyPrefix + key.trim();
			}
			orderByBuff.append(key).append(" ").append(value).append(" ");
		}
		return orderByBuff.toString();
	}
	
	protected String generateOrderByByParam(String keyPrefix, Map<String, Object> param) {
		Object orderBy = param.remove(DaoConstant.ORDER_BY);
		if(orderBy == null) {
			return "";
		}
		if(orderBy instanceof String) {	// 这种字符串类型的orderBy比较弱
			return generateOrderBy((String) orderBy);
		}
		if(orderBy instanceof Map) {
			return generateOrderBy(keyPrefix, (Map<?, ?>) orderBy);
		}
		return "";
	}
	
	protected OperationParsedResult parseOperation(String keyWithOper) {
		if (StringUtils.isBlank(keyWithOper)) {
			return null;
		}
		int index = keyWithOper.lastIndexOf("__");
		if (index == -1) {
			String key = keyWithOper;
			return new OperationParsedResult(key.replaceAll("_", "."), " = ", ":" + key, keyWithOper);
		}
		String key = keyWithOper.substring(0, index);
		String operName = keyWithOper.substring(index + 2);
		String oper = " = ", placeholder = ":" + keyWithOper;
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
		return new OperationParsedResult(key.replaceAll("_", "."), oper, placeholder, keyWithOper);
	}
	
}
