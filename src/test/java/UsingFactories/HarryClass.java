package UsingFactories;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.CProcessorFactory;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_REQUEST_Initiator;

class HarryClass extends CAgent {

	public HarryClass(AgentID aid) throws Exception {
		super(aid);
	}

	protected void Initialize(CProcessor myProcessor, ACLMessage welcomeMessage) {

		ACLMessage msg;

		System.out.println(myProcessor.getMyAgent().getName()
				+ ": the welcome message is " + welcomeMessage.getContent());

		// Cada conversación del agente es llevada a cabo por un CProcessor y
		// los CProcessors son creados por las CProcessorFactories, en respuesta
		// a mensajes que inician la actividad del agente en una conversación.

		// Una forma sencilla de crear una CProcessorFactory a partir de las
		// fábricas
		// predeterminadas del paquete es.upv.dsic.gti_ia.cAgents.protocols.
		// Otra alternativa, no mostrada en este ejemplo, consiste en que el
		// agente
		// diseñe su propia fábrica y, por tanto, un nuevo protocolo de
		// interacción.

		// En este ejemplo el agente va a actuar como iniciador en el protocolo
		// REQUEST definido por FIPA.
		// Para ello extiende la clase FIPA_REQUEST_Initiator implementando el
		// método que recibe la respuesta (Process_Inform)

		class myFIPA_REQUEST extends FIPA_REQUEST_Initiator {
			protected void Process_Inform(CProcessor myProcessor, ACLMessage msg) {
				System.out.println(myProcessor.getMyAgent().getName() + ": "
						+ msg.getSender().name + " informs me "
						+ msg.getContent());
			}
		}

		// El agente crea la CProcessorFactory que creará procesadores para
		// iniciar
		// conversaciones
		// cuyo protocol sea REQUEST y su performativa REQUEST. En este ejemplo
		// la
		// cProcessorFactory recibe el nombre "TALK", no se le incorpora ningún
		// criterio
		// de aceptación adicional al requerido por el protocolo REQUEST (null)
		// y
		// no se limita el número de procesadores simultáneos (valor 0)

		CProcessorFactory talk = new myFIPA_REQUEST().newFactory("TALK", null,
				1, 0);

		// La fábrica se configura para responder ante solicitudes del agente
		// de inicio de conversaciones usando el protocolo REQUEST

		this.addFactoryAsInitiator(talk);

		// Para iniciar una conversación, el agente crea un mensaje que pueda
		// ser
		// aceptado por una de sus fabricas iniciadoras.

		msg = talk.getTemplate();
		msg.setReceiver(new AgentID("Sally"));
		msg.setContent("May you give me your phone number?");

		// y finalmente se inicia la nueva conversación. Al ser del tipo
		// síncrona,
		// la interacción en curso se detiene hasta que concluya la nueva
		// conversación

		myProcessor.createSyncConversation(msg);

		myProcessor.ShutdownAgent();
	}

	protected void Finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {
	}
}