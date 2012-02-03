package organizational__message_example.flat;

import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.architecture.FIPARequestResponder;
import es.upv.dsic.gti_ia.architecture.MessageTemplate;
import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.THOMASException;


public class Product extends QueueAgent {


	OMSProxy omsProxy = new OMSProxy(this);
	Monitor m = new Monitor();
	Responder responder = new Responder(this);

	public Product(AgentID aid) throws Exception {
		super(aid);

	}

	public void execute() {



		try
		{
			String result = omsProxy.acquireRole("participant", "virtual");
			logger.info(""+this.getName()+"] result acquire role: "+ result);
			result = omsProxy.acquireRole("operador", "calculin");
			logger.info("["+this.getName()+"] result acquire role: "+ result);




			this.addTask(responder);

			m.waiting();

		}catch(THOMASException e)
		{
			e.printStackTrace();
		}
	}

	public void conclude()
	{
		responder.finish();
		m.advise();
	}

	public void finalize()
	{

		try
		{
			String result = omsProxy.leaveRole("operador", "calculin");
			System.out.println("["+this.getName()+"] Result leave role operador: "+result);

			result = omsProxy.leaveRole("participant", "virtual");
			System.out.println("["+this.getName()+"] Result leave role participant: "+result);

			logger.info("["+this.getName()+" ] end execution!");
		}catch(THOMASException e)
		{
			e.printStackTrace();
		}
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
			return (msg);
		} // end prepareResultNotification

	}// end class SFResponder

}


