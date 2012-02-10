package organizational__message_example.CAgents.hierarchy;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_REQUEST_Participant;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.MessageFilter;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.THOMASException;


public class Display extends CAgent {

	OMSProxy omsProxy = new OMSProxy(this);
	Monitor m = new Monitor();
	ACLMessage receivedMsg = new ACLMessage();
	Queue<ACLMessage> messageList = new LinkedList<ACLMessage>();
	boolean finished = false;

	public Display(AgentID aid) throws Exception {
		super(aid);

	}

	protected void execution(CProcessor firstProcessor, ACLMessage welcomeMessage) {


		try
		{
			omsProxy.acquireRole("participant", "virtual");


			class ShutdownAgentFIPA_REQUEST extends FIPA_REQUEST_Participant {

				@Override
				protected String doAction(CProcessor myProcessor) {
					CAgent cAgent = myProcessor.getMyAgent();
					return "INFORM";
				}

				@Override
				protected void doInform(CProcessor myProcessor, ACLMessage response) {
					CAgent cAgent = myProcessor.getMyAgent();
					response.setSender(cAgent.getAid());
					response.setReceiver(myProcessor.getLastReceivedMessage().getSender());
					response.setHeader("shutdown", "ShutdownAgent");
					response.setContent("All my published services have been removed and all played roles have been left.");

					myProcessor.ShutdownAgent();
				}

				@Override
				protected String doReceiveRequest(CProcessor myProcessor, ACLMessage request) {
					return "AGREE";
				}

				@Override
				protected void doAgree(CProcessor myProcessor, ACLMessage messageToSend) {
					messageToSend.setPerformative(ACLMessage.AGREE);
					messageToSend.setHeader("shutdown", "ShutdownAgent");
					messageToSend.setSender(myProcessor.getMyAgent().getAid());
					messageToSend.setReceiver(myProcessor.getLastReceivedMessage().getSender());
					messageToSend.setHeader("shutdown", "ShutdownAgent");
					messageToSend.setContent("Received 'Shutdown' order.");
				}
			}

			
			MessageFilter shutdownFilter = new MessageFilter("performative = " + ACLMessage.getPerformative(ACLMessage.REQUEST) + "  AND shutdown = ShutdownAgent");

			CFactory shutdownTalk = new ShutdownAgentFIPA_REQUEST().newFactory("ShutdownTalk", shutdownFilter, 1, firstProcessor.getMyAgent());
			// The template processor is ready. We activate the factory
			// as participant. Every message that arrives to the agent
			// with the performative set to REQUEST will make the factory
			// ShutdownTalk to create a processor in order to manage the
			// conversation.
			this.addFactoryAsParticipant(shutdownTalk);
			
			
			
			// The agent creates the CFactory that manages every message which its
			// performative is set to PROPOSE and filter is set to shutdown.

			// The agent creates the CFactory that manages every message which its
			// performative is set to REQUEST and filter is set to shutdown.

			// We create a factory in order to manage ShutdownAgent orders
			class agentFIPA_REQUEST extends FIPA_REQUEST_Participant {

				int n=0;
				@Override
				protected String doAction(CProcessor myProcessor) {
					CAgent cAgent = myProcessor.getMyAgent();
					return "INFORM";
				}

				@Override
				protected void doInform(CProcessor myProcessor, ACLMessage response) {
					CAgent cAgent = myProcessor.getMyAgent();
					response.setSender(cAgent.getAid());
					response.setReceiver(myProcessor.getLastReceivedMessage().getSender());
	
				}

				@Override
				protected String doReceiveRequest(CProcessor myProcessor, ACLMessage request) {
					return "AGREE";
				}

				@Override
				protected void doAgree(CProcessor myProcessor, ACLMessage messageToSend) {
					messageToSend.setPerformative(ACLMessage.AGREE);
					messageToSend.setSender(myProcessor.getMyAgent().getAid());
					messageToSend.setReceiver(myProcessor.getLastReceivedMessage().getSender());
					messageToSend.setContent("OK");
				}
			}
			
			//Waiting for messages.

			MessageFilter fipaRequestFilter = new MessageFilter("receiver = calculin OR shutdown = " );

			CFactory shutdownTalk = new agentFIPA_REQUEST().newFactory("fipaRequestTalk", fipaRequestFilter, 1, firstProcessor.getMyAgent());
			// The template processor is ready. We activate the factory
			// as participant. Every message that arrives to the agent
			// with the performative set to REQUEST will make the factory
			// ShutdownTalk to create a processor in order to manage the
			// conversation.
			this.addFactoryAsParticipant(shutdownTalk);
			
			do{
				m.waiting();  //Waiting messages. The method onMessage is in charge of warning when a new message arrive

				do{
					this.displayMessage(messageList.poll());
				}while(messageList.size() != 0);
			}while(!finished);

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

		}catch(THOMASException e)
		{
			e.printStackTrace();
		}


	}



	/**
	 * Display agent messages with position qual to supervisor 
	 * @param _msg
	 */
	public void displayMessage(ACLMessage _msg)
	{

		try
		{
			
			ACLMessage msg = _msg;

			/**
			For every role that exists in the unit calculin we extract the members who have this role
			 **/


			if (msg.getContent().equals("shut down"))
				finished = true;
			ArrayList<ArrayList<String>> informMembers = omsProxy.informMembers("calculin","","supervisor");
		
			
			for(ArrayList<String> informMember : informMembers)
			{
				//If the agent is equal to the sender, then the position is extracted
				if (informMember.get(0).equals(msg.getSender().name))
					System.out.println("[ "+this.getName()+" ]  "+ msg.getSender().name+" says " + msg.getContent());
			}

			



		}catch(THOMASException e)
		{
			e.printStackTrace();
		}
	}



	public void onMessage(ACLMessage msg)
	{

		if (msg.getReceiver().name.equals("calculin")) //Messages that come from the organization
		{

			messageList.add(msg);	
			m.advise(); //When a new message arrives, it advise the main thread

		}
		if (msg.getSender().name.equals("OMS") || msg.getSender().name.equals("SF"))
			super.onMessage(msg);

	}

	@Override
	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {
		// TODO Auto-generated method stub
		
	}



}
