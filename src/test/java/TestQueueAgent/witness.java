package TestQueueAgent;

import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.architecture.FIPARequestInitiator;
import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;

/**
 * Class witness represents the initiator agent in the FIPA Request protocol
 * 
 * @author David Fernández - dfernandez@dsic.upv.es
 */

public class witness extends QueueAgent {

	public String petitionResult;
	public String informResult;
	public int witnessDistance;
	public String content;
	
	
	private ManejadorInitiator ini;

	int con = 0;
	
	private Monitor monitor = new Monitor();
	
	
	
	public witness(AgentID aid) throws Exception {

		super(aid);
		
		petitionResult = "";
		informResult = "";
		witnessDistance = 10;
		content = "accident to " + witnessDistance + " km";

	}


	protected void execute() {
		DOMConfigurator.configure("configuration/loggin.xml");
		
		System.out
				.println("I have seen an accident! Requesting help to several hospitals...");

		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		// for (int i = 0; i < args.length; ++i)
		msg.setReceiver(new AgentID("HospitalAgent"));
		msg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		msg.setContent(content);
		msg.setSender(this.getAid());

		
		ini = new ManejadorInitiator(this, msg);
		this.addTask(ini);
		
		while(!ini.finished()){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
	
	class ManejadorInitiator extends FIPARequestInitiator {


		public ManejadorInitiator(QueueAgent a, ACLMessage msg) {
			super(a, msg);

		}

		protected void handleAgree(ACLMessage agree) {
			
			petitionResult = " !!!!Hospital "
					+ agree.getSender().getLocalName()
					+ " It informs that they have gone out to attend to the accident.";
			
			System.out.println(petitionResult);
		}

		protected void handleRefuse(ACLMessage refuse) {

			petitionResult = " !!!!Hospital "
					+ refuse.getSender().getLocalName()
					+ " It answers that the accident this one out of his radio of action. They will not come in time!!!";
			
			System.out.println(petitionResult);
		}

		protected void handleNotUnderstood(ACLMessage notUnderstood) {

			petitionResult= " !!!!Hospital "
					+ notUnderstood.getSender().getLocalName()
					+ " It cannot understand the message.";
			
			System.out.println(petitionResult);
		}

		protected void handleInform(ACLMessage inform) {
			
			informResult = " !!!!!!Hospital "
					+ inform.getSender().getLocalName()
					+ " It informs that they have attended to the accident.";
			
			System.out.println(informResult);
		}

		protected void handleFailure(ACLMessage fallo) {
			if (fallo.getSender().name.equals(myAgent.getName())) {
				System.out.println("FALLOOOOOOO");
				petitionResult = " Someone of the hospitals does not exist";			
			} else {
				
				petitionResult = " I fail in the hospital "
						+ fallo.getSender().getLocalName()
						+ ": "
						+ fallo.getContent().substring(0,
								fallo.getContent().length());
			}
			
			System.out.println(petitionResult);
		}
	}
}
