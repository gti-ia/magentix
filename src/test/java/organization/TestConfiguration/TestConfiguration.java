package organization.TestConfiguration;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import es.upv.dsic.gti_ia.organization.Configuration;


/** 
 * @author Jose Alemany Bordera  -  jalemany1@dsic.upv.es
 * 
 */

public class TestConfiguration {

	/* Attributes */
	private Configuration config;
	private Configuration otherConfig;
	
	@Before
	public void setUp() throws Exception {

		config = Configuration.getConfiguration();
	}
	
	@After
	public void tearDown() throws Exception {

		config = null;
		otherConfig = null;
	}
	
	/* Tests */
	@Test(timeout = 5 * 1000)
	public void testConstructor() throws Exception { 
	    
	    Constructor<Configuration> c = Configuration.class.getDeclaredConstructor();
		c.setAccessible(true);
		
		otherConfig = c.newInstance();
		
		assertEquals(true, otherConfig instanceof Configuration);
		
		Field fields[] = Configuration.class.getDeclaredFields();
	    for (int i = 0; i < fields.length; i++){ 
	        fields[i].setAccessible(true);
	        assertNotNull(fields[i].get(otherConfig));
	    }
	    
	    assertNotSame(otherConfig, config);
	}
	
	@Test(timeout = 5 * 1000)
	public void testSingleton() throws Exception {
		
		otherConfig = Configuration.getConfiguration();
		assertSame(otherConfig, config);
	}
	
	@Test(timeout = 5 * 1000)
	public void testGetOMSServiceDescriptionLocation() throws Exception {
		assertEquals("http://localhost:8080/omsservices/services/", config.getOMSServiceDescriptionLocation());
	}
	
	@Test(timeout = 5 * 1000)
	public void testGetSFServiceDescriptionLocation() throws Exception {
		assertEquals("http://localhost:8080/sfservices/services/", config.getSFServiceDescriptionLocation());
	}
	
	@Test(timeout = 5 * 1000)
	public void testGetBridgeHttpPort() throws Exception {
		assertEquals(8082, config.getBridgeHttpPort());
	}
	
	@Test(timeout = 5 * 1000)
	public void testGetHttpInterfacepPort() throws Exception {
		assertEquals(8081, config.getHttpInterfacepPort());
	}
	
	@Test(timeout = 5 * 1000)
	public void testGetIsTomcat() throws Exception {
		assertEquals(false, config.getIsTomcat());
	}
	
	@Test(timeout = 5 * 1000)
	public void testGetOS() throws Exception {
		assertEquals("linux", config.getOS());
	}
	
	@Test(timeout = 5 * 1000)
	public void testGetPathTomcat() throws Exception {
		assertEquals("../apache-tomcat-6.0.20", config.getPathTomcat());
	}
	
	@Test(timeout = 5 * 1000)
	public void testGetdatabaseServer() throws Exception {
		assertEquals("localhost", config.getdatabaseServer());
	}
	
	@Test(timeout = 5 * 1000)
	public void testGetdatabaseName() throws Exception {
		assertEquals("thomas", config.getdatabaseName());
	}
	
	@Test(timeout = 5 * 1000)
	public void testGetdatabaseUser() throws Exception {
		assertEquals("thomas", config.getdatabaseUser());
	}
	
	@Test(timeout = 5 * 1000)
	public void testGetdatabasePassword() throws Exception {
		assertEquals("thomas", config.getdatabasePassword());
	}
	
	@Test(timeout = 5 * 1000)
	public void testGetqpidHost() throws Exception {
		assertEquals("localhost", config.getqpidHost());
	}
	
	@Test(timeout = 5 * 1000)
	public void testGetqpidPort() throws Exception {
		assertEquals(5672, config.getqpidPort());
	}
	
	@Test(timeout = 5 * 1000)
	public void testGetqpidVhost() throws Exception {
		assertEquals("test", config.getqpidVhost());
	}
	
	@Test(timeout = 5 * 1000)
	public void testGetqpidUser() throws Exception {
		assertEquals("guest", config.getqpidUser());
	}
	
	@Test(timeout = 5 * 1000)
	public void testGetqpidPassword() throws Exception {
		assertEquals("guest", config.getqpidPassword());
	}
	
	@Test(timeout = 5 * 1000)
	public void testGetqpidSSL() throws Exception {
		assertEquals(false, config.getqpidSSL());
	}
	
	@Test(timeout = 5 * 1000)
	public void testIsSecureMode() throws Exception {
		assertEquals(false, config.isSecureMode());
	}
	
	@Test(timeout = 5 * 1000)
	public void testGetqpidsaslMechs() throws Exception {
		assertEquals("EXTERNAL", config.getqpidsaslMechs());
	}
	
	@Test(timeout = 5 * 1000)
	public void testGetjenadbURL() throws Exception {
		assertEquals("jdbc:mysql://localhost/thomas", config.getjenadbURL());
	}
	
	@Test(timeout = 5 * 1000)
	public void testGetjenadbType() throws Exception {
		assertEquals("MySQL", config.getjenadbType());
	}
	
	@Test(timeout = 5 * 1000)
	public void testGetjenadbDriver() throws Exception {
		assertEquals("com.mysql.jdbc.Driver", config.getjenadbDriver());
	}
	
	@Test(timeout = 5 * 1000)
	public void testGetTraceMask() throws Exception {
		assertEquals("1111110100", config.getTraceMask());
	}
}
