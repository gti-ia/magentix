package omsTests;


import Thomas_example.TestThomas;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.SF;
import junit.framework.Test;
import junit.framework.TestSuite;

public class TestAllTests {

	OMS oms = null;
	SF sf = null;

	public void connect()
	{
		try
		{
			AgentsConnection.connect();


			OMS oms = new OMS(new AgentID("OMS"));

			SF sf =  new SF(new AgentID("SF"));

			oms.start();
			sf.start();
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void close()
	{

		oms.terminate();
		sf.terminate();
		
		oms = null;
		sf = null;
	}


	public static Test suite() {



		TestSuite suite = new TestSuite();
		//$JUnit-BEGIN$
		//suite.addTestSuite(TestThomas.class);

		suite.addTestSuite(TestRegisterUnitCorrectParam.class);
		suite.addTestSuite(TestRegisterUnitInCorrectParam.class);
		suite.addTestSuite(TestRegisterUnitInCorrectPermissions.class);

		suite.addTestSuite(TestDeRegisterUnit.class);
		suite.addTestSuite(TestDeRegisterUnitInCorrectPermissions.class);

		suite.addTestSuite(TestRegisterRole.class);
		suite.addTestSuite(TestRegisterRoleInCorrectParam.class);
		suite.addTestSuite(TestRegisterRoleInCorrectPermissions.class);

		suite.addTestSuite(TestDeRegisterRole.class);
		suite.addTestSuite(TestDeRegisterRoleInCorrectParam.class);
		suite.addTestSuite(TestDeRegisterRoleInCorrectPermissions.class);

		suite.addTestSuite(TestAcquireRole.class);
		suite.addTestSuite(TestAcquireRoleInCorrectParam.class);
		suite.addTestSuite(TestAcquireRoleInCorrectPermissions.class);

		suite.addTestSuite(TestLeaveRole.class);
		suite.addTestSuite(TestLeaveRoleInCorrectParam.class);
		suite.addTestSuite(TestLeaveRoleInCorrectPermissions.class);

		suite.addTestSuite(TestAllocateRole.class);
		suite.addTestSuite(TestAllocateRoleInCorrectParam.class);
		suite.addTestSuite(TestAllocateRoleInCorrectPermissions.class);

		suite.addTestSuite(TestDeAllocateRole.class);
		suite.addTestSuite(TestDeAllocateRoleInCorrectParam.class);
		suite.addTestSuite(TestDeAllocateRoleInCorrectPermissions.class);

		suite.addTestSuite(TestJointUnit.class);
		suite.addTestSuite(TestJointUnitInCorrectParam.class);
		suite.addTestSuite(TestJointUnitInCorrectPermissions.class);

		suite.addTestSuite(TestInformAgentRole.class);
		suite.addTestSuite(TestInformAgentRoleInCorrectParam.class);

		suite.addTestSuite(TestInformMembers.class);
		suite.addTestSuite(TestInformMembersInCorrectParam.class);

		suite.addTestSuite(TestQuantityMembers.class);
		suite.addTestSuite(TestQuantityMembersInCorrectParam.class);

		suite.addTestSuite(TestInformUnit.class);
		suite.addTestSuite(TestInformUnitInCorrectParam.class);
		suite.addTestSuite(TestInformUnitInCorrectPermissions.class);

		suite.addTestSuite(TestInformUnitRoles.class);
		suite.addTestSuite(TestInformUnitRoleInCorrectParam.class);

		suite.addTestSuite(TestInformRole.class);
		suite.addTestSuite(TestInformRoleInCorrectParam.class);
		suite.addTestSuite(TestInformRoleInCorrectPermissions.class);



		//$JUnit-END$
		return suite;
	}

}
