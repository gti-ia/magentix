package Request_Example;





import java.util.StringTokenizer;

import es.upv.dsic.gti_ia.architecture.FIPARequestResponder;
import es.upv.dsic.gti_ia.architecture.FailureException;
import es.upv.dsic.gti_ia.architecture.MessageTemplate;
import es.upv.dsic.gti_ia.architecture.NotUnderstoodException;
import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.architecture.RefuseException;
import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;



public class Hospital extends QueueAgent {

	
	

    

	
	public double DISTANCIA_MAX;

    
	public Hospital(AgentID aid)throws Exception {


		super(aid);


	}

	



	protected void execute() {
		DISTANCIA_MAX = 5;//(Math.random() * 10);

		MessageTemplate plantilla = new MessageTemplate(
				InteractionProtocol.FIPA_REQUEST);

		ManejadorResponder responder = new ManejadorResponder(this, plantilla);


		System.out.println("Hospital " + this.getName()
				+ ": Waiting for notices ...");

		this.addTask(responder);
		
		while(true){}
		
		
		
	}

	class ManejadorResponder extends FIPARequestResponder {
		public ManejadorResponder(QueueAgent a, MessageTemplate mt) {
			super(a, mt);
		}

		protected ACLMessage prepareResponse(ACLMessage request)
				throws NotUnderstoodException, RefuseException {
			System.out.println("Hospital " + getName()
					+ ": We have received a call of "
					+ request.getSender().name
					+ " Saying that it has seen an accident.");
			StringTokenizer st = new StringTokenizer(request.getContent());
			String contenido = st.nextToken();
			if (contenido.equalsIgnoreCase("accident")) {
				st.nextToken();
				int distancia = Integer.parseInt(st.nextToken());
				if (distancia < DISTANCIA_MAX) {
					System.out.println("Hospital " + getName()
							+ ": We go at once!!!");
					ACLMessage agree = request.createReply();
					agree.setPerformative(ACLMessage.AGREE);
					return agree;
				} else {
				
					System.out
							.println("Hospital "
									+ getName()
									+ ": Accident out of our radius of action. We will not come in time!!!");
					throw new RefuseException("Accident too far");
				}
			} else
				throw new NotUnderstoodException(
						"Hospital orders a message that I cannot understand.");
		}

		protected ACLMessage prepareResultNotification(ACLMessage request,
				ACLMessage response) throws FailureException {
			if (Math.random() > 0.2) {
			
				System.out.println("Hospital " + getName()
						+ ": They have returned of attending to the accident.");
				ACLMessage inform = request.createReply();
				inform.setPerformative(ACLMessage.INFORM);
				return inform;
			} else {
				
				System.out.println("Hospital " + getName()
						+ ": They have done everything possible, we feel it.");
				throw new FailureException("They have done everything possible");
			}
		}
	}

}
