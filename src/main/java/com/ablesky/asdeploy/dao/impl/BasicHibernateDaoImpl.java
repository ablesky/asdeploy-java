package com.ablesky.asdeploy.dao.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ablesky.asdeploy.util.Page;

@Repository
public class BasicHibernateDaoImpl {

	@Autowired
	private SessionFactory sessionFactory;

	public Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}
	
	public <T> void saveOrUpdate(T entity){
		getCurrentSession().saveOrUpdate(entity);
	}
	
	public <T> void delete(T entity) {
		getCurrentSession().delete(entity);
	}
	
	public <T> void deleteById(Class<T> clazz, Long id) {
		String hql = "delete from " + clazz.getSimpleName() + " where id = :id ";
		Query query = getCurrentSession().createQuery(hql);
		fillParameter(query, "id", id);
		query.executeUpdate();
	}
	
	public <T> T getById(Class<T> clazz, Long id) {
		return clazz.cast(getCurrentSession().get(clazz, id));
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> list(int start, int limit, String hql, Map<String, Object> param) {
		Session session = getCurrentSession();
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
	
	public int count(String hql, Map<String, Object> param) {
		return count(hql, param, true);
	}
	
	/**
	 * @param hql
	 * @param param
	 * @param needConvert: true表明hql本身不是一个取count的hql，不需要做select count(*)的替换
	 * @return
	 */
	public int count(String hql, Map<String, Object> param, boolean needConvert) {
		if(param == null) {
			param = Collections.<String,Object>emptyMap();
		}
		Session session = getCurrentSession();
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
			return new Integer(list.get(0).toString());
		}
	}
	
	public <T> Page<T> paginate(int start, int limit, String hql, Map<String, Object> param) {
		return new Page<T>(start, limit, count(hql, param), this.<T>list(start, limit, hql, param));
	}
	
	public void executeSql(String sql) {
		executeSql(sql, Collections.<String, Object>emptyMap());
	}
	
	public void executeSql(String sql, Map<String, Object> param) {
		Session session = getCurrentSession();
		Query query = session.createSQLQuery(sql);
		for (String key : getPlaceHolderList(sql)) {
			fillParameter(query, key, param);
		}
		query.executeUpdate();
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
	 * 获取sql/hql中的所有占位符(形如":XXX")
	 * @author zyang
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
			idx1 = sql.indexOf("'", idx);
			idx2 = sql.indexOf("\"", idx);
			if(idx1 == -1){
				idx1 = sql.length();
			}
			if(idx2 == -1){
				idx2 = sql.length();
			}
			minIdx = Math.min(idx1, idx2);
			clearedSqlBuff.append(sql.substring(idx, minIdx));
			if(minIdx == sqlLen){
				break;
			}
			idx = (idx1 < idx2? sql.indexOf("'", idx1 + 1): sql.indexOf("\"", idx2 + 1)) + 1;
		}
		String clearedSql = clearedSqlBuff.toString();
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
