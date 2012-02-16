package organizational__message_example.CAgents.team;


import java.util.ArrayList;

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
import es.upv.dsic.gti_ia.organization.THOMASException;


public class Display extends CAgent {

	OMSProxy omsProxy = new OMSProxy(this);





	public Display(AgentID aid) throws Exception {
		super(aid);

	}

	protected void execution(CProcessor firstProcessor, ACLMessage welcomeMessage) {


		try
		{

			omsProxy.acquireRole("participant", "virtual");

			//------------------------------------------------------------------------
			//-----------------------CFactory definition------------------------------
			//------------------------------------------------------------------------
			MessageFilter filter = new MessageFilter("performative = REQUEST OR performative = INFORM");
			CFactory additionTalk = new CFactory("PRODUCT_TALK", null, 1,this);


			//----------------------------BEGIN STATE----------------------------------
			BeginState BEGIN = (BeginState) additionTalk.cProcessorTemplate().getState("BEGIN");
			BEGIN.setMethod(new BEGIN_Method());

			//----------------------------WAIT STATE----------------------------------
			WaitState WAIT = new WaitState("WAIT", 0);
			additionTalk.cProcessorTemplate().registerState(WAIT);
			additionTalk.cProcessorTemplate().addTransition(BEGIN, WAIT);
			
			//----------------------------SHUTDOWN STATE----------------------------------
			ReceiveState RECEIVE_SHUTDOWN = new ReceiveState("RECEIVE_SHUTDOWN");
			RECEIVE_SHUTDOWN.setAcceptFilter(new MessageFilter("shutdown = true")); // null -> accept any message
			RECEIVE_SHUTDOWN.setMethod(new RECEIVE_Shutdown_Method());
			additionTalk.cProcessorTemplate().registerState(RECEIVE_SHUTDOWN);
			additionTalk.cProcessorTemplate().addTransition(WAIT, RECEIVE_SHUTDOWN);

			//----------------------------RECEIVE STATE----------------------------------
			ReceiveState RECEIVE = new ReceiveState("RECEIVE");
			RECEIVE.setAcceptFilter(filter); // null -> accept any message
			RECEIVE.setMethod(new RECEIVE_Method());
			additionTalk.cProcessorTemplate().registerState(RECEIVE);
			additionTalk.cProcessorTemplate().addTransition(WAIT, RECEIVE);

			//----------------------------FINAL STATE----------------------------------
			FinalState FINAL = new FinalState("FINAL");
			FINAL.setMethod(new FINAL_Method());
			additionTalk.cProcessorTemplate().registerState(FINAL);
			additionTalk.cProcessorTemplate().addTransition(RECEIVE, FINAL);
			
			//----------------------------FINAL SHUTDOWN STATE----------------------------------
			FinalState FINAL_SHUTDOWN = new FinalState("FINAL_SHUTDOWN");
			FINAL_SHUTDOWN.setMethod(new FINAL_SHUTDOWN_Method());
			additionTalk.cProcessorTemplate().registerState(FINAL_SHUTDOWN);
			additionTalk.cProcessorTemplate().addTransition(RECEIVE_SHUTDOWN, FINAL_SHUTDOWN);

			this.addFactoryAsParticipant(additionTalk);




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
			return "WAIT";
		};

	}


	class RECEIVE_Shutdown_Method implements ReceiveStateMethod
	{


		public String run(CProcessor myProcessor, ACLMessage receivedMessage) {


			String state = "FINAL_SHUTDOWN";

			return state;
		}

	}

	class RECEIVE_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {

			String state = "FINAL";


			ArrayList<ArrayList<String>> informMembers;
			try {
				informMembers = omsProxy.informMembers("calculin","manager","");



				for(ArrayList<String> informMember : informMembers)
				{
					//If the agent is equal to the sender, then the position is extracted
					if (informMember.get(0).equals(messageReceived.getSender().name))
						System.out.println("[ "+myProcessor.getMyAgent().getName()+" ]  "+ messageReceived.getSender().name+" says " + messageReceived.getContent());
				}

			} catch (THOMASException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


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
			OMSProxy omsProxy = new OMSProxy(myProcessor);

			try {


				ArrayList<String> result = omsProxy.informUnit("externa");

				System.out.println("---------------------");
				System.out.println("unit external");
				System.out.println("type: "+ result.get(0));
				System.out.println("parent unit: "+ result.get(1));
				System.out.println("---------------------");

				ArrayList<ArrayList<String>> informUnitRoles = omsProxy.informUnitRoles("calculin");

				for(ArrayList<String> unitRole : informUnitRoles)
				{
					System.out.println("---------------------");

					System.out.println("role name: "+unitRole.get(0));
					System.out.println("position: "+ unitRole.get(1));
					System.out.println("visibility: "+ unitRole.get(2));
					System.out.println("accesibility: "+ unitRole.get(3));

					System.out.println("---------------------");
				}

				omsProxy.leaveRole("manager", "calculin");

				omsProxy.leaveRole("participant", "virtual");

			} catch (THOMASException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			myProcessor.ShutdownAgent();

		}

	}


}
