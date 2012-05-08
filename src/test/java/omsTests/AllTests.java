package omsTests;


import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.SF;
import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		
		AgentsConnection.connect();
		
		
		OMS oms = OMS.getOMS();
		
		SF sf = SF.getSF();
		
		oms.start();
		sf.start();
		
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		//suite.addTestSuite(TestThomas.class);
		//suite.addTestSuite(TestAcquireRole.class);
		//suite.addTestSuite(TestRegisterUnit.class);
		//suite.addTestSuite(TestRegisterRole.class);
		suite.addTestSuite(TestDeRegisterRole.class);
		//$JUnit-END$
		return suite;
	}

}
