package organizational__message_example.CAgents.hierarchy;

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
import es.upv.dsic.gti_ia.cAgents.SendState;
import es.upv.dsic.gti_ia.cAgents.SendStateMethod;
import es.upv.dsic.gti_ia.cAgents.WaitState;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.MessageFilter;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.THOMASException;


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

			MessageFilter filter_shutdown = new MessageFilter("shutdown = true");
			MessageFilter filter_generic = new MessageFilter("shutdown = false");
			
			
			
			
			CFactory talk = new CFactory("SUMMATION_REQUEST", null, 1,this);

			//----------------------------BEGIN STATE----------------------------------
			BeginState BEGIN = (BeginState) talk.cProcessorTemplate().getState("BEGIN");
			BEGIN.setMethod(new BEGIN_Method());
			talk.cProcessorTemplate().registerState(BEGIN);


			
			
			
			


			WaitState WAIT = new WaitState("WAIT", 0);
			
			talk.cProcessorTemplate().registerState(WAIT);
			talk.cProcessorTemplate().addTransition(BEGIN, WAIT);
			

		


			ReceiveState RECEIVE_SHUTDOWN = new ReceiveState("RECEIVE_SHUTDOWN");
			ReceiveState RECEIVE_GENERIC = new ReceiveState("RECEIVE_GENERIC");
			
			RECEIVE_SHUTDOWN.setAcceptFilter(filter_shutdown);
			RECEIVE_GENERIC.setAcceptFilter(filter_generic);
			
			RECEIVE_SHUTDOWN.setMethod(new RECEIVE_SHUTDOWN_Method());
			RECEIVE_GENERIC.setMethod(new RECEIVE_GENERIC_Method());

			talk.cProcessorTemplate().registerState(RECEIVE_GENERIC);
			talk.cProcessorTemplate().registerState(RECEIVE_SHUTDOWN);
			
			talk.cProcessorTemplate().addTransition(WAIT, RECEIVE_SHUTDOWN);
			talk.cProcessorTemplate().addTransition(WAIT, RECEIVE_GENERIC);


			
			FinalState FINAL = new FinalState("FINAL");
			
			talk.cProcessorTemplate().addTransition(RECEIVE_GENERIC, WAIT);
			talk.cProcessorTemplate().addTransition(RECEIVE_SHUTDOWN, FINAL);
			

			FINAL.setMethod(new FINAL_Method());

			talk.cProcessorTemplate().registerState(FINAL);


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
			omsProxy.registerUnit("calculin", "hierarchy", "virtual", "creador");
			

			omsProxy.registerRole("manager", "calculin",  "internal", "private","supervisor");
			
			
			omsProxy.registerRole("operador", "calculin", "external", "public","subordinate");
			
			
			omsProxy.allocateRole("manager", "calculin", "agente_visor");
//			
//			
	//		omsProxy.allocateRole("manager", "calculin", "agente_sumatorio");
//			
//			
			omsProxy.allocateRole("manager", "calculin", "agente_sumaPotencias");
			
		}catch(THOMASException e)
		{
			e.printStackTrace();
		}

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
			System.out.println("BEGIN");
			return "WAIT";
		};

	}



	class RECEIVE_SHUTDOWN_Method implements ReceiveStateMethod {

		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			System.out.println("RECEIVE");
			String state = "FINAL";
		
			return state;
		}

	}
	
	class RECEIVE_GENERIC_Method implements ReceiveStateMethod {

		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			System.out.println("RECEIVE");
			String state = "WAIT";
		
			return state;
		}

	}



	class FINAL_Method implements FinalStateMethod {
		public void run(CProcessor myProcessor, ACLMessage responseMessage) {


			System.out.println("FINAL");
			try{
				
			
			omsProxy.acquireRole("manager", "calculin");
			
			omsProxy.jointUnit("externa", "calculin");
			
			boolean searching = true;
			do{
				int quantity = omsProxy.quantityMembers("calculin", "", "supervisor");
			
				if (quantity > 1)
					m.waiting(3 * 1000);
				else
					searching = false;
			}while(searching);
			
			omsProxy.leaveRole("manager", "calculin");
			
			ArrayList<ArrayList<String>> members = omsProxy.informMembers("calculin", "", "subordinate");
			
			for(ArrayList<String> member : members)
			{
				
				omsProxy.deallocateRole(member.get(1), "calculin", member.get(0));
			}
			
			omsProxy.deregisterRole("operador", "calculin");
			

			omsProxy.deregisterRole("manager", "calculin");
			
			
		//	ArrayList<ArrayList<String>> agentRole;
			
			do
			{
				m.waiting(3 * 1000);
				//agentRole = omsProxy.informAgentRole("agente_ruidoso");
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
