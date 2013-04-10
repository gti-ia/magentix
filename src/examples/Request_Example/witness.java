package Request_Example;

import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.architecture.FIPARequestInitiator;
import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;

public class witness extends QueueAgent {


	int con = 0;
	
	private Monitor monitor = new Monitor();
	
	private String status = null;
	
	
	public witness(AgentID aid) throws Exception {

		super(aid);

	}


	protected void execute() {
		DOMConfigurator.configure("configuration/loggin.xml");
		
		System.out
				.println("I have seen an accident! Requesting help to several hospitals...");

		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		// for (int i = 0; i < args.length; ++i)
		msg.setReceiver(new AgentID("HospitalAgent"));
		msg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		msg.setContent("accident to " + "2" + " km");
		msg.setSender(this.getAid());

		this.addTask(new ManejadorInitiator(this, msg));
		
		monitor.waiting(100);
		
		

	}

	class ManejadorInitiator extends FIPARequestInitiator {


		public ManejadorInitiator(QueueAgent a, ACLMessage msg) {
			super(a, msg);

		}

		protected void handleAgree(ACLMessage agree) {

			System.out.println(" !!!!Hospital "
					+ agree.getSender().getLocalName()
					+ " It informs that they have gone out to attend to the accident.");
		}

		protected void handleRefuse(ACLMessage refuse) {

			System.out
					.println(" !!!!Hospital "
							+ refuse.getSender().getLocalName()
							+ " It answers that the accident this one out of his radio of action. We will not come in time!!!");
		}

		protected void handleNotUnderstood(ACLMessage notUnderstood) {

			System.out.println(" !!!!Hospital "
					+ notUnderstood.getSender().getLocalName()
					+ " It cannot understand the message.");
		}

		protected void handleInform(ACLMessage inform) {

			System.out.println(" !!!!!!Hospital "
					+ inform.getSender().getLocalName()
					+ " It informs that they have attended to the accident.");
			
			status = "OK";
		}

		protected void handleFailure(ACLMessage fallo) {
			if (fallo.getSender().name.equals(myAgent.getName())) {

				System.out.println(" Someone of the hospitals does not exist");
			} else {

				System.out.println(" I fail in the hospital "
						+ fallo.getSender().getLocalName()
						+ ": "
						+ fallo.getContent().substring(0,
								fallo.getContent().length()));
			}
		}
	}
	
	public String getStatus()
	{
		return status;
	}
}
