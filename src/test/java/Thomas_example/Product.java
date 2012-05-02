package Thomas_example;

/**
 * In this class the agent product is represented. 
 * Functions:
 *  -	Acquire role operation inside the unit calculator.
 *  - 	Register a new service square. This service is executed by the client.
 * 	-	Register a new service product. The function of this service is to multiply the values of entry and to return the result.
 *  - 	Provide the execution of the service product.
 *  - 	Implements a new FIPA REQUEST protocol in order to accept the client requests for the service product.
 *  
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_REQUEST_Participant;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.SFProxy;
import es.upv.dsic.gti_ia.organization.ServiceTools;
import es.upv.dsic.gti_ia.organization.THOMASException;

public class Product extends CAgent {

	OMSProxy omsProxy = new OMSProxy(this);
	SFProxy sfProxy = new SFProxy(this);
	String message;

	public Product(AgentID aid) throws Exception {
		super(aid);

	}
	
	public String getMessage()
	{
		return message;
	}

	protected void execution(CProcessor myProcessor, ACLMessage welcomeMessage) {

		try {

			CFactory informTalk = new myInform_Protocol().newFactory("Inform_TALK", 1, myProcessor.getMyAgent());
			this.addFactoryAsParticipant(informTalk);

			String result = omsProxy.acquireRole("operation", "calculator");
			logger.info("[" + this.getName() + "] Result acquire role operation: " + result);

			System.out.println("[" + this.getName() + "]" + " operation (calculator) role acquired");

			ArrayList<String> resultRegister = sfProxy
					.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");
			Iterator<String> iterRes = resultRegister.iterator();
			String registerRes = "";
			while (iterRes.hasNext()) {
				registerRes += iterRes.next() + "\n";
			}
			logger.info("[" + this.getName() + "] Result registerService: " + registerRes);

			resultRegister = sfProxy
					.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl");
			iterRes = resultRegister.iterator();
			registerRes = "";
			while (iterRes.hasNext()) {
				registerRes += iterRes.next() + "\n";
			}
			logger.info("[" + this.getName() + "] Result registerService: " + registerRes);

			System.out.println("[" + this.getName() + "] " + "Product and Square services registered. Waiting Request");

			CFactory additionTalk = new myFIPA_REQUEST().newFactory("PRODUCT_TALK", null, 0, myProcessor.getMyAgent());

			this.addFactoryAsParticipant(additionTalk);

		} catch (THOMASException e) {
			e.printStackTrace();
			message="ERROR";
		}

	}

	@Override
	protected void finalize(CProcessor firstProcessor, ACLMessage finalizeMessage) {
		System.out.println("[" + firstProcessor.getMyAgent().getName() + "] End execution");
		message="OK";
	}

	// ------------------------------------------------------------------------
	// -----------------------CFactory implementation--------------------------
	// ------------------------------------------------------------------------

	class myFIPA_REQUEST extends FIPA_REQUEST_Participant {

		ServiceTools st = new ServiceTools();
		HashMap<String, String> inputs = new HashMap<String, String>();
		String serviceName = "";

		@Override
		protected String doAction(CProcessor myProcessor) {
			String next = "";

			double result = 1;
			try {

				for (Entry<String, String> e : inputs.entrySet()) {

					result *= Double.parseDouble(e.getValue());
				}

				next = "INFORM";
				String resultXML = "";
				resultXML += "<serviceOutput>\n";
				resultXML += "<serviceName>" + serviceName + "</serviceName>\n";
				resultXML += "<outputs>\n";
				resultXML += "<Result>" + result + "</Result>\n";
				resultXML += "</outputs>\n";
				resultXML += "</serviceOutput>\n";

				myProcessor.getLastReceivedMessage().setContent(resultXML);

			} catch (Exception e) {
				next = "FAILURE";
				message="ERROR";
			}

			return next;
		}

		@Override
		protected void doInform(CProcessor myProcessor, ACLMessage response) {
			ACLMessage lastReceivedMessage = myProcessor.getLastReceivedMessage();
			response.setContent(lastReceivedMessage.getContent());
		}

		@Override
		protected String doReceiveRequest(CProcessor myProcessor, ACLMessage request) {
			String next = "";
			ACLMessage msg = request;

			if (msg != null) {

				try {

					inputs.clear();
					serviceName = st.extractServiceContent(msg.getContent(), inputs);

					if (serviceName.toLowerCase().contains("product")) {

						logger.info("AGREE");
						next = "AGREE";

					} else {

						logger.info("REFUSE");
						next = "REFUSE";
					}

				} catch (Exception e) {

					logger.info("EXCEPTION");
					System.out.println(e);
					e.printStackTrace();
					message="ERROR";
					throw new RuntimeException(e.getMessage());

				}

			} else {

				logger.info("NOTUNDERSTOOD");
				next = "NOT_UNDERSTOOD";
			}

			logger.info("[Product]Sending First message:" + next);

			return next;
		}
	}

	class myInform_Protocol extends ExampleEndedInform {

		@Override
		protected boolean doFinish(CProcessor myProcessor, ACLMessage msgReceived) {
			if (msgReceived.getHeaderValue("EXAMPLEENDED") != null)
				return true;
			else
				return false;
		}

		@Override
		protected void doDie(CProcessor myProcessor) {
			myProcessor.ShutdownAgent();

		}

	}

}
