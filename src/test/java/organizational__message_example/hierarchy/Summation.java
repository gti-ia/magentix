package organizational__message_example.hierarchy;

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
	int expected = 2;
	Monitor m = new Monitor();

	public Summation(AgentID aid) throws Exception {
		super(aid);
	}

	public void execute() {

		try {
			omsProxy.acquireRole("participant", "virtual");

			this.send_request(6,3);
			m.waiting(); // Waiting the response with a timeout
			this.send_result("" + result); // Inform the result.

			expected = 1; //Reset the result and messages expected
			result=0;
			this.send_request(5,3);
			m.waiting(); // Waiting the response with a timeout
			this.send_result("" + result); // Inform the result.

			
			this.send_shutdown();

			m.waiting(1 * 1000);

			omsProxy.leaveRole("manager", "calculin");

			omsProxy.leaveRole("participant", "virtual");

			System.out.println("[ "+this.getName()+" ] end execution!");
		} catch (THOMASException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}




	private void add_and_advise(ACLMessage msg) {
		
		result += Integer.parseInt(msg.getContent());
		expected--;
		if (expected == 0) {
			m.advise(); //When all message arrives, it notifies the main thread		
		}
	}

	public void onMessage(ACLMessage msg) {




		if (msg.getSender().name.equals("agente_suma") || msg.getSender().name.contains("agente_producto")) 
		{
			//When a message arrives, it select the message with a results
			if (!msg.getContent().equals("OK")) 
			{
				this.add_and_advise(msg);
			}
		}
		//The messages with OMS and SF senders will be returned to super onMessage
		if (msg.getSender().name.equals("OMS") || msg.getSender().name.equals("SF")) // Los del agente OMS y SF los volvemos a encolar, ya que sonnecesarios para el thomas proxy
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

	
	private void send_shutdown()
	{
		try {
			ACLMessage msg = omsProxy.buildOrganizationalMessage("calculin");
			msg.setPerformative(InteractionProtocol.FIPA_REQUEST);
			msg.setProtocol(InteractionProtocol.FIPA_REQUEST);
			msg.setLanguage("ACL");
			msg.setContent("shut down");

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
