package organization.TestConfiguration;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import TestTrace.CommandedTraceManager;
import es.upv.dsic.gti_ia.organization.Configuration;
import es.upv.dsic.gti_ia.trace.TraceMask;


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
	@Test
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
	
	@Test
	public void testSingleton() throws Exception {
		
		otherConfig = Configuration.getConfiguration();
		assertSame(otherConfig, config);
	}
	
	@Test
	public void testGetOMSServiceDescriptionLocation() throws Exception {
		assertEquals("http://localhost:8080/omsservices/services/", config.getOMSServiceDescriptionLocation());
	}
	
	@Test
	public void testGetSFServiceDescriptionLocation() throws Exception {
		assertEquals("http://localhost:8080/sfservices/services/", config.getSFServiceDescriptionLocation());
	}
	
	@Test
	public void testGetBridgeHttpPort() throws Exception {
		assertEquals(8082, config.getBridgeHttpPort());
	}
	
	@Test
	public void testGetHttpInterfacepPort() throws Exception {
		assertEquals(8081, config.getHttpInterfacepPort());
	}
	
	@Test
	public void testGetIsTomcat() throws Exception {
		assertEquals(false, config.getIsTomcat());
	}
	
	@Test
	public void testGetOS() throws Exception {
		assertEquals("linux", config.getOS());
	}
	
	@Test
	public void testGetPathTomcat() throws Exception {
		assertEquals("../apache-tomcat-6.0.20", config.getPathTomcat());
	}
	
	@Test
	public void testGetdatabaseServer() throws Exception {
		assertEquals("localhost", config.getdatabaseServer());
	}
	
	@Test
	public void testGetdatabaseName() throws Exception {
		assertEquals("thomas", config.getdatabaseName());
	}
	
	@Test
	public void testGetdatabaseUser() throws Exception {
		assertEquals("thomas", config.getdatabaseUser());
	}
	
	@Test
	public void testGetdatabasePassword() throws Exception {
		assertEquals("thomas", config.getdatabasePassword());
	}
	
	@Test
	public void testGetqpidHost() throws Exception {
		assertEquals("localhost", config.getqpidHost());
	}
	
	@Test
	public void testGetqpidPort() throws Exception {
		assertEquals(5672, config.getqpidPort());
	}
	
	@Test
	public void testGetqpidVhost() throws Exception {
		assertEquals("test", config.getqpidVhost());
	}
	
	@Test
	public void testGetqpidUser() throws Exception {
		assertEquals("guest", config.getqpidUser());
	}
	
	@Test
	public void testGetqpidPassword() throws Exception {
		assertEquals("guest", config.getqpidPassword());
	}
	
	@Test
	public void testGetqpidSSL() throws Exception {
		assertEquals(false, config.getqpidSSL());
	}
	
	@Test
	public void testIsSecureMode() throws Exception {
		assertEquals(false, config.isSecureMode());
	}
	
	@Test
	public void testGetqpidsaslMechs() throws Exception {
		assertEquals("EXTERNAL", config.getqpidsaslMechs());
	}
	
	@Test
	public void testGetjenadbURL() throws Exception {
		assertEquals("jdbc:mysql://localhost/thomas", config.getjenadbURL());
	}
	
	@Test
	public void testGetjenadbType() throws Exception {
		assertEquals("MySQL", config.getjenadbType());
	}
	
	@Test
	public void testGetjenadbDriver() throws Exception {
		assertEquals("com.mysql.jdbc.Driver", config.getjenadbDriver());
	}
	
	@Test
	public void testGetTraceMask() throws Exception {
		assertEquals("1111110100", config.getTraceMask());
	}
}
