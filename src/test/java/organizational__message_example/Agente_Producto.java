package organizational__message_example;

import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.architecture.FIPARequestResponder;
import es.upv.dsic.gti_ia.architecture.MessageTemplate;
import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.OMSProxy;


public class Agente_Producto extends QueueAgent {

	public Agente_Producto(AgentID aid) throws Exception {
		super(aid);

	}

	public void execute() {



		logger.info("Executing, I'm " + getName());
		/**
		 * This agent has no definite work. Wait infinitely the arrival of new
		 * messages.
		 */
		OMSProxy omsProxy = new OMSProxy(this);

		omsProxy.acquireRole("member", "virtual");
		omsProxy.acquireRole("operador", "calculin");
		
		Responder responder = new Responder(this);

		this.addTask(responder);

		es.upv.dsic.gti_ia.architecture.Monitor mon = new es.upv.dsic.gti_ia.architecture.Monitor();
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
			response.setContent("En breve obtendras tu respuesta");	





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
			
			//Una vez terminada la peticion sale del grupo
			
//			OMSProxy omsProxy = new OMSProxy(this.getQueueAgent());
//			n++;
//			omsProxy.leaveRole("operador"+n, "calculin");
//			System.out.println("VALOR "+ n);
			
		//	System.out.println("Envio soy "+ this.getQueueAgent().getName()+ " contenido: "+ msg.getContent());
			return (msg);
		} // end prepareResultNotification

	}// end class SFResponder

}


