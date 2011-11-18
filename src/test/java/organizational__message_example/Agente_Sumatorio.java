package organizational__message_example;

import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.THOMASException;



public class Agente_Sumatorio extends QueueAgent {

	OMSProxy omsProxy = new OMSProxy(this);
	int result=0;
	int mensajes_esperados=2;
	Monitor m = new Monitor();

	public Agente_Sumatorio(AgentID aid) throws Exception {
		super(aid);
	}

	public void execute() {



		logger.info("Executing, I'm " + getName());
		/**
		 * This agent has no definite work. Wait infinitely the arrival of new
		 * messages.
		 */


		omsProxy.acquireRole("member", "virtual");
		omsProxy.acquireRole("manager", "calculin");

		
		this.enviar_peticion("6 3");
	
		do
		{
			m.waiting(); //Espero a que me lleguen las respuestas
			this.enviar_informacion(""+result); //Informo del resultado obtenido
		}while(true);
		
		
	}
	
	private void sumar_y_avisar(ACLMessage msg)
	{
		result+=Integer.parseInt(msg.getContent());
		mensajes_esperados--;
		if (mensajes_esperados == 0)
		{
			m.advise(); //Te que acabar el onMessage, sinos no em deixa rebre mes missatges, per tant hem de avisar a un programa desde fora del metode
	
		}
	}
	public void onMessage(ACLMessage msg)
	{
		
		
		
		if (msg.getSender().name.equals("agente_suma") || msg.getSender().name.equals("agente_producto")) // Nos interesan tratar los mensajes de los agentes suma y producto
		{
			if (!msg.getContent().contains("En breve")) //Descartamos los mensajes informativos.
			{
				
				this.sumar_y_avisar(msg);
			}
		}
		if (msg.getSender().name.equals("OMS") || msg.getSender().name.equals("SF")) //Los del agente OMS y SF los volvemos a encolar, ya que son necesarios para el thomasproxy
			super.onMessage(msg);

	}

	private void enviar_peticion(String content)
	{
		try {
			ACLMessage msg = omsProxy.buildOrganizationalMessage("calculin");
			msg.setPerformative(InteractionProtocol.FIPA_REQUEST);
			msg.setProtocol(InteractionProtocol.FIPA_REQUEST);
			msg.setLanguage("ACL");
			msg.setContent(content);

			
			send(msg);
		} catch (THOMASException e) {
			System.out.println(e.getContent());

		}
	}
	
	private void enviar_informacion(String content)
	{
		try {
			ACLMessage msg = omsProxy.buildOrganizationalMessage("calculin");
			msg.setPerformative(ACLMessage.INFORM);
			msg.setLanguage("ACL");
			msg.setContent(content);

			send(msg);
		} catch (Exception e) {
			System.out.println(e.getMessage());

		}
	}

}
