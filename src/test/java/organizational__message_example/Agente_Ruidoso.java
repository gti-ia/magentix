package organizational__message_example;

import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.THOMASException;


public class Agente_Ruidoso extends QueueAgent {


	OMSProxy omsProxy = new OMSProxy(this);
	int result=0;
	int mensajes_esperados=2;
	Monitor m = new Monitor();

	public Agente_Ruidoso(AgentID aid) throws Exception {
		super(aid);

	}

	public void execute() {



		logger.info("Executing, I'm " + getName());
		/**
		 * This agent has no definite work. Wait infinitely the arrival of new
		 * messages.
		 */


		omsProxy.acquireRole("member", "virtual");


		this.inicializar_flat();


		this.enviar_peticion(1,7);

		m.waiting(); //Espero a que me lleguen las respuestas y las mostraré por pantalla

		this.enviar_informacion(result+"");

		System.out.println("[ "+this.getName()+" ] Bye Bye.");

	}

	private void sumar_y_avisar(ACLMessage msg)
	{
		result+=Integer.parseInt(msg.getContent());
		mensajes_esperados--;


		if (mensajes_esperados == 0)
		{

			m.advise();
		}
	}

	public void onMessage(ACLMessage msg)
	{



		if (msg.getSender().name.equals("agente_suma") || msg.getSender().name.equals("agente_producto"))
		{
			if (!msg.getContent().contains("En breve"))
			{
				this.sumar_y_avisar(msg);
			}
		}
		if (msg.getSender().name.equals("OMS") || msg.getSender().name.equals("SF")) //TODO Sols els del OMS o el SF, que son els que utilize en el PROXY, MILLORAR AÇO
			super.onMessage(msg);

	}

	private void inicializar_flat()
	{

		omsProxy.registerUnit("externa", "flat", "unidad_externa_tipo_flat", "virtual");
		omsProxy.registerRole("manager", "externa", "internal", "member", "public","member");
		omsProxy.registerRole("creador", "externa", "internal", "member", "public","member"); //TODO deberia ser creator, pero ahora la implementacion del OMS no lo permite.



		omsProxy.acquireRole("manager","externa");
		omsProxy.acquireRole("creador","externa");
	}

	private void enviar_peticion(int n1, int n2)
	{
		try {
			ACLMessage msg = omsProxy.buildOrganizationalMessage("calculin");
			msg.setPerformative(InteractionProtocol.FIPA_REQUEST);
			msg.setProtocol(InteractionProtocol.FIPA_REQUEST);
			msg.setLanguage("ACL");
			msg.setContent(n1+" "+ n2);

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
		} catch (THOMASException e) {
			System.out.println(e.getContent());

		}
	}


}
