package organizational__message_example;

import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.THOMASException;


public class Agente_Creador extends QueueAgent {
	
	OMSProxy omsProxy = new OMSProxy(this);
	
	public Agente_Creador(AgentID aid) throws Exception {
		super(aid);
	}

	public void execute() {



		logger.info("Executing, I'm " + getName());
		/**
		 * This agent has no definite work. Wait infinitely the arrival of new
		 * messages.
		 */
		

		omsProxy.acquireRole("member", "virtual");//En un futur estos dos seran el mateix, es quedara com a creator.
		
		
		this.inicializar_jerarquia();
		
		omsProxy.acquireRole("creador", "calculin");
		
		this.enviar_peticion(4, 2);
		
		
		
	}
	
	private void inicializar_jerarquia()
	{
		omsProxy.registerUnit("calculin", "flat", "unidad_calculin", "virtual");
		
		omsProxy.registerRole("creador", "calculin", "internal", "member", "public","member"); //TODO deberia ser position creator, pero ahora no se puede
		omsProxy.registerRole("manager", "calculin", "internal", "member", "public","member");
		omsProxy.registerRole("operador", "calculin", "internal", "member", "public","member");
	
	}
	
	public void enviar_peticion(int n1, int n2)
	{
		try {
			ACLMessage msg = omsProxy.buildOrganizationalMessage("calculin");
			msg.setPerformative(InteractionProtocol.FIPA_REQUEST);
			msg.setProtocol(InteractionProtocol.FIPA_REQUEST);
			msg.setLanguage("ACL");
			msg.setContent(n1+" y "+ n2);

			
			send(msg);
		} catch (THOMASException e) {
			// TODO Auto-generated catch block
			System.out.println("[ "+this.getName()+" ] "+ e.getContent());

		}
	}


}
