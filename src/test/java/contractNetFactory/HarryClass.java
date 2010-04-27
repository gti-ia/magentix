package contractNetFactory;

import java.util.ArrayList;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.CProcessorFactory;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_CONTRACTNET_Initiator;

class HarryClass extends CAgent {

	public HarryClass(AgentID aid) throws Exception {
		super(aid);
	}

	protected void Initialize(CProcessor myProcessor, ACLMessage welcomeMessage) {

		ACLMessage msg;

		System.out.println(myProcessor.getMyAgent().getName()
				+ ": the welcome message is " + welcomeMessage.getContent());

		// Cada conversaci�n del agente es llevada a cabo por un CProcessor y
		// los CProcessors son creados por las CProcessorFactories, en respuesta
		// a mensajes que inician la actividad del agente en una conversaci�n.

		// Una forma sencilla de crear una CProcessorFactory a partir de las
		// f�bricas
		// predeterminadas del paquete es.upv.dsic.gti_ia.cAgents.protocols.
		// Otra alternativa, no mostrada en este ejemplo, consiste en que el
		// agente
		// dise�e su propia f�brica y, por tanto, un nuevo protocolo de
		// interacci�n.

		// En este ejemplo el agente va a actuar como iniciador en el protocolo
		// REQUEST definido por FIPA.
		// Para ello extiende la clase FIPA_REQUEST_Initiator implementando el
		// m�todo que recibe la respuesta (Process_Inform)

		class myFIPA_CONTRACTNET extends FIPA_CONTRACTNET_Initiator {

			@Override
			protected void doEvaluateProposals(CProcessor myProcessor,
					ArrayList<ACLMessage> proposes,
					ArrayList<ACLMessage> acceptances,
					ArrayList<ACLMessage> rejections) {
				// al primer li diguem que si
				ACLMessage accept = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
				accept.setContent("Me mola la teua proposta");
				System.out.println("Me mola "+proposes.get(0).getSender()+" "+getAid());
				accept.setReceiver(proposes.get(0).getSender());
				accept.setSender(getAid());
				accept.setProtocol("fipa-contract-net");
				acceptances.add(accept);
				
				//al segon que no
				ACLMessage reject = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
				reject.setContent("No me mola la teua proposta");
				System.out.println("No Me mola "+proposes.get(1).getSender());
				reject.setReceiver(proposes.get(1).getSender());
				reject.setSender(getAid());
				reject.setProtocol("fipa-contract-net");
				rejections.add(reject);				
			}

			@Override
			protected void doReceiveInform(CProcessor myProcessor,
					ACLMessage msg) {
				//rebem resultat de la proposta
				System.out.println("Resultat: "+msg.getContent());
				
			}
			
		}
		
		// Para iniciar una conversaci�n, el agente crea un mensaje que pueda
		// ser
		// aceptado por una de sus fabricas iniciadoras.

		msg = new ACLMessage(ACLMessage.CFP);
		msg.addReceiver(new AgentID("Sally"));
		msg.addReceiver(new AgentID("Sally2"));
		msg.setContent("Cuando quedamos?");

		// El agente crea la CProcessorFactory que crear� procesadores para
		// iniciar
		// conversaciones
		// cuyo protocol sea REQUEST y su performativa REQUEST. En este ejemplo
		// la
		// cProcessorFactory recibe el nombre "TALK", no se le incorpora ning�n
		// criterio
		// de aceptaci�n adicional al requerido por el protocolo REQUEST (null)
		// y
		// no se limita el n�mero de procesadores simult�neos (valor 0)
		
		CProcessorFactory talk = new myFIPA_CONTRACTNET().newFactory("TALK", null, msg,
				1, myProcessor.getMyAgent(), 0, 2, 0);

		// La f�brica se configura para responder ante solicitudes del agente
		// de inicio de conversaciones usando el protocolo REQUEST

		this.addFactoryAsInitiator(talk);

		// y finalmente se inicia la nueva conversaci�n. Al ser del tipo
		// s�ncrona,
		// la interacci�n en curso se detiene hasta que concluya la nueva
		// conversaci�n

		myProcessor.createSyncConversation(msg);

		myProcessor.ShutdownAgent();
	}

	protected void Finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {
	}
}