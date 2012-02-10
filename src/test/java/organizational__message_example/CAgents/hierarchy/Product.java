package organizational__message_example.CAgents.hierarchy;

import java.util.ArrayList;

import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.architecture.FIPARequestResponder;
import es.upv.dsic.gti_ia.architecture.MessageTemplate;
import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.THOMASException;
import es.upv.dsic.gti_ia.architecture.Monitor;


public class Product extends QueueAgent {

	OMSProxy omsProxy = new OMSProxy(this);
	Responder responder = new Responder(this);
	Monitor m = new Monitor();
	boolean finished = false;
	
	public Product(AgentID aid) throws Exception {
		super(aid);

	}

	public void execute() {


		try
		{

			omsProxy.acquireRole("participant", "virtual");
			
			ArrayList<ArrayList<String>> roles;
			boolean exists = false;

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
			this.addTask(responder);
			do{
				m.waiting(5*1000);
				
			}while(!finished);
			
			omsProxy.leaveRole("participant", "virtual");
			
		}catch(THOMASException e)
		{
			e.printStackTrace();
		}

	}



	public void conclude()
	{
		finished = true;
		
		
	}



	/**
	 * Manages the messages for the  agent provider services
	 */
	public class Responder extends FIPARequestResponder {



		public Responder(QueueAgent agent) {
			super(agent, new MessageTemplate(InteractionProtocol.FIPA_REQUEST));

		}// SFResponder

		protected ACLMessage prepareResponse(ACLMessage msg) {

			ACLMessage response = msg.createReply();


			response.setPerformative(ACLMessage.AGREE);
			response.setContent("OK");
			
			return (response);

		} // end prepareResponse

		protected ACLMessage prepareResultNotification(ACLMessage inmsg, ACLMessage outmsg) {




			ACLMessage msg = inmsg.createReply();
			
			
			if (inmsg.getContent().equals("shut down"))
			{
				
				
				((Product)this.getQueueAgent()).conclude();
				
				msg.setPerformative(ACLMessage.INFORM);


				msg.setContent("OK");

			}
			else
			{

				int p1,p2, result;
				try{
					p1 = Integer.parseInt(inmsg.getContent().split(" ")[0]);
					p2 = Integer.parseInt(inmsg.getContent().split(" ")[1]);

					result = p1 * p2;

					msg.setPerformative(ACLMessage.INFORM);


					msg.setContent(""+result);



				}catch(Exception e)
				{
					msg.setPerformative(ACLMessage.FAILURE);
					msg.setContent(e.getMessage());
				}
			}

			return (msg);
		} // end prepareResultNotification

	}// end class SFResponder

}


