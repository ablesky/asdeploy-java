package com.ablesky.asdeploy.util.cmd;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import com.ablesky.asdeploy.util.DeployConfiguration;


public class AblejsCmd extends AbstractCmd<AblejsCmd.AblejsOperation, AblejsCmd.AblejsOperationType> {
	
	// 如果后续会新增项目，则此处的配置有可能会继续增加。 
	// 如果ablejs的context-path目录能与项目保持一一映射，则不需要配置，直接用项目名(project.name)就可以了
	@SuppressWarnings("serial")
	public static final Map<String, String> PROJECT_MAPPING_FOR_ABLEJS = Collections.unmodifiableMap(new HashMap<String, String>(){{
		put("as-web", "default");
		put("as-search", "default");
	}});
	
	public static final String ABLEJS_FILEMAP_ROOT_PATH = DeployConfiguration.getInstance().getAblejsFilemapRootPath();
	
	private static String getProjectMappedName(String projectName) {
		if(StringUtils.isBlank(projectName)) {
			return "default";
		}
		String mappedName = PROJECT_MAPPING_FOR_ABLEJS.get(projectName);
		if(StringUtils.isNotBlank(mappedName)) {
			return mappedName;
		}
		return projectName;
	}
	
	/**
	 * 根据项目名称拼接相应的filemap.json文件的存储路径
	 * 注意要与ablejs的配置保持一致
	 */
	private static String getAblejsContextPathByProjectName(String projectName) {
		return FilenameUtils.concat(ABLEJS_FILEMAP_ROOT_PATH, getProjectMappedName(projectName));
	}
	
	/**
	 * 项目名称，决定了工程所对应的filemap.json的存储路径
	 */
	private String projectName;

	public AblejsCmd() {
		this("as-web");
	}
	
	public AblejsCmd(String projectName) {
		super(new File(SystemUtils.IS_OS_WINDOWS? "c:/": "/home"));
		this.projectName = projectName;
	}
	
	public AblejsCmd(File dir) {
		super(dir);
	}
	
	public AblejsOperation path() {
		return oper(AblejsOperationType.PATH);
	}
	
	public AblejsOperation fingerprint() {
		return oper(AblejsOperationType.FINGERPRINT);
	}
	
	/**
	 * 非静态类的构造方法的反射调用遇到了问题
	 * 需要找时间研究下
	 */
	@Override
	public AblejsOperation oper(AblejsOperationType operType) {
		return new AblejsOperation(dir, operType);
	}

	public class AblejsOperation extends AbstractCmd.Operation<AblejsOperationType> {

		public AblejsOperation(File dir, AblejsOperationType operType) {
			super(dir, operType);
		}

		@Override
		public String toString() {
			StringBuilder cmdBuff = new StringBuilder()
				.append("ablejs").append(" ")
				.append("--cli").append(" ")
				.append("--context-path").append(" ")	// 针对不同的工程，filemap.json会放在不同的路径下
				.append(AblejsCmd.getAblejsContextPathByProjectName(AblejsCmd.this.projectName)).append(" ")
				.append(operType.getOperName());
			for(String param: params.keySet()) {
				cmdBuff.append(" ").append(param);
			}
			return cmdBuff.toString();
		}
		
	}
	
	public enum AblejsOperationType implements AbstractCmd.OperationType {
		
		PATH("--filemap-path"), 				// 根据相对路径查询依赖信息
		FINGERPRINT("--filemap-fingerprint");	// 根据hash后的路径查询依赖信息
		
		private String option;
		
		private AblejsOperationType(String option) {
			this.option = option;
		}

		@Override
		public String getOperName() {
			return option;
		}
	}
}
