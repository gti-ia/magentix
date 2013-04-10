package organizational__message_example;






import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
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
	int count=0;


	public Summation(AgentID aid) throws Exception {
		super(aid);
	}

	protected void execution(CProcessor firstProcessor, ACLMessage welcomeMessage) {

		try {


			omsProxy.acquireRole("participant", "virtual");




			//------------------------------------------------------------------------
			//-----------------------Participant CFactory definition------------------
			//------------------------------------------------------------------------
			
			CFactory talk_Manager = new CFactory("RECEIVE_MANAGER", null, 1,this);
			
			//----------------------------BEGIN STATE----------------------------------
			BeginState BEGIN_MANAGER = (BeginState) talk_Manager.cProcessorTemplate().getState("BEGIN");
			
			//----------------------------wait STATE----------------------------------
			WaitState WAIT = new WaitState("WAIT", 0);
			talk_Manager.cProcessorTemplate().registerState(WAIT);
			talk_Manager.cProcessorTemplate().addTransition(BEGIN_MANAGER, WAIT);
			

			//------------------------------------------------------------------------
			//-----------------------Initiator CFactory definition------------------
			//------------------------------------------------------------------------

			MessageFilter filter = new MessageFilter("result = true");
			CFactory talk = new CFactory("SUMMATION_REQUEST", filter, 1,this);

			//----------------------------BEGIN STATE----------------------------------
			BeginState BEGIN = (BeginState) talk.cProcessorTemplate().getState("BEGIN");
			BEGIN.setMethod(new BEGIN_Method());

			
			//----------------------------REQUEST STATE----------------------------------
			SendState REQUEST = new SendState("REQUEST");
			REQUEST.setMethod(new REQUEST_Method());
			talk.cProcessorTemplate().registerState(REQUEST);
			talk.cProcessorTemplate().addTransition(BEGIN, REQUEST);

			//----------------------------WAIT STATE----------------------------------
			talk.cProcessorTemplate().registerState(WAIT);
			talk.cProcessorTemplate().addTransition(REQUEST, WAIT);

			//----------------------------NOT ACCEPTED STATE----------------------------------
			talk.cProcessorTemplate().registerState(new not_accepted());

			//----------------------------RECEIVE STATE----------------------------------
			ReceiveState RECEIVE = new ReceiveState("RECEIVE");
			RECEIVE.setAcceptFilter(filter); // null -> accept any message
			RECEIVE.setMethod(new RECEIVE_Method());
			talk.cProcessorTemplate().registerState(RECEIVE);
			talk.cProcessorTemplate().addTransition(WAIT, RECEIVE);
			talk.cProcessorTemplate().addTransition(RECEIVE, WAIT);


			//----------------------------SEND RESULT STATE----------------------------------
			SendState SEND_RESULT = new SendState("SEND_RESULT");
			talk.cProcessorTemplate().registerState(SEND_RESULT);
			SEND_RESULT.setMethod(new RESPONSE_Method());
			talk.cProcessorTemplate().addTransition(SEND_RESULT, REQUEST);
			talk.cProcessorTemplate().addTransition(RECEIVE, SEND_RESULT);
			
			//----------------------------SEND SHUTDOWN STATE----------------------------------
			SendState SEND_SHUTDOWN = new SendState("SEND_SHUTDOWN");
			talk.cProcessorTemplate().registerState(SEND_SHUTDOWN);
			SEND_SHUTDOWN.setMethod(new SEND_SHUTDOWN_Method());
			talk.cProcessorTemplate().addTransition(SEND_RESULT, SEND_SHUTDOWN);
			
			//----------------------------FINAL STATE----------------------------------
			FinalState FINAL = new FinalState("FINAL");
			FINAL.setMethod(new FINAL_Method());
			talk.cProcessorTemplate().registerState(FINAL);
			talk.cProcessorTemplate().addTransition(SEND_SHUTDOWN, FINAL);
			talk.cProcessorTemplate().addTransition(REQUEST, FINAL);
			talk.cProcessorTemplate().addTransition(RECEIVE, FINAL);

			



			this.addFactoryAsParticipant(talk_Manager);
			this.addFactoryAsInitiator(talk);


			this.startSyncConversation("SUMMATION_REQUEST");





		} catch (THOMASException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	public void addCount()
	{
		count++;
	}

	public int getCount()
	{
		return count;
	}
	public void setResult(int n)
	{
		result = n;
	}

	public void sumResult(int n)
	{
		result += n;
	}

	public int getResult()
	{
		return result;
	}

	class RECEIVE_MANAGER_Method implements ReceiveStateMethod {

		public String run(CProcessor myProcessor, ACLMessage messageReceived) {

			String state = "WAIT";

			return state;
		}

	}

	@Override
	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {
		System.out.println("["+firstProcessor.getMyAgent().getName()+"] end execution!");

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


	//	class BEGIN_MANAGER_Method implements BeginStateMethod {
	//
	//		public String run(CProcessor myProcessor, ACLMessage msg) {
	//
	//
	//			return "WAIT";
	//		};
	//
	//	}



	class BEGIN_Method implements BeginStateMethod {

		public String run(CProcessor myProcessor, ACLMessage msg) {


			return "REQUEST";
		};

	}



	class REQUEST_Method implements SendStateMethod {
		int n=0;

		public String run(CProcessor myProcessor, ACLMessage messageToSend) {


			OMSProxy omsProxy = new OMSProxy(myProcessor);

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


			((Summation)myProcessor.getMyAgent()).sumResult(Integer.parseInt(messageReceived.getContent()));

			if (((Summation)myProcessor.getMyAgent()).getCount() == 0)
			{
				if (n<1)
				{
					n++;
					state = "WAIT";
				}
			}


			return state;
		}

	}

	class RESPONSE_Method implements SendStateMethod {

		int n=0;

		public String run(CProcessor myProcessor, ACLMessage messageToSend) {


			ACLMessage msg;
			String state = "SEND_SHUTDOWN";
			OMSProxy omsProxy = new OMSProxy(myProcessor);

			try {
				msg = omsProxy.buildOrganizationalMessage("calculin");

				messageToSend.copyFromAsTemplate(msg);
				messageToSend.setPerformative(ACLMessage.INFORM);
				messageToSend.setLanguage("ACL");
				int content = ((Summation)myProcessor.getMyAgent()).getResult();
				((Summation)myProcessor.getMyAgent()).setResult(0);
				messageToSend.setContent(""+content);

				((Summation)myProcessor.getMyAgent()).addCount();

				if (n<1)
				{
					n++;
					state = "REQUEST";
				}

			} catch (THOMASException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				state = "SEND_SHUTDOWN";
			}
			return state;

		}

	}

	class SEND_SHUTDOWN_Method implements SendStateMethod
	{

		@Override
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			OMSProxy omsProxy = new OMSProxy(myProcessor);
			String state = "FINAL";
			try {	

				ACLMessage msg = omsProxy.buildOrganizationalMessage("calculin");
				messageToSend.copyFromAsTemplate(msg);

				messageToSend.setPerformative(InteractionProtocol.FIPA_REQUEST);
				messageToSend.setProtocol(InteractionProtocol.FIPA_REQUEST);
				messageToSend.setLanguage("ACL");
				messageToSend.setContent("shut down");

				messageToSend.setHeader("shutdown", "true");




			} catch (THOMASException e) {
				e.printStackTrace();

			}	
			return state;
		}

	}
	class FINAL_Method implements FinalStateMethod {
		public void run(CProcessor myProcessor, ACLMessage responseMessage) {

			OMSProxy omsProxy = new OMSProxy(myProcessor);

			try {

				omsProxy.leaveRole("manager", "calculin");

				omsProxy.leaveRole("participant", "virtual");

			} catch (THOMASException e) {
				e.printStackTrace();

			}	

			myProcessor.ShutdownAgent();

			
		}

	}

	//	class FINAL_MANAGER_Method implements FinalStateMethod {
	//		public void run(CProcessor myProcessor, ACLMessage responseMessage) {
	//
	//		}
	//
	//	}

}
