package organizational__message_example.hierarchy;



import es.upv.dsic.gti_ia.architecture.FIPARequestResponder;
import es.upv.dsic.gti_ia.architecture.MessageTemplate;
import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.OMSProxy;


public class Addition extends QueueAgent {

	public Addition(AgentID aid) throws Exception {
		super(aid);

	}

	public void execute() {


		OMSProxy omsProxy = new OMSProxy(this);

		//Acquire the member rol in virtual unit and operador inside the calculin unit
		omsProxy.acquireRole("member", "virtual");
		omsProxy.acquireRole("operador", "calculin");

		Responder responder = new Responder(this);
		
		//Add a new protocol FIPA Request
		this.addTask(responder);
		
		es.upv.dsic.gti_ia.architecture.Monitor mon = new es.upv.dsic.gti_ia.architecture.Monitor();
		
		//Waiting for messages.
		mon.waiting();

	}




	/**
	 * Manages the messages for the  agent provider services
	 */
	public class Responder extends FIPARequestResponder {

		
		int n=0;
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

				result = p1 + p2;

				msg.setPerformative(ACLMessage.INFORM);


				msg.setContent(""+result);

				OMSProxy omsProxy = new OMSProxy(this.getQueueAgent());

				
				switch(n)
				{
				//Leave operador role
					case 0: omsProxy.leaveRole("operador", "calculin"); break;
				}

				n++;


			}catch(Exception e)
			{
				msg.setPerformative(ACLMessage.FAILURE);
				msg.setContent(e.getMessage());
			}
			return (msg);
		} // end prepareResultNotification

	}// end class SFResponder

}


