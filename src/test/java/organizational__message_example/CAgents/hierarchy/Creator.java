package organizational__message_example.CAgents.hierarchy;

import java.util.ArrayList;

import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
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


			this.send_request(4, 2);
			
			omsProxy.acquireRole("manager", "calculin");
			
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
			
			result = omsProxy.deregisterRole("operador", "calculin");
			logger.info("["+this.getName()+"] Result leave role operador: "+result);

			result = omsProxy.deregisterRole("manager", "calculin");
			logger.info("["+this.getName()+"] Result leave role manager: "+result);
			
		//	ArrayList<ArrayList<String>> agentRole;
			
			do
			{
				m.waiting(3 * 1000);
				//agentRole = omsProxy.informAgentRole("agente_ruidoso");
				members = omsProxy.informMembers("externa", "manager", "");
			}while(members.contains("agente_ruidoso"));

			
			result = omsProxy.deregisterUnit("externa");
			logger.info("["+this.getName()+"] Result deregister unit calculin: "+result);
			
			result = omsProxy.deregisterUnit("calculin");
			logger.info("["+this.getName()+"] Result deregister unit calculin: "+result);

			result = omsProxy.leaveRole("participant", "virtual");
			logger.info("["+this.getName()+"] Result leave role participant: "+result);
			logger.info("["+this.getName()+" ] end execution!");
			
		}catch(THOMASException e)
		{
			e.printStackTrace();
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
			
			
			omsProxy.allocateRole("manager", "calculin", "agente_sumatorio");
			
			
			omsProxy.allocateRole("manager", "calculin", "agente_sumaPotencias");
			
		}catch(THOMASException e)
		{
			e.printStackTrace();
		}

	}
	

	public void send_request(int n1, int n2)
	{
		try {
			//Build the organizational message
			ACLMessage msg = omsProxy.buildOrganizationalMessage("calculin");

			//Refill the organization message with the protocol and the message content
			msg.setPerformative(InteractionProtocol.FIPA_REQUEST);
			msg.setProtocol(InteractionProtocol.FIPA_REQUEST);
			msg.setLanguage("ACL");
			msg.setContent(n1+" y "+ n2);

			//Send the message
			send(msg);
		} catch (THOMASException e) {//Caught the thomas exception

			System.out.println("[ "+this.getName()+" ] "+ e.getContent());

		}
	}

	@Override
	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {
		// TODO Auto-generated method stub
		
	}

}
