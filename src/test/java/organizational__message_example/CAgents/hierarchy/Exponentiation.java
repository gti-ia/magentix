package organizational__message_example.CAgents.hierarchy;
import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.cAgents.ActionState;
import es.upv.dsic.gti_ia.cAgents.ActionStateMethod;
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

public class Exponentiation extends CAgent {

	OMSProxy omsProxy = new OMSProxy(this);
	int result = 0;
	int expected = 2;
	Monitor m = new Monitor();

	public Exponentiation(AgentID aid) throws Exception {
		super(aid);
	}

	protected void execution(CProcessor firstProcessor, ACLMessage welcomeMessage) {

		try
		{
			omsProxy.acquireRole("participant", "virtual");


			MessageFilter filter = new MessageFilter("result = true");
			CFactory talk = new CFactory("EXPONENTIATION_REQUEST", filter, 1,this);
			
			
	//-----------------------------Participant factory-----------------------
			
			CFactory talk_Manager = new CFactory("RECEIVE_MANAGER", null, 1,this);
			BeginState BEGIN_MANAGER = (BeginState) talk_Manager.cProcessorTemplate().getState("BEGIN");
			BEGIN_MANAGER.setMethod(new BEGIN_MANAGER_Method());
			
			WaitState WAIT = new WaitState("WAIT", 0);
			
			talk_Manager.cProcessorTemplate().registerState(WAIT);
			talk_Manager.cProcessorTemplate().addTransition(BEGIN_MANAGER, WAIT);
			//Para capturar mensajes que vienen del otro agente manager.
			ReceiveState RECEIVE_MANAGER = new ReceiveState("RECEIVE_MANAGER");
			RECEIVE_MANAGER.setAcceptFilter(new MessageFilter("sender = agente_sumatorio")); // null -> accept any message
			RECEIVE_MANAGER.setMethod(new RECEIVE_MANAGER_Method());
			talk_Manager.cProcessorTemplate().registerState(RECEIVE_MANAGER);

			talk_Manager.cProcessorTemplate().addTransition(WAIT, RECEIVE_MANAGER);
			talk_Manager.cProcessorTemplate().addTransition(RECEIVE_MANAGER, WAIT);
			
			FinalState FINAL_MANAGER = new FinalState("FINAL");
			talk_Manager.cProcessorTemplate().registerState(FINAL_MANAGER);
			FINAL_MANAGER.setMethod(new FINAL_MANAGER_Method());
			
			
			

			//----------------------------BEGIN STATE----------------------------------
			BeginState BEGIN = (BeginState) talk.cProcessorTemplate().getState("BEGIN");
			BEGIN.setMethod(new BEGIN_Method());

			
			talk.cProcessorTemplate().registerState(WAIT);

			
			SendState REQUEST = new SendState("REQUEST");
			REQUEST.setMethod(new REQUEST_Method());
			talk.cProcessorTemplate().registerState(REQUEST);
			talk.cProcessorTemplate().addTransition(BEGIN, REQUEST);

			talk.cProcessorTemplate().registerState(new not_accepted());


			talk.cProcessorTemplate().addTransition(REQUEST, WAIT);

						

			ReceiveState RECEIVE = new ReceiveState("RECEIVE");
			RECEIVE.setAcceptFilter(filter); // null -> accept any message
			RECEIVE.setMethod(new RECEIVE_Method());
			talk.cProcessorTemplate().registerState(RECEIVE);

			talk.cProcessorTemplate().addTransition(WAIT, RECEIVE);
			talk.cProcessorTemplate().addTransition(RECEIVE, WAIT);


			SendState SEND_RESULT = new SendState("SEND_RESULT");
			ActionState LEAVE_MANAGER = new ActionState("LEAVE_MANAGER");
			FinalState FINAL = new FinalState("FINAL");
			
			talk.cProcessorTemplate().registerState(SEND_RESULT);
			talk.cProcessorTemplate().registerState(FINAL);
			talk.cProcessorTemplate().registerState(LEAVE_MANAGER);

			FINAL.setMethod(new FINAL_Method());			
			SEND_RESULT.setMethod(new RESPONSE_Method());
			LEAVE_MANAGER.setMethod(new LEAVE_MANAGER_Method());
			
			talk.cProcessorTemplate().addTransition(SEND_RESULT, FINAL);
			talk.cProcessorTemplate().addTransition(SEND_RESULT, LEAVE_MANAGER);
			talk.cProcessorTemplate().addTransition(LEAVE_MANAGER, REQUEST);
			talk.cProcessorTemplate().addTransition(LEAVE_MANAGER, FINAL);
			talk.cProcessorTemplate().addTransition(REQUEST, FINAL);
			talk.cProcessorTemplate().addTransition(RECEIVE, FINAL);

			talk.cProcessorTemplate().addTransition(RECEIVE, SEND_RESULT);



			this.addFactoryAsParticipant(talk_Manager);
			this.addFactoryAsInitiator(talk);

			// Finally Harry starts the conversation.
			this.startSyncConversation("EXPONENTIATION_REQUEST");




			System.out.println("Fuera");
		

		}catch(THOMASException e)
		{
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



	@Override
	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {
		
	
		

	}

	//------------------------------------------------------------------------
	//-----------------------CFactory implementation--------------------------
	//------------------------------------------------------------------------

	class BEGIN_MANAGER_Method implements BeginStateMethod {

		public String run(CProcessor myProcessor, ACLMessage msg) {


			return "WAIT";
		};

	}

	class FINAL_MANAGER_Method implements FinalStateMethod {
		public void run(CProcessor myProcessor, ACLMessage responseMessage) {

		}

	}
	class BEGIN_Method implements BeginStateMethod {

		public String run(CProcessor myProcessor, ACLMessage msg) {

			// In this example there is nothing more to do than continue
			// to the next state which will send the message.
			return "REQUEST";
		};

	}

	class REQUEST_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {

			String state = "WAIT";
			OMSProxy omsProxy = new OMSProxy(myProcessor);
			ACLMessage msg;
			System.out.println("["+myProcessor.getMyAgent().getAid().name+"]REQUEST");
			try {
				msg = omsProxy.buildOrganizationalMessage("calculin");

				messageToSend.copyFromAsTemplate(msg);

				messageToSend.setPerformative(ACLMessage.REQUEST);

				messageToSend.setLanguage("ACL");
				messageToSend.setContent(6+" "+3);
				



			} catch (THOMASException e) {
				System.out.println("[ "+myProcessor.getMyAgent().getName()+" ] "+ e.getContent());
				messageToSend.setReceiver(new AgentID("null"));
				messageToSend.setPerformative(ACLMessage.FAILURE);
				messageToSend.setContent(e.getContent());
				state="FINAL";
			}
			return state;
		}

	}

	class RECEIVE_MANAGER_Method implements ReceiveStateMethod {
		
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {

			String state = "WAIT";
			System.out.println("["+myProcessor.getMyAgent().getName()+"]Esto es lo que ha dicho el agente sumatorio: "+ messageReceived.getContent());
			return state;
		}

	}
	
	class RECEIVE_Method implements ReceiveStateMethod {
		int n=0;
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {

			String state = "SEND_RESULT";
			System.out.println("["+myProcessor.getMyAgent().getAid().name+"]RECEIVE: "+ messageReceived.getContent());
			((Exponentiation)myProcessor.getMyAgent()).sumResult(Integer.parseInt(messageReceived.getContent()) * Integer.parseInt(messageReceived.getContent()));

			if (n<1)
			{
				n++;
				state = "WAIT";
			}
			return state;
		}

	}

	class RESPONSE_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
		
			String state = "LEAVE_MANAGER";
			ACLMessage msg;
			OMSProxy omsProxy = new OMSProxy(myProcessor);
			System.out.println("["+myProcessor.getMyAgent().getAid().name+"]RESPONSE: ");
			try {
				msg = omsProxy.buildOrganizationalMessage("calculin");
				messageToSend.copyFromAsTemplate(msg);
				messageToSend.setPerformative(ACLMessage.INFORM);
				messageToSend.setLanguage("ACL");
				int content = ((Exponentiation)myProcessor.getMyAgent()).getResult();
				messageToSend.setContent(""+content);
			

				System.out.println("["+myProcessor.getMyAgent().getAid().name+"]RESPONSE: "+ content);



			} catch (THOMASException e) {
				System.out.println("[ "+myProcessor.getMyAgent().getName()+" ] "+ e.getContent());
				state="FINAL";
			}
			return state;

		}

	}
	
	class LEAVE_MANAGER_Method implements ActionStateMethod {
		@Override
		public String run(CProcessor myProcessor) {
			
			try {
				Thread.sleep(1 * 1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String state = "REQUEST";
			System.out.println("["+myProcessor.getMyAgent().getAid().name+"]LEAVE MANAGER: ");
			try
			{
				OMSProxy omsProxy = new OMSProxy(myProcessor);
				omsProxy.leaveRole("manager", "calculin");
			} catch (THOMASException e) {
				
				System.out.println("[ "+myProcessor.getMyAgent().getName()+" ] "+ e.getContent());
				state="FINAL";
			}
			return state;

		}

	}
	

	class FINAL_Method implements FinalStateMethod {
		public void run(CProcessor myProcessor, ACLMessage responseMessage) {
			System.out.println("["+myProcessor.getMyAgent().getAid().name+"]FINAL");
			
			OMSProxy omsProxy = new OMSProxy(myProcessor);
			try {
				omsProxy.leaveRole("participant", "virtual");
			} catch (THOMASException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			myProcessor.ShutdownAgent();

		}

	}












}
