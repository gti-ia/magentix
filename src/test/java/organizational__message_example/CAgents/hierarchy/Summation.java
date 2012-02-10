package organizational__message_example.CAgents.hierarchy;

import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.cAgents.BeginState;
import es.upv.dsic.gti_ia.cAgents.BeginStateMethod;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.FinalState;
import es.upv.dsic.gti_ia.cAgents.FinalStateMethod;
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
			omsProxy.acquireRole("participant", "virtual");

			MessageFilter filter = new MessageFilter("performative = REQUEST");
			CFactory talk = new CFactory("EXPONENTIATION_REQUEST", filter, 1,this);

			//----------------------------BEGIN STATE----------------------------------
			BeginState BEGIN = (BeginState) talk.cProcessorTemplate().getState("BEGIN");
			BEGIN.setMethod(new BEGIN_Method());

			
			SendState REQUEST = new SendState("REQUEST");
			REQUEST.setMethod(new REQUEST_Method());
			talk.cProcessorTemplate().registerState(REQUEST);
			talk.cProcessorTemplate().addTransition(BEGIN, REQUEST);
			

			WaitState WAIT = new WaitState("WAIT", 1000);
			talk.cProcessorTemplate().registerState(WAIT);
			
			talk.cProcessorTemplate().addTransition(REQUEST, WAIT);


			ReceiveState RECEIVE = new ReceiveState("RECEIVE");
			RECEIVE.setAcceptFilter(null); // null -> accept any message
			RECEIVE.setMethod(new RECEIVE_Method());

			talk.cProcessorTemplate().registerState(RECEIVE);
			talk.cProcessorTemplate().addTransition(WAIT, RECEIVE);
			talk.cProcessorTemplate().addTransition(RECEIVE, WAIT);
			
			
			SendState SEND_RESULT = new SendState("SEND_RESULT");
			FinalState FINAL = new FinalState("FINAL");
			talk.cProcessorTemplate().registerState(RECEIVE);
			
			SEND_RESULT.setMethod(new RESPONSE_Method());
			
			talk.cProcessorTemplate().addTransition(SEND_RESULT, FINAL);
			talk.cProcessorTemplate().addTransition(SEND_RESULT, REQUEST);
			
			talk.cProcessorTemplate().addTransition(RECEIVE, SEND_RESULT);
			
			FINAL.setMethod(new FINAL_Method());

			talk.cProcessorTemplate().registerState(FINAL);

			
			this.addFactoryAsInitiator(talk);
			
			 // Finally Harry starts the conversation.
			this.startSyncConversation("EXPONENTIATION_REQUEST");

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



	@Override
	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {
		// TODO Auto-generated method stub
		
	}
	
	
	//------------------------------------------------------------------------
	//-----------------------CFactory implementation--------------------------
	//------------------------------------------------------------------------



	class BEGIN_Method implements BeginStateMethod {

		public String run(CProcessor myProcessor, ACLMessage msg) {

			// In this example there is nothing more to do than continue
			// to the next state which will send the message.
			return "REQUEST";
		};

	}

	class REQUEST_Method implements SendStateMethod {
		int n=0;
		
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			CAgent myAgent = myProcessor.getMyAgent();
			OMSProxy omsProxy = new OMSProxy(myAgent);
			ACLMessage msg;
			try {
				msg = omsProxy.buildOrganizationalMessage("calculin");

				msg.setPerformative(InteractionProtocol.FIPA_REQUEST);
				msg.setProtocol(InteractionProtocol.FIPA_REQUEST);
				msg.setLanguage("ACL");
				if (n<1)
				{
					msg.setContent(6+" "+3);
					n++;
				}
				else
					msg.setContent(5+" "+3);
				messageToSend = msg;


			} catch (THOMASException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "WAIT";
		}

	}

	class RECEIVE_Method implements ReceiveStateMethod {
		int n=0;
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {

			String state = "SEND_RESULT";
			((Exponentiation)myProcessor.getMyAgent()).sumResult(Integer.parseInt(messageReceived.getContent()));
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
			
			try {
				msg = omsProxy.buildOrganizationalMessage("calculin");

				msg.setPerformative(ACLMessage.INFORM);
				msg.setLanguage("ACL");
				int content = ((Exponentiation)myProcessor.getMyAgent()).getResult();
				msg.setContent(""+content);
				messageToSend = msg;
				
				if (n<1)
				{
					n++;
					state = "REQUEST";
				}
				
			} catch (THOMASException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return state;

		}

	}
	
	class FINAL_Method implements FinalStateMethod {
		public void run(CProcessor myProcessor, ACLMessage responseMessage) {
			
			try {
				ACLMessage msg = omsProxy.buildOrganizationalMessage("calculin");
				msg.setPerformative(InteractionProtocol.FIPA_REQUEST);
				msg.setProtocol(InteractionProtocol.FIPA_REQUEST);
				msg.setLanguage("ACL");
				msg.setContent("shut down");

				responseMessage = msg;
				
				
				
			} catch (THOMASException e) {
				e.printStackTrace();

			}	
			
		
		myProcessor.ShutdownAgent();
		
		}
		
		}

}
