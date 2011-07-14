package MyService_example;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.log4j.xml.DOMConfigurator;

import org.mindswap.owls.OWLSFactory;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.process.execution.ProcessExecutionEngine;
import org.mindswap.query.ValueMap;

import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_REQUEST_Participant;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.Oracle;
import es.upv.dsic.gti_ia.organization.ProcessDescription;
import es.upv.dsic.gti_ia.organization.ProfileDescription;
import es.upv.dsic.gti_ia.organization.SFProxy;

public class CAgentProvider extends CAgent {

	// ************************** Variables
	// ****************************************************//
	private OMSProxy omsProxy = new OMSProxy(this);
	private SFProxy sfProxy = new SFProxy(this);

	ProfileDescription profile = new ProfileDescription(
			"http://localhost:8080/MyService/owl/owls/MyServiceProfile.owl",
			"MyService");
	ProcessDescription process = new ProcessDescription(
			"http://localhost:8080/MyService/owl/owls/MyServiceProcess.owl",
			"MyService");

	private ArrayList<String> results = new ArrayList<String>();
	private Oracle oracle;
	ValueMap values;

	/**
	 * Constructor
	 * 
	 * @param aid
	 * @throws Exception
	 */
	public CAgentProvider(AgentID aid) throws Exception {

		super(aid);

	}

	// ************************************ Methods
	// *********************************************//

	/**
	 * Acquire Role
	 */
	public void scenario1() {

		System.out.println("[AgentProvider] Acquire Role member in virtual: "
				+ omsProxy.acquireRole("member", "virtual"));

		omsProxy.registerUnit("myunit", "", "MyFirstUnit", "");

		omsProxy.registerRole("provider", "myunit", "", "", "", "");

		omsProxy.registerRole("customer", "myunit", "", "", "", "");

	}

	/**
	 * Register Profile Calculators
	 */
	public void scenario2() {
		sfProxy.registerProfile(profile);

		System.out
				.println("[AgentAnnoucement]The operation register Profile return: "
						+ profile.getServiceID() + "\n");
	}

	/**
	 * Search Profile MyService
	 */
	public void scenario3() {

		results = sfProxy.searchService("MyService");

		if (results.size() == 0) {
			System.out.println("no similar profiles");
		} else {

			String URLProfile = sfProxy.getProfile(results.get(0));

			URL profile;
			try {
				profile = new URL(URLProfile);
				oracle = new Oracle(profile);

			} catch (MalformedURLException e) {
				logger.error("ERROR: Profile URL Malformed!");
				e.printStackTrace();
			}

			System.out.println("[AgentProvider] Acquire Role "
					+ oracle.getProviderList().get(0)
					+ " in "
					+ oracle.getProviderUnitList().get(0)
					+ " :"
					+ omsProxy.acquireRole(oracle.getProviderList().get(0),
							oracle.getProviderUnitList().get(0)));

		}

	}

	/**
	 * Register Process
	 */
	public void scenario4() {

		process.setProfileID(results.get(0));

		System.out.println("[AgentProvider] RegisterProcess: "
				+ sfProxy.registerProcess(process));

	}

	class myFIPA_REQUEST extends FIPA_REQUEST_Participant {

		@Override
		protected String doAction(CProcessor myProcessor) {
			String next;
			ACLMessage inmsg = myProcessor.getLastReceivedMessage();
			// ACLMessage msg =
			// myProcessor.getLastReceivedMessage().createReply();

			// create an execution engine
			ProcessExecutionEngine exec = OWLSFactory.createExecutionEngine();

			try {
				Process aProcess = process.getProcess(inmsg);

				// initialize the input values to be empty
				ValueMap values = new ValueMap();

				values = process.getServiceRequestValues(inmsg);

				System.out.println("[Provider]Executing... "
						+ values.getValues().toString());
				values = exec.execute(aProcess, values);

				System.out.println("[Provider]Values obtained... :"
						+ values.toString());

				System.out
						.println("[Provider]Creating inform message to send...");

				// msg.setPerformative(ACLMessage.INFORM);

				System.out.println("[Provider]Before set message content...");
				// msg.setContent(aProcess.getLocalName() + "=" +
				// values.toString());
				myProcessor.getLastReceivedMessage().setContent(
						aProcess.getLocalName() + "=" + values.toString());
				next = "INFORM";

			} catch (Exception e) {

				System.out.println("EXCEPTION");
				System.out.println(e);
				e.printStackTrace();
				// msg.setPerformative(ACLMessage.FAILURE);
				next = "FAILURE";
			}
			return next;
		}

		@Override
		protected void doInform(CProcessor myProcessor, ACLMessage response) {
			ACLMessage lastReceivedMessage = myProcessor
					.getLastReceivedMessage();
			response.setContent(lastReceivedMessage.getContent());
		}

		@Override
		protected String doReceiveRequest(CProcessor myProcessor, ACLMessage msg) {
			String next;
			ACLMessage response = msg.createReply();
			values = new ValueMap();
			try {
				values = process.getServiceRequestValues(msg);

				if (values.size() == 1) {

					Process aProcess = process.getProcess(msg);
					System.out.println("AGREE");
					response.setPerformative(ACLMessage.AGREE);
					response.setContent(aProcess.getLocalName() + "=Agree");
					next = "AGREE";

				} else {

					System.out.println("NOTUNDERSTOOD");
					response.setPerformative(ACLMessage.NOT_UNDERSTOOD);
					response.setContent("NotUnderstood");
					next = "NOT_UNDERSTOOD";
				}

			} catch (Exception e) {

				System.out.println("EXCEPTION");
				System.out.println(e);
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());

			}

			System.out.println("[Provider]Sending First message:" + response);

			return (next);
		}
	}

	@Override
	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {
		// TODO Auto-generated method stub

		;
	}

	@Override
	protected void execution(CProcessor firstProcessor,
			ACLMessage welcomeMessage) {
		// TODO Auto-generated method stub

		DOMConfigurator.configure("configuration/loggin.xml");
		logger.info("Executing, I'm " + getName());

		CFactory talk = new myFIPA_REQUEST().newFactory("TALK", null, 0,
				firstProcessor.getMyAgent());

		// Finally the factory is setup to answer to incoming messages that
		// can start the participation of the agent in a new conversation
		this.addFactoryAsParticipant(talk);

		this.scenario1();
		this.scenario2();
		this.scenario3();
		this.scenario4();

		/*
		 * try { Thread.sleep(10 * 1000); } catch (InterruptedException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); }
		 * //this.removeFactory("TALK"); firstProcessor.ShutdownAgent();
		 */

	}

	public void finalize() {
		sfProxy.removeProvider(process);

		System.out.println(" deregister profile : " + profile.getServiceID()
				+ sfProxy.deregisterProfile(profile));

		System.out.println(" leave role : "
				+ omsProxy.leaveRole(oracle.getProviderList().get(0), oracle
						.getProviderUnitList().get(0)));

		System.out.println(" deregister role : "
				+ omsProxy.deregisterRole("provider", "myunit"));

		System.out.println(" deregister role : "
				+ omsProxy.deregisterRole("customer", "myunit"));

		System.out.println(" deregister unit : "
				+ omsProxy.deregisterUnit("myunit"));

		System.out.println(" leave role : "
				+ omsProxy.leaveRole("member", "virtual"));
	}

}