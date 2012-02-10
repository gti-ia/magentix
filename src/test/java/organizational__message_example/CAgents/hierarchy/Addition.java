package organizational__message_example.CAgents.hierarchy;



import java.util.ArrayList;

import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_REQUEST_Participant;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.MessageFilter;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.THOMASException;

public class Addition extends CAgent {

	OMSProxy omsProxy = new OMSProxy(this);

	
	public Addition(AgentID aid) throws Exception {
		super(aid);

	}

	
	protected void execution(CProcessor firstProcessor, ACLMessage welcomeMessage) {


		
		try
		{
		
		ArrayList<ArrayList<String>> roles;

		boolean exists = false;

		String result = omsProxy.acquireRole("participant", "virtual");
		logger.info("["+this.getName()+"] Result acquire role participant: "+result);
		
		do
		{
			roles = omsProxy.informUnitRoles("calculin");

			for(ArrayList<String> role : roles)
			{
				if (role.get(0).equals("operador"))
					exists = true;
			}
		}while(!exists);
		
		omsProxy.acquireRole("operador", "calculin");
		
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
				
				

				int p1,p2, result;
				try{
					p1 = Integer.parseInt(myProcessor.getLastReceivedMessage().getContent().split(" ")[0]);
					p2 = Integer.parseInt(myProcessor.getLastReceivedMessage().getContent().split(" ")[1]);

					result = p1 + p2;

					response.setPerformative(ACLMessage.INFORM);

					response.setContent(""+result);
					

					OMSProxy omsProxy = new OMSProxy(cAgent);

					
					switch(n)
					{
					//Leave operador role
						case 1: 
							
							omsProxy.leaveRole("operador", "calculin"); 
							omsProxy.leaveRole("participant", "virtual");
							
						
							myProcessor.ShutdownAgent();
						break;
					}

					n++;
				
				

				}catch(THOMASException e)
				{
					e.printStackTrace();
				}
				
				
				
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

		MessageFilter fipaRequestFilter = new MessageFilter("performative = " + ACLMessage.getPerformative(ACLMessage.REQUEST));

		CFactory shutdownTalk = new agentFIPA_REQUEST().newFactory("fipaRequestTalk", fipaRequestFilter, 1, firstProcessor.getMyAgent());
		// The template processor is ready. We activate the factory
		// as participant. Every message that arrives to the agent
		// with the performative set to REQUEST will make the factory
		// ShutdownTalk to create a processor in order to manage the
		// conversation.
		this.addFactoryAsParticipant(shutdownTalk);
		
		
		
		
		
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

}


