package organizational__message_example.CAgents.flat;

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


public class Noisy extends CAgent {


	OMSProxy omsProxy = new OMSProxy(this);




	public Noisy(AgentID aid) throws Exception {
		super(aid);

	}

	protected void execution(CProcessor firstProcessor, ACLMessage welcomeMessage) {

		try
		{
			OMSProxy omsProxy = new OMSProxy(this);


			String result = omsProxy.acquireRole("participant", "virtual");
			logger.info("["+this.getName()+"] Result acquire role participant: "+result);

			this.initialize_scenario();

			result = omsProxy.acquireRole("manager","externa");
			logger.info("["+this.getName()+"] Result acquire role manager: "+result);



			//------------------------------------------------------------------------
			//-----------------------CFactory definition------------------------------
			//------------------------------------------------------------------------

			CFactory talk_Manager = new CFactory("RECEIVE_MANAGER", null, 1,this);


			//----------------------------BEGIN STATE----------------------------------
			BeginState BEGIN_MANAGER = (BeginState) talk_Manager.cProcessorTemplate().getState("BEGIN");
			BEGIN_MANAGER.setMethod(new BEGIN_MANAGER_Method());

			//----------------------------WAIT STATE----------------------------------
			WaitState WAIT = new WaitState("WAIT", 0);
			talk_Manager.cProcessorTemplate().registerState(WAIT);
			talk_Manager.cProcessorTemplate().addTransition(BEGIN_MANAGER, WAIT);

			//----------------------------RECEIVE MANAGER STATE----------------------------------
			//In order to accept the message of the agent manager
			ReceiveState RECEIVE_MANAGER = new ReceiveState("RECEIVE_MANAGER");
			RECEIVE_MANAGER.setAcceptFilter(new MessageFilter("sender = agente_sumatorio")); // null -> accept any message
			RECEIVE_MANAGER.setMethod(new RECEIVE_MANAGER_Method());
			talk_Manager.cProcessorTemplate().registerState(RECEIVE_MANAGER);
			talk_Manager.cProcessorTemplate().addTransition(WAIT, RECEIVE_MANAGER);
			talk_Manager.cProcessorTemplate().addTransition(RECEIVE_MANAGER, WAIT);

			//----------------------------FINAL MANAGER STATE----------------------------------
			FinalState FINAL_MANAGER = new FinalState("FINAL_MANAGER");
			talk_Manager.cProcessorTemplate().registerState(FINAL_MANAGER);
			FINAL_MANAGER.setMethod(new FINAL_MANAGER_Method());





			//------------------------------------------------------------------------
			//-----------------------Initiator CFactory definition--------------------
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


			//----------------------------RECEIVE STATE----------------------------------
			ReceiveState RECEIVE = new ReceiveState("RECEIVE");
			RECEIVE.setAcceptFilter(filter); // null -> accept any message
			RECEIVE.setMethod(new RECEIVE_Method());
			talk.cProcessorTemplate().registerState(RECEIVE);
			talk.cProcessorTemplate().addTransition(WAIT, RECEIVE);
			talk.cProcessorTemplate().addTransition(RECEIVE, WAIT);



			//----------------------------FINAL STATE----------------------------------
			FinalState FINAL = new FinalState("FINAL");
			FINAL.setMethod(new FINAL_Method());
			talk.cProcessorTemplate().registerState(FINAL);
			talk.cProcessorTemplate().addTransition(REQUEST, FINAL);
			talk.cProcessorTemplate().addTransition(RECEIVE, FINAL);

			//----------------------------NOT ACCEPTED STATE----------------------------------
			talk.cProcessorTemplate().registerState(new not_accepted());


			this.addFactoryAsInitiator(talk);
			this.startSyncConversation("SUMMATION_REQUEST");

		}catch(THOMASException e)
		{
			e.printStackTrace();
		}

	}

	
	private void initialize_scenario()
	{

		try
		{
			String result = omsProxy.registerUnit("externa", "flat", "virtual", "creador");
			logger.info("["+this.getName()+"] Result register unit externa: "+ result);
			result = omsProxy.registerRole("manager", "externa", "internal", "private","member");
			logger.info("["+this.getName()+"] Result register role manager: "+ result);

		}catch(THOMASException e)
		{
			e.printStackTrace();
		}
	}
	
	
	//------------------------------------------------------------------------
	//-----------------------CFactory methods implementation------------------
	//------------------------------------------------------------------------
	
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
	class BEGIN_MANAGER_Method implements BeginStateMethod {

		public String run(CProcessor myProcessor, ACLMessage msg) {


			return "WAIT";
		};

	}

	class BEGIN_Method implements BeginStateMethod {

		public String run(CProcessor myProcessor, ACLMessage msg) {


			return "REQUEST";
		};

	}

	class RECEIVE_MANAGER_Method implements ReceiveStateMethod {

		public String run(CProcessor myProcessor, ACLMessage messageReceived) {

			String state = "FINAL_MANAGER";

			return state;
		}

	}

	class FINAL_MANAGER_Method implements FinalStateMethod {
		public void run(CProcessor myProcessor, ACLMessage responseMessage) {

		}

	}
	class REQUEST_Method implements SendStateMethod {


		public String run(CProcessor myProcessor, ACLMessage messageToSend) {


			OMSProxy omsProxy = new OMSProxy(myProcessor);

			ACLMessage msg;
			String state = "WAIT";
			try {

				omsProxy.allocateRole("creador", "externa", "agente_creador");

				msg = omsProxy.buildOrganizationalMessage("calculin");
				messageToSend.copyFromAsTemplate(msg);

				messageToSend.setPerformative(ACLMessage.REQUEST);
				messageToSend.setLanguage("ACL");

				messageToSend.setContent(1+" "+7);

				messageToSend.setHeader("organizational", "true");


			} catch (THOMASException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

				state="FINAL";
			}


			try {
				Thread.sleep(1*100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return state;
		}

	}

	class RECEIVE_Method implements ReceiveStateMethod {
		int n=0;

		public String run(CProcessor myProcessor, ACLMessage messageReceived) {

			String state = "FINAL";


			System.out.println("["+myProcessor.getMyAgent().getName()+"] Received: "+ messageReceived.getContent());


			return state;
		}

	}

	class FINAL_Method implements FinalStateMethod {
		public void run(CProcessor myProcessor, ACLMessage responseMessage) {

			OMSProxy omsProxy = new OMSProxy(myProcessor);

			try {

				omsProxy.leaveRole("manager", "externa");


				omsProxy.leaveRole("creador", "externa");


				omsProxy.leaveRole("participant", "virtual");




			} catch (THOMASException e) {
				e.printStackTrace();

			}	


			myProcessor.ShutdownAgent();

		}

	}



	@Override
	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {
		System.out.println("[ "+this.getName()+" ] end execution!");

	}


}
