package organizational__message_example.CAgents.hierarchy;



import org.omg.PortableServer.POAManagerPackage.State;

import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.cAgents.BeginState;
import es.upv.dsic.gti_ia.cAgents.BeginStateMethod;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.FinalState;
import es.upv.dsic.gti_ia.cAgents.FinalStateMethod;
import es.upv.dsic.gti_ia.cAgents.NotAcceptedMessagesState;
import es.upv.dsic.gti_ia.cAgents.ReceiveState;
import es.upv.dsic.gti_ia.cAgents.ReceiveStateMethod;
import es.upv.dsic.gti_ia.cAgents.SendState;
import es.upv.dsic.gti_ia.cAgents.SendStateMethod;
import es.upv.dsic.gti_ia.cAgents.WaitState;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.MessageFilter;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.THOMASException;

public class Summation extends CAgent {

	OMSProxy omsProxy = new OMSProxy(this);
	int result = 0;
	int expected = 2;
	Monitor m = new Monitor();

	public Summation(AgentID aid) throws Exception {
		super(aid);
	}

	protected void execution(CProcessor firstProcessor, ACLMessage welcomeMessage) {

		try {

			logger.info("pool");

			omsProxy.acquireRole("participant", "virtual");

			MessageFilter filter = new MessageFilter("performative = INFORM");
			CFactory talk = new CFactory("SUMMATION_REQUEST", filter, 1,this);

			//----------------------------BEGIN STATE----------------------------------
			BeginState BEGIN = (BeginState) talk.cProcessorTemplate().getState("BEGIN");
			BEGIN.setMethod(new BEGIN_Method());


			SendState REQUEST = new SendState("REQUEST");

			REQUEST.setMethod(new REQUEST_Method());
			talk.cProcessorTemplate().registerState(REQUEST);
			talk.cProcessorTemplate().addTransition(BEGIN, REQUEST);


			WaitState WAIT = new WaitState("WAIT", 0);
			talk.cProcessorTemplate().registerState(WAIT);

			talk.cProcessorTemplate().addTransition(REQUEST, WAIT);
			
		
			

			
			talk.cProcessorTemplate().registerState(new not_accepted());
			
			

			ReceiveState RECEIVE = new ReceiveState("RECEIVE");
			RECEIVE.setAcceptFilter(filter); // null -> accept any message
			RECEIVE.setMethod(new RECEIVE_Method());
			talk.cProcessorTemplate().registerState(RECEIVE);


			talk.cProcessorTemplate().addTransition(WAIT, RECEIVE);
			talk.cProcessorTemplate().addTransition(RECEIVE, WAIT);


			SendState SEND_RESULT = new SendState("SEND_RESULT");
			FinalState FINAL = new FinalState("FINAL");

			talk.cProcessorTemplate().registerState(SEND_RESULT);
			FINAL.setMethod(new FINAL_Method());

			talk.cProcessorTemplate().registerState(FINAL);

			SEND_RESULT.setMethod(new RESPONSE_Method());

			talk.cProcessorTemplate().addTransition(SEND_RESULT, FINAL);
			talk.cProcessorTemplate().addTransition(SEND_RESULT, REQUEST);
			talk.cProcessorTemplate().addTransition(REQUEST, FINAL);
			talk.cProcessorTemplate().addTransition(RECEIVE, FINAL);

			talk.cProcessorTemplate().addTransition(RECEIVE, SEND_RESULT);




			this.addFactoryAsInitiator(talk);
			//	this.send_request(5,6);
			// Finally Harry starts the conversation.
			this.startSyncConversation("SUMMATION_REQUEST");


			System.out.println("ACABE");
			omsProxy.leaveRole("manager", "calculin");

			omsProxy.leaveRole("participant", "virtual");

		} catch (THOMASException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}



	public void sumResult(int n)
	{
		result += n;
	}

	public int getResult()
	{
		return result;
	}


	private void send_request(int n1, int n2) {

		ACLMessage msg = new ACLMessage();
		msg.setPerformative(InteractionProtocol.FIPA_REQUEST);
		msg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		msg.setLanguage("ACL");
		msg.setContent(n1+" "+n2);
		msg.setSender(this.getAid());
		msg.setReceiver(new AgentID("agente_suma"));

		send(msg);

	}

	@Override
	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {
		// TODO Auto-generated method stub

	}


	class not_accepted extends NotAcceptedMessagesState
	{
	
		String content;
		@Override
		protected int run(ACLMessage exceptionMessage, String next) {
			
			return NotAcceptedMessagesState.IGNORE;
		}

		@Override
		protected String getNext(String previousState) {
			// TODO Auto-generated method stub
			String state = "WAIT";
			
			return state;
		}
		
	}
	//------------------------------------------------------------------------
	//-----------------------CFactory implementation--------------------------
	//------------------------------------------------------------------------




	class BEGIN_Method implements BeginStateMethod {

		public String run(CProcessor myProcessor, ACLMessage msg) {


			return "REQUEST";
		};

	}



	class REQUEST_Method implements SendStateMethod {
		int n=0;

		public String run(CProcessor myProcessor, ACLMessage messageToSend) {


			OMSProxy omsProxy = new OMSProxy(myProcessor);
			logger.info("pool");
			ACLMessage msg;
			String state = "WAIT";
			try {
				msg = omsProxy.buildOrganizationalMessage("calculin");
				messageToSend.copyFromAsTemplate(msg);

				messageToSend.setPerformative(ACLMessage.REQUEST);
				messageToSend.setLanguage("ACL");
				if (n<1)
					messageToSend.setContent(6+" "+3);
				else
					messageToSend.setContent(5+" "+3);

				System.out.println("----------------");
				System.out.println("["+myProcessor.getMyAgent().getAid().name+"] Message to send: "+ messageToSend.getContent());
				messageToSend.setHeader("organizational", "true");
				n++;

			} catch (THOMASException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

				state="FINAL";
			}



			return state;
		}

	}

	class RECEIVE_Method implements ReceiveStateMethod {
		int n=0;
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {

			String state = "SEND_RESULT";
			logger.info("pool");
			System.out.println("["+myProcessor.getMyAgent().getAid().name+"]Mensaje recibido: "+ messageReceived.getContent());
			((Summation)myProcessor.getMyAgent()).sumResult(Integer.parseInt(messageReceived.getContent()));
			if (n<1)
			{
				n++;
				state = "WAIT";
			}
			return state;
		}

	}

	class RESPONSE_Method implements SendStateMethod {

		int n=0;

		public String run(CProcessor myProcessor, ACLMessage messageToSend) {


			ACLMessage msg;
			String state = "FINAL";
			OMSProxy omsProxy = new OMSProxy(myProcessor);
			logger.info("pool");
			try {
				msg = omsProxy.buildOrganizationalMessage("calculin");

				messageToSend.copyFromAsTemplate(msg);
				messageToSend.setPerformative(ACLMessage.INFORM);
				messageToSend.setLanguage("ACL");
				int content = ((Summation)myProcessor.getMyAgent()).getResult();
				messageToSend.setContent(""+content);
				
				System.out.println("["+myProcessor.getMyAgent().getAid().name+"]Response");
				if (n<1)
				{
					n++;
					state = "REQUEST";
				}

			} catch (THOMASException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				state = "FINAL";
			}
			return state;

		}

	}

	class FINAL_Method implements FinalStateMethod {
		public void run(CProcessor myProcessor, ACLMessage responseMessage) {

			OMSProxy omsProxy = new OMSProxy(myProcessor);

			try {
				ACLMessage msg = omsProxy.buildOrganizationalMessage("calculin");
				responseMessage.copyFromAsTemplate(msg);
				
				responseMessage.setPerformative(InteractionProtocol.FIPA_REQUEST);
				responseMessage.setProtocol(InteractionProtocol.FIPA_REQUEST);
				responseMessage.setLanguage("ACL");
				responseMessage.setContent("shut down");
				
				responseMessage.setHeader("shutdown", "true");

				

				responseMessage = msg;
				System.out.println("["+myProcessor.getMyAgent().getAid().name+"]Shutdown");


			} catch (THOMASException e) {
				e.printStackTrace();

			}	


			myProcessor.ShutdownAgent();

		}

	}

}
