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

		

		omsProxy.acquireRole("member", "virtual");
		omsProxy.acquireRole("manager", "calculin");
	
	
		this.send_request(6,3);
		m.waiting(10 * 1000); // Espero a que me lleguen las respuestas con un timeout de 10 segundos
		this.send_result("" + result); // Informo del resultado obtenido
		
		expected = 2; //Reinicio el contador
		result=0;
		this.send_request(5,3);
		m.waiting(10 * 1000); // Espero a que me lleguen las respuestas con un timeout de 10 segundos
		this.send_result("" + result); // Informo del resultado obtenido

	}

	private void add_and_advise(ACLMessage msg) {
		result += Integer.parseInt(msg.getContent());
		expected--;
		if (expected == 0) {
			m.advise(); //Aviso al hilo principal que ya tiene todas las respuestas		
		}
	}

	public void onMessage(ACLMessage msg) {

	
		if (msg.getSender().name.equals("agente_suma") || msg.getSender().name.contains("agente_producto")) 
		{
			if (!msg.getContent().contains("OK")) // Descartamos los mensajes informativos.
			{
				this.add_and_advise(msg);
			}
		}
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
