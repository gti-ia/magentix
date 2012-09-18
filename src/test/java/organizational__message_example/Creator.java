package organizational__message_example;

import java.util.ArrayList;

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
import es.upv.dsic.gti_ia.cAgents.WaitState;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.MessageFilter;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.exception.THOMASException;


public class Creator extends CAgent {

	OMSProxy omsProxy = new OMSProxy(this);
	Monitor m = new Monitor();

	public Creator(AgentID aid) throws Exception {
		super(aid);
	}

	protected void execution(CProcessor firstProcessor, ACLMessage welcomeMessage) {


		try
		{
			String result = omsProxy.acquireRole("participant", "virtual");
			logger.info("["+this.getName()+"] Result acquire role participant: "+result);

			this.initialize_scenario();

			this.sendRequest(4, 6);

			omsProxy.acquireRole("manager", "calculin");
			MessageFilter filter_shutdown = new MessageFilter("shutdown = true");




			//------------------------------------------------------------------------
			//-----------------------CFactory definition------------------------------
			//------------------------------------------------------------------------
			CFactory talk = new CFactory("SUMMATION_REQUEST", null, 1,this);

			
			//----------------------------BEGIN STATE----------------------------------
			BeginState BEGIN = (BeginState) talk.cProcessorTemplate().getState("BEGIN");
			BEGIN.setMethod(new BEGIN_Method());
			talk.cProcessorTemplate().registerState(BEGIN);

			//----------------------------WAIT STATE----------------------------------
			WaitState WAIT = new WaitState("WAIT", 0);
			talk.cProcessorTemplate().registerState(WAIT);
			talk.cProcessorTemplate().addTransition(BEGIN, WAIT);

			//----------------------------RECEIVE SHUTDONW STATE----------------------------------
			ReceiveState RECEIVE_SHUTDOWN = new ReceiveState("RECEIVE_SHUTDOWN");
			RECEIVE_SHUTDOWN.setAcceptFilter(filter_shutdown);
			RECEIVE_SHUTDOWN.setMethod(new RECEIVE_SHUTDOWN_Method());
			talk.cProcessorTemplate().registerState(RECEIVE_SHUTDOWN);
			talk.cProcessorTemplate().addTransition(WAIT, RECEIVE_SHUTDOWN);
			
			//----------------------------RECEIVE GENERIC STATE----------------------------------
			ReceiveState RECEIVE_GENERIC = new ReceiveState("RECEIVE_GENERIC");
			RECEIVE_GENERIC.setAcceptFilter(new MessageFilter("performative = REQUEST OR performative = INFORM"));
			RECEIVE_GENERIC.setMethod(new RECEIVE_GENERIC_Method());
			talk.cProcessorTemplate().registerState(RECEIVE_GENERIC);
			talk.cProcessorTemplate().addTransition(WAIT, RECEIVE_GENERIC);


			//----------------------------FINAL STATE----------------------------------
			FinalState FINAL = new FinalState("FINAL");
			talk.cProcessorTemplate().addTransition(RECEIVE_GENERIC, FINAL);
			FINAL.setMethod(new FINAL_Method());
			talk.cProcessorTemplate().registerState(FINAL);

			
			//----------------------------FINAL SHUTDOWN STATE----------------------------------
			FinalState FINAL_SHUTDOWN = new FinalState("FINAL_SHUTDOWN");
			talk.cProcessorTemplate().registerState(FINAL_SHUTDOWN);
			FINAL_SHUTDOWN.setMethod(new FINAL_SHUTDOWN_Method()); 
			talk.cProcessorTemplate().addTransition(RECEIVE_SHUTDOWN, FINAL_SHUTDOWN);


			

			this.addFactoryAsParticipant(talk);





		}catch(THOMASException e)
		{
			e.printStackTrace();
		}

	}

	private void sendRequest(int i, int j)
	{
		try {
			ACLMessage msg = omsProxy.buildOrganizationalMessage("calculin");

			msg.setPerformative(InteractionProtocol.FIPA_REQUEST);
			msg.setProtocol(InteractionProtocol.FIPA_REQUEST);
			msg.setLanguage("ACL");

			msg.setContent(4+" "+2);

			send(msg);


		} catch (THOMASException e) {

			System.out.println("[ "+this.getName()+" ] "+ e.getContent());

		}
	}

	private void initialize_scenario()
	{
		try
		{
			omsProxy.registerUnit("calculin", "team", "virtual", "creador");


			omsProxy.registerRole("manager", "calculin",  "internal", "private","member");


			omsProxy.registerRole("operador", "calculin", "external", "public","member");


			omsProxy.allocateRole("manager", "calculin", "agente_visor");


			omsProxy.allocateRole("manager", "calculin", "agente_sumatorio");




		}catch(THOMASException e)
		{
			System.out.println("["+this.getName()+"] "+ e.getContent());
		}

	}



	@Override
	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {
		System.out.println("["+firstProcessor.getMyAgent().getName()+"] end execution!");	 

	}


	//------------------------------------------------------------------------
	//-----------------------CFactory implementation--------------------------
	//------------------------------------------------------------------------



	class BEGIN_Method implements BeginStateMethod {

		public String run(CProcessor myProcessor, ACLMessage msg) {

			// In this example there is nothing more to do than continue
			// to the next state which will send the message.

			return "WAIT";
		};

	}



	class RECEIVE_SHUTDOWN_Method implements ReceiveStateMethod {

		public String run(CProcessor myProcessor, ACLMessage messageReceived) {

			String state = "FINAL_SHUTDOWN";

			return state;
		}

	}

	class RECEIVE_GENERIC_Method implements ReceiveStateMethod {

		public String run(CProcessor myProcessor, ACLMessage messageReceived) {

			String state = "FINAL";

			return state;
		}

	}

	class FINAL_Method implements FinalStateMethod {

		@Override
		public void run(CProcessor myProcessor, ACLMessage messageToSend) {
			// TODO Auto-generated method stub

		}

	}

	class FINAL_SHUTDOWN_Method implements FinalStateMethod {
		public void run(CProcessor myProcessor, ACLMessage responseMessage) {



			try{




				omsProxy.joinUnit("externa", "calculin");

				boolean searching = true;
				do{
					int quantity = omsProxy.quantityMembers("calculin", "", "member");

					if (quantity > 2)
						m.waiting(3 * 1000);
					else
						searching = false;
				}while(searching);

				omsProxy.leaveRole("manager", "calculin");

				ArrayList<ArrayList<String>> members = omsProxy.informMembers("calculin", "", "member");


				for(ArrayList<String> member : members)
				{

					omsProxy.deallocateRole(member.get(1), "calculin", member.get(0));
				}

				omsProxy.deregisterRole("operador", "calculin");


				omsProxy.deregisterRole("manager", "calculin");




				do
				{
					m.waiting(3 * 1000);

					members = omsProxy.informMembers("externa", "manager", "");
				}while(members.contains("agente_ruidoso"));


				omsProxy.deregisterUnit("externa");


				omsProxy.deregisterUnit("calculin");


				omsProxy.leaveRole("participant", "virtual");

			}catch(THOMASException e)
			{
				e.printStackTrace();
			}

			myProcessor.ShutdownAgent();

		}

	}

}
