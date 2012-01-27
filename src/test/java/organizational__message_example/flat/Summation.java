package organizational__message_example.flat;

import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.THOMASException;

public class Summation extends QueueAgent {

	OMSProxy omsProxy = new OMSProxy(this);
	int result = 0;
	Monitor m = new Monitor();
	long last = 0;
	int recibidos = 0;
	public Summation(AgentID aid) throws Exception {
		super(aid);
	}

	public void execute() {

		

		String resultA = omsProxy.acquireRole("participant", "virtual");
		System.out.println("["+this.getName()+"] result acquire role: "+ resultA);
		resultA = omsProxy.acquireRole("manager", "calculin");
		System.out.println("["+this.getName()+"] result acquire role: "+ resultA);
	
		
		
		this.send_request(6,3);
		m.waiting(); // Waiting the response with a timeout
		this.send_result("" + result); // Inform the result.
		
		result=0;
		this.send_request(5,3);
		m.waiting(10 * 1000); // Waiting the response with a timeout
		this.send_result("" + result); // Inform the result.

	}

	private void add_and_advise(ACLMessage msg) {
		result += Integer.parseInt(msg.getContent());
		last = System.currentTimeMillis();
		recibidos++;
		if (recibidos==2)
			m.advise();
	}

	public void onMessage(ACLMessage msg) {

	
		if (msg.getSender().name.equals("agente_suma") || msg.getSender().name.contains("agente_producto")) 
		{
			//When a message arrives, it select the message with a results
			if (!msg.getContent().contains("OK")) 
			{
				this.add_and_advise(msg);	
			}
		}
		//The messages with OMS and SF senders will be returned to super onMessage
		if (msg.getSender().name.equals("OMS") || msg.getSender().name.equals("SF")) 
			super.onMessage(msg);

	}

	private void send_request(int n1, int n2) {
		try {
			ACLMessage msg = omsProxy.buildOrganizationalMessage("calculin");
			msg.setPerformative(InteractionProtocol.FIPA_REQUEST);
			msg.setProtocol(InteractionProtocol.FIPA_REQUEST);
			msg.setLanguage("ACL");
			msg.setContent(n1+" "+n2);

			send(msg);
		} catch (THOMASException e) {
			System.out.println("[ " + this.getName() + " ] " + e.getContent());

		}
	}

	private void send_result(String content) {
		try {

			ACLMessage msg = omsProxy.buildOrganizationalMessage("calculin");
			msg.setPerformative(ACLMessage.INFORM);
			msg.setLanguage("ACL");
			msg.setContent(content);

			send(msg);
		} catch (THOMASException e) {
			System.out.println("[ " + this.getName() + " ] " + e.getContent());

		}
	}

}
