package com.ablesky.asdeploy.dao.base;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import com.ablesky.asdeploy.util.Page;

public class BaseHibernateDao {
	
	private BaseHibernateDao() {}
	
	private static final BaseHibernateDao SINGLETON_INSTANCE = new BaseHibernateDao();
	
	public static final BaseHibernateDao singletonInstance() {
		return SINGLETON_INSTANCE;
	}

	public <T> void saveOrUpdate(Session session, T entity) {
		session.saveOrUpdate(entity);
	}
	
	public <T> void delete(Session session, T entity) {
		session.delete(entity);
	}
	
	public <T> void deleteById(Session session, Class<T> clazz, Long id) {
		String hql = "delete from " + clazz.getSimpleName() + " where id = :id ";
		Query query = session.createQuery(hql);
		fillParameter(query, "id", id);
		query.executeUpdate();
	}
	
	public <T> T getById(Session session, Class<T> clazz, Long id) {
		return clazz.cast(session.get(clazz, id));
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> list(Session session, int start, int limit, String hql, Map<String, Object> param) {
		Query query = session.createQuery(hql);
		for(String key: getPlaceHolderList(hql)) {
			fillParameter(query, key, param);
		}
		if(limit != 0) {
			query.setFirstResult(start);
			query.setMaxResults(limit);
		}
		return query.list();
	}
	
	public int count(Session session, String hql, Map<String, Object> param) {
		return count(session, hql, param, true);
	}
	
	/**
	 * @param hql
	 * @param param
	 * @param needConvert: true表明hql本身不是一个取count的hql，不需要做select count(*)的替换
	 * @return
	 */
	public int count(Session session, String hql, Map<String, Object> param, boolean needConvert) {
		if(param == null) {
			param = Collections.<String,Object>emptyMap();
		}
		if(needConvert) {
			int beginPos = hql.indexOf("from");
			int endPos = hql.indexOf("order by");
			if(endPos == -1) {
				endPos = hql.length();
			}
			hql = "select count(*) " + hql.substring(beginPos, endPos);
		}
		Query query = session.createQuery(hql);
		for (String key : getPlaceHolderList(hql)) {
			fillParameter(query, key, param);
		}
		List<?> list = query.list();
		if(list.isEmpty()) {
			return 0;
		}
		if(hql.indexOf("group by") != -1) {
			return list.size();
		} else {
			return Integer.valueOf(list.get(0).toString());
		}
	}
	
	public <T> Page<T> paginate(Session session, int start, int limit, String hql, Map<String, Object> param) {
		return new Page<T>(start, limit, count(session, hql, param), this.<T>list(session, start, limit, hql, param));
	}
	
	public int executeSql(Session session, String sql) {
		return executeSql(session, sql, Collections.<String, Object>emptyMap());
	}
	
	public int executeSql(Session session, String sql, Map<String, Object> param) {
		Query query = session.createSQLQuery(sql);
		for (String key : getPlaceHolderList(sql)) {
			fillParameter(query, key, param);
		}
		return query.executeUpdate();
	}
	
	private void fillParameter(Query query, String key, Object value) {
		if(query == null || StringUtils.isBlank(key)) {
			return;
		}
		if (value instanceof Collection) {
			query.setParameterList(key, (Collection<?>)value);
		} else if (value instanceof Object[]) {
			query.setParameterList(key, (Object[]) value);
		} else {
			query.setParameter(key, value);
		}
	}
	
	private void fillParameter(Query query, String key, Map<String, Object> param) {
		if(query == null || param == null || StringUtils.isBlank(key)) {
			return;
		}
		fillParameter(query, key, param.get(key));
	}
	
	/**
	 * 可用来获取sql中的字符串边界
	 * 主要用来克服字符串中所出现的sql转义字符
	 */
	private int indexOfNextKeyChar(String sql, char c, int fromIndex) {
		int pos = -1, len = sql.length();
		for(pos = sql.indexOf(c, fromIndex); pos > -1 && pos < len; pos = sql.indexOf(c, fromIndex)) {
			if(pos > 0 && sql.charAt(pos - 1) == '\\') {
				fromIndex = pos + 1;
				continue;
			}
			// '作为转义符，只能放在另一个'的前面，即''的情形
			if(c == '\'' && pos < len - 1 && sql.charAt(pos + 1) == '\'') {
				fromIndex = pos + 2;
				continue;
			}
			break;
		}
		return pos;
	}
	
	/**
	 * 获取sql/hql中的所有占位符(形如":XXX")
	 */
	private List<String> getPlaceHolderList(String sql){
		if(sql.indexOf(":") == -1){
			return Collections.<String>emptyList();
		}
		// 剔除sql中的字符串
		StringBuilder clearedSqlBuff = new StringBuilder();
		int idx1 = 0, idx2 = 0, idx = 0, minIdx = 0;
		int sqlLen = sql.length();
		while(idx < sqlLen){
			idx1 = indexOfNextKeyChar(sql, '\'', idx);
			idx2 = indexOfNextKeyChar(sql, '"', idx);
			if(idx1 == -1){
				idx1 = sqlLen;
			}
			if(idx2 == -1){
				idx2 = sqlLen;
			}
			minIdx = Math.min(idx1, idx2);
			clearedSqlBuff.append(sql.substring(idx, minIdx));
			if(minIdx == sqlLen){
				break;
			}
			idx = (idx1 < idx2? indexOfNextKeyChar(sql, '\'', idx1 + 1): indexOfNextKeyChar(sql, '"', idx2 + 1)) + 1;
		}
		String clearedSql = clearedSqlBuff.toString();	// 剔除了字符串后的sql
		List<String> placeHolderList = new LinkedList<String>();
		String prefix = ":";
		int clearedSqlLen = clearedSql.length();
		int prefixLen = prefix.length();
		int beginIdx = clearedSql.indexOf(prefix), 
			endIdx;
		if(beginIdx == -1) {
			return Collections.<String>emptyList();
		}
		while(beginIdx != -1){
			beginIdx += prefixLen;
			endIdx = beginIdx;
			char c;
			while(endIdx < clearedSqlLen){
				c = clearedSql.charAt(endIdx);
				if(!Character.isLetter(c) && c != '_' && (c < '0' || c > '9')){
					break;
				}
				endIdx ++;
			}
			if(endIdx <= beginIdx + 1){
				continue;
			}
			placeHolderList.add(clearedSql.substring(beginIdx, endIdx));
			beginIdx = clearedSql.indexOf(prefix, endIdx);
		}
		return placeHolderList;
	}
}
