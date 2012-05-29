package omsTests;


import Thomas_example.Thomas;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.SF;
import junit.framework.Test;
import junit.framework.TestSuite;

public class TestAllTests {

	public static Test suite() {
		
		AgentsConnection.connect();
		
		
		OMS oms = OMS.getOMS();
		
		SF sf = SF.getSF();
		
		oms.start();
		sf.start();
		
		TestSuite suite = new TestSuite();
		//$JUnit-BEGIN$
		suite.addTestSuite(Thomas.class);
		
		suite.addTestSuite(RegisterUnitCorrectParamTest.class);
		suite.addTestSuite(RegisterUnitInCorrectParamTest.class);
		suite.addTestSuite(RegisterUnitInCorrectPermissionsTest.class);
		
		suite.addTestSuite(DeRegisterUnit.class);
		suite.addTestSuite(DeRegisterUnitInCorrectPermissionsTest.class);
		
		suite.addTestSuite(RegisterRole.class);
		suite.addTestSuite(RegisterRoleInCorrectParamTest.class);
		suite.addTestSuite(RegisterRoleInCorrectPermissionsTest.class);
		
		suite.addTestSuite(DeRegisterRole.class);
		suite.addTestSuite(DeRegisterRoleInCorrectParamTest.class);
		suite.addTestSuite(DeRegisterRoleInCorrectPermissionsTest.class);
		
		suite.addTestSuite(AcquireRole.class);
		suite.addTestSuite(AcquireRoleInCorrectParamTest.class);
		suite.addTestSuite(AcquireRoleInCorrectPermissionsTest.class);
		
		suite.addTestSuite(LeaveRole.class);
		suite.addTestSuite(LeaveRoleInCorrectParamTest.class);
		suite.addTestSuite(LeaveRoleInCorrectPermissionsTest.class);
	
		suite.addTestSuite(AllocateRole.class);
		suite.addTestSuite(AllocateRoleInCorrectParamTest.class);
		suite.addTestSuite(AllocateRoleInCorrectPermissionsTest.class);
		
		suite.addTestSuite(DeAllocateRole.class);
		suite.addTestSuite(DeAllocateRoleInCorrectParamTest.class);
		suite.addTestSuite(DeAllocateRoleInCorrectPermissionsTest.class);
		
		suite.addTestSuite(JointUnit.class);
		suite.addTestSuite(JointUnitInCorrectParamTest.class);
		suite.addTestSuite(JointUnitInCorrectPermissionsTest.class);
		
		suite.addTestSuite(InformAgentRole.class);
		suite.addTestSuite(InformAgentRoleInCorrectParamTest.class);

		suite.addTestSuite(InformMembers.class);
		suite.addTestSuite(InformMembersInCorrectParamTest.class);
		
		suite.addTestSuite(QuantityMembers.class);
		suite.addTestSuite(QuantityMembersInCorrectParamTest.class);
		
		suite.addTestSuite(InformUnit.class);
		suite.addTestSuite(InformUnitInCorrectParamTest.class);
		suite.addTestSuite(InformUnitInCorrectPermissionsTest.class);
		
		suite.addTestSuite(InformUnitRoles.class);
		suite.addTestSuite(InformUnitRoleInCorrectParamTest.class);
		
		suite.addTestSuite(InformRole.class);
		suite.addTestSuite(InformRoleInCorrectParamTest.class);
		suite.addTestSuite(InformRoleInCorrectPermissionsTest.class);
				

		
		//$JUnit-END$
		return suite;
	}

}
