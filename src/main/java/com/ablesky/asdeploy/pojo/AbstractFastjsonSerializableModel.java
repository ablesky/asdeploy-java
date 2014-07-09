package com.ablesky.asdeploy.pojo;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * 用于对象需要基于fastjson进行序列化和反序列化操作的情形
 * @author zyang
 */
public abstract class AbstractFastjsonSerializableModel<T> extends AbstractModel {
	
	public static final String CLASS_NAME = "className";
	
	/**
	 * 根据jsonObj中的内容，填充实例自身
	 * @param jsonObj
	 */
	public abstract T fromJSONObject(JSONObject jsonObj);
	
	/**
	 * 用于按json格式序列化和反序列化时，记录类信息
	 * 仅用于fastjson
	 */
	@JSONField(name = CLASS_NAME)
	public String reflectClassName() {
		return this.getClass().getName();
	}

}
