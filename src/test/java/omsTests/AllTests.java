package omsTests;


import Thomas_example.Thomas;
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
		
		TestSuite suite = new TestSuite();
		//$JUnit-BEGIN$
//		suite.addTestSuite(Thomas.class);
//		suite.addTestSuite(AcquireRole.class);
//		suite.addTestSuite(RegisterUnit.class);
//		suite.addTestSuite(RegisterRole.class);
//		suite.addTestSuite(DeRegisterRole.class);
//		suite.addTestSuite(AllocateRole.class);
//		suite.addTestSuite(DeAllocateRole.class);
//		suite.addTestSuite(JointUnit.class);
//		suite.addTestSuite(LeaveRole.class);
		//suite.addTestSuite(InformAgentRole.class);
		suite.addTestSuite(InformMembers.class);
		//$JUnit-END$
		return suite;
	}

}
