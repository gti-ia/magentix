package omsTests;



import junit.framework.Test;
import junit.framework.TestSuite;

public class TestAllTests {



	


	public static Test suite() {



		TestSuite suite = new TestSuite();
		//$JUnit-BEGIN$
		//suite.addTestSuite(TestThomas.class);


		suite.addTestSuite(TestDeRegisterUnit.class);


		suite.addTestSuite(TestRegisterRole.class);


		suite.addTestSuite(TestDeRegisterRole.class);


		suite.addTestSuite(TestAcquireRole.class);


		suite.addTestSuite(TestLeaveRole.class);


		suite.addTestSuite(TestAllocateRole.class);
	

		suite.addTestSuite(TestDeAllocateRole.class);


		suite.addTestSuite(TestJointUnit.class);


		suite.addTestSuite(TestInformAgentRole.class);


		suite.addTestSuite(TestInformMembers.class);


		suite.addTestSuite(TestInformQuantityMembers.class);


		suite.addTestSuite(TestInformUnit.class);


		suite.addTestSuite(TestInformUnitRoles.class);


		suite.addTestSuite(TestInformRole.class);




		//$JUnit-END$
		return suite;
	}

}
