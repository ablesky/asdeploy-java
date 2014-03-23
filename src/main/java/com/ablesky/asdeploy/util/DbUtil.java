package com.ablesky.asdeploy.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DbUtil {

	private DbUtil() {}
	
	/**
	 * 不清楚<jdbc:initialize-database>是如何实现的
	 */
	@SuppressWarnings("resource")
	public static final void initDb() throws IOException {
		File dbFolder = new File(FilenameUtils.concat(SystemUtils.USER_DIR, "db"));
		if(!dbFolder.exists()) {
			dbFolder.mkdirs();
		}
		Profiles.setProfileAsSystemProperty(Profiles.DEVELOPMENT_INIT);
		new ClassPathXmlApplicationContext("applicationContext.xml");
	}
	
	public static void main(String[] args) throws IOException {
		initDb();
	}

}
