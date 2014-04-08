package TestContractNet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.CountDownLatch;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

public class TestContractNet {

	Client cliente = null;
	private Process qpid_broker;
	CountDownLatch finished = new CountDownLatch(1);

	@Before
	public void setUp() throws Exception {

		qpid_broker = qpidManager.UnixQpidManager.startQpid(
				Runtime.getRuntime(), qpid_broker);
		/**
		 * Setting the Logger
		 */
		// DOMConfigurator.configure("configuration/loggin.xml");

		/**
		 * Connecting to Qpid Broker
		 */
		AgentsConnection.connect();

		try {
			for (int i = 0; i < 5; i++) {
				/**
				 * Instantiating a consumer agent
				 */
				Concessionaire concesionario = new Concessionaire(new AgentID(
						"Autos" + i), finished);
				/**
				 * Execute the agents
				 */
				concesionario.start();
			}

			/**
			 * Instantiating a sender agent
			 */
			cliente = new Client(new AgentID("Client"), finished);

		} catch (Exception e) {
			fail("Exception " + e.getMessage());
		}

	}

	@Test(timeout = 30000)
	public void testContractNet() {

		/**
		 * Execute the agents
		 */
		cliente.start();

		// while (cliente.getState() == null) {
		//
		// try {
		// Thread.sleep(1 * 1000);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }

		try {
			finished.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (cliente.getState().equals("inform"))
			assertEquals("Sending purchase contract.", cliente.getStatus());
		if (cliente.getState().equals("failure"))
			assertEquals("Error on having sent contract.", cliente.getStatus());

	}

	@After
	public void tearDown() throws Exception {

		AgentsConnection.disconnect();
		qpidManager.UnixQpidManager.stopQpid(qpid_broker);
	}

}
