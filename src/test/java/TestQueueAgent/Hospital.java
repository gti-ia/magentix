package TestQueueAgent;



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

/**
 * Class Hospital represents the responder agent in the FIPA Request protocol
 * 
 * @author David Fernández - dfernandez@dsic.upv.es
 */

public class Hospital extends QueueAgent {	

    

	
	public double DISTANCIA_MAX;//Field of act of Hospital
	public double SUCCESS_PROB;//Probability of saving
	
	ManejadorResponder responder;

    //Private variable indicating the end of the protocol/conversation
	private boolean end;
	
	public Hospital(AgentID aid)throws Exception {
		super(aid);
		
		end  = false;
		DISTANCIA_MAX = 0;
		SUCCESS_PROB = 0;

	}
	



	protected void execute() {

		MessageTemplate plantilla = new MessageTemplate(
				InteractionProtocol.FIPA_REQUEST);

		responder = new ManejadorResponder(this, plantilla);


		System.out.println("Hospital " + this.getName()
				+ ": Waiting for notices ...");

		this.addTask(responder);
		
		
		while(!end){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
		}
		responder.finish();	//Its necessary to force it because it is a Responder ROL
							//which will never stop himself
	}
	
	//Informs when the protocol is in the final state (finished)
	public boolean finished(){
		return responder.getState()==-1;
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
					end=true;//In this case this is the end of conversation not de ResultNotification
					throw new RefuseException("Accident too far");
				}
			} else{
				end=true;//In this case this is the end of conversation not de ResultNotification
				throw new NotUnderstoodException(
						"Hospital orders a message that I cannot understand.");
			}
		}

		protected ACLMessage prepareResultNotification(ACLMessage request,
				ACLMessage response) throws FailureException {
			
			if (SUCCESS_PROB > 0.2) {
			
				System.out.println("Hospital " + getName()
						+ ": They have returned of attending to the accident.");
				ACLMessage inform = request.createReply();
				inform.setPerformative(ACLMessage.INFORM);
				end = true;//Indicates the end of the conversation
				return inform;
			} else {
				
				System.out.println("Hospital " + getName()
						+ ": They have done everything possible, we feel it.");
				end = true;//Indicates the end of the conversation
				throw new FailureException("They have done everything possible");
			}
			
		}
	}

}
