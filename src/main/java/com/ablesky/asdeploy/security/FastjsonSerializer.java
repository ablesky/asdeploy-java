package com.ablesky.asdeploy.security;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.shiro.io.SerializationException;
import org.apache.shiro.io.Serializer;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;

import com.ablesky.asdeploy.pojo.AbstractFastjsonSerializableModel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 本类基于fastjson进行序列化和反序列化操作
 * 用于rememberMe功能相关的序列化和反序列化PrincipalCollection
 * shiro默认的实现，使用的是基于java字节码的序列化和反序列化，产生的cookie实在有点大
 * 复杂类型需要是 {@link AbstractFastjsonSerializableModel} 的子类，才能被正确的反序列化 
 * @author zyang
 */
public class FastjsonSerializer implements Serializer<PrincipalCollection>{

	@Override
	public byte[] serialize(PrincipalCollection collection) throws SerializationException {
		Map<String, Object> infoMap = new HashMap<String, Object>();
		Set<String> realmNameSet = collection.getRealmNames();
		for(String realmName: realmNameSet) {
			infoMap.put(realmName, collection.fromRealm(realmName));
		}
		return JSON.toJSONString(infoMap).getBytes();
	}

	@Override
	public PrincipalCollection deserialize(byte[] serialized) throws SerializationException {
		String json = new String(serialized);
		JSONObject infoMap = JSON.parseObject(json);
		SimplePrincipalCollection collection = new SimplePrincipalCollection();
		for(String realmName: infoMap.keySet()) {
			JSONArray list = infoMap.getJSONArray(realmName);
			if(CollectionUtils.isEmpty(list)) {
				continue;
			}
			for(Object obj: list) {
				if(obj instanceof JSONObject) {
					JSONObject jsonObj = (JSONObject) obj;
					String className = jsonObj.getString(AbstractFastjsonSerializableModel.CLASS_NAME);
					Class<?> clazz = null;
					try { 
						clazz = Class.forName(className); 
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					if(clazz == null) {
						continue;
					}
					collection.add(deserializeObj(jsonObj, clazz), realmName);
				} else {
					collection.add(obj, realmName);
				}
			}
		}
		return collection;
	}
	
	private <T> T deserializeObj(JSONObject jsonObj, Class<T> clazz) {
		if(!AbstractFastjsonSerializableModel.class.isAssignableFrom(clazz)) {
			return null;
		}
		AbstractFastjsonSerializableModel<?> model = null;
		try {
			model = (AbstractFastjsonSerializableModel<?>) clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return clazz.cast(model.fromJSONObject(jsonObj));
	}
	
}
