package TestQueueAgent;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/**
 * Test class for RequestExample an example of FIPA Request protocol
 * 
 * @author David Fernández - dfernandez@dsic.upv.es
 */

public class TestRequest {

	Hospital hos;
	witness wit;
	Logger logger;
	Process qpid_broker;
	
	//Method before updating to junit4
	//
	//public TestRequest(String name) {
	//	super(name);
	//}
	
	@Before
	public void setUp() throws Exception {
		qpid_broker = qpidManager.UnixQpidManager.startQpid(Runtime.getRuntime(), qpid_broker);
		
		/**
		 * Setting the configuration
		 */
		DOMConfigurator.configure("configuration/loggin.xml");

		try {

			/**
			 * Connecting to Qpid Broker, default localhost.
			 */
			AgentsConnection.connect();

			/**
			 * Instantiating a Hospital agent
			 */
			hos = new Hospital(new AgentID("HospitalAgent"));

			/**
			 * Instantiating a witness agent
			 */
			wit = new witness(new AgentID("witnesAgent"));

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Testing hospital agree answer
	 */
	@Test(timeout = 5 * 1000)
	public void testAgreeAnswer() {
		hos.DISTANCIA_MAX = wit.witnessDistance + 1;// The hospital should reach
													// the accident
		hos.SUCCESS_PROB = 1;
		hos.start();
		wit.start();

		// If witness has not received answer wait
		while (wit.petitionResult.equalsIgnoreCase("") || !hos.finished()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		assertEquals(
				" !!!!Hospital "
						+ hos.getName()
						+ " It informs that they have gone out to attend to the accident.",
				wit.petitionResult);
	}

	/**
	 * Testing hospital inform answer
	 */
	@Test(timeout = 5 * 1000)
	public void testInformAnswer() {

		hos.DISTANCIA_MAX = wit.witnessDistance + 1;// The hospital should reach
													// the accident
		hos.SUCCESS_PROB = 1;
		hos.start();
		wit.start();

		// If witness has not received answer and hos hasn't finished, wait
		while (wit.informResult.equalsIgnoreCase("") || !hos.finished()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		assertEquals(" !!!!!!Hospital " + hos.getName()
				+ " It informs that they have attended to the accident.",
				wit.informResult);

	}

	/**
	 * Testing hospital refuse answer
	 */
	@Test(timeout = 5 * 1000)
	public void testRefuseAnswer() {
		hos.DISTANCIA_MAX = wit.witnessDistance - 1;// The hospital should NOT
													// reach the accident
		// Success probability is indifferent
		hos.start();
		wit.start();

		// If witness has not received answer and hos hasn't finished, wait
		while (wit.petitionResult.equalsIgnoreCase("") || !hos.finished()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		assertEquals(
				" !!!!Hospital "
						+ hos.getName()
						+ " It answers that the accident this one out of his radio of action. They will not come in time!!!",
				wit.petitionResult);
	}

	/**
	 * Testing hospital NotUnderstood answer
	 */
	@Test(timeout = 5 * 1000)
	public void testNotUnderstoodAnswer() {
		wit.content = "donkey to " + "10" + " km";
		// Now distance is indifferent
		// Success probability is indifferent
		hos.start();
		wit.start();

		// If witness has not received answer and hos hasn't finished, wait
		while (wit.petitionResult.equalsIgnoreCase("") || !hos.finished()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		assertEquals(" !!!!Hospital " + hos.getName()
				+ " It cannot understand the message.", wit.petitionResult);
	}

	/**
	 * Testing not existing Hospital failure
	 * 
	 * Skiped test because witness waits forever to the response of hospital not
	 * making the failure appear
	 */
	/*
	 * 	@Test(timeout = 5 * 1000)
	 * 
	 * @Ignore public void testNoHospitalFailure(){ //Now distance is
	 * indifferent //Success probability is indifferent //hos.start(); There is
	 * no hospital running wit.start();
	 * 
	 * //If witness has not received answer and hos hasn't finished, wait
	 * while(wit.petitionResult.equalsIgnoreCase("")) {
	 * System.out.println("RESULT:"+wit.petitionResult); try {
	 * Thread.sleep(100); } catch (InterruptedException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } }
	 * 
	 * assertEquals(" Someone of the hospitals does not exist",
	 * wit.petitionResult); }
	 */

	/**
	 * Testing not saving the user failure
	 */
	@Test(timeout = 5 * 1000)
	public void testNotSavedFailure() {
		hos.DISTANCIA_MAX = wit.witnessDistance + 1;// The hospital should reach
													// the accident
		hos.SUCCESS_PROB = 0;// They will not save the user
		hos.start();
		wit.start();

		// If witness has not received answer and hos hasn't finished, wait
		while (wit.petitionResult.equalsIgnoreCase("") || !hos.finished()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		assertEquals(" I fail in the hospital " + hos.getName()
				+ ": They have done everything possible", wit.petitionResult);
	}
	@After
	public void tearDown() throws Exception {

        //otras cosas que hacer...

        AgentsConnection.disconnect();

        qpidManager.UnixQpidManager.stopQpid(qpid_broker);

    }
}
