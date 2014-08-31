package com.ablesky.asdeploy.test;

import org.junit.Ignore;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.ablesky.asdeploy.util.Profiles;

/**
 * Spring的支持依赖注入的JUnit4 集成测试基类, 相比Spring原基类名字更短.
 * 
 * 子类需要定义applicationContext文件的位置,如:
 * 
 * @ContextConfiguration(locations = { "/applicationContext-test.xml" })
 * 
 * @author calvin
 */
@Ignore
@ActiveProfiles(Profiles.UNIT_TEST)
@ContextConfiguration(locations = {"/applicationContext.xml", "/applicationContext-test.xml"})
public class SpringTransactionalTestCase extends AbstractTransactionalJUnit4SpringContextTests {

}
