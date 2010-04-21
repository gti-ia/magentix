package UsingFactories;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.CProcessorFactory;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_REQUEST_Participant;

class SallyClass extends CAgent {

	public SallyClass(AgentID aid) throws Exception {
		super(aid);
	}

	protected void Initialize(CProcessor myProcessor, ACLMessage welcomeMessage) {

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

		// En este ejemplo el agente va a actuar como participante en el
		// protocolo
		// REQUEST definido por FIPA.
		// Para ello extiende la clase FIPA_REQUEST_Participant implementando el
		// m�todo que recibe la petici�n (DO_Request) y el m�todo que genera la
		// respuesta
		// (DO_Inform)

		class myFIPA_REQUEST extends FIPA_REQUEST_Participant {

			@Override
			protected String doAction(CProcessor myProcessor) {
				System.out.println("Nada que hacer en la acción, solo retornar el siguiente estado");
				return "INFORM";
			}

			@Override
			protected void doInform(CProcessor myProcessor, ACLMessage response) {
				response.setContent("Maybe someday");
				System.out.println(myProcessor.getMyAgent().getName()
						+ ": I send the answer to " + myProcessor.getLastReceivedMessage().getSender().name);
				
			}

			@Override
			protected String doReceiveRequest(CProcessor myProcessor,
					ACLMessage request) {
				System.out.println("Siempre aceptamos peticiones");
				return "AGREE";
			}
		}

		// El agente crea la CProcessorFactory que atender� los mensajes
		// entrantes
		// cuyo protocol sea REQUEST y su performativa REQUEST. En este ejemplo
		// la
		// cProcessorFactory recibe el nombre "TALK", no se le incorpora ning�n
		// criterio
		// de aceptaci�n adicional al requerido por el protocolo REQUEST (null)
		// y
		// se limita el n�mero de procesadores simult�neos a 1, es decir, las
		// peticiones
		// se atender�n una por una.

		CProcessorFactory talk = new myFIPA_REQUEST().newFactory("TALK", null,
				0, myProcessor.getMyAgent());

		// Por �ltimo la f�brica se configura para responder ante mensajes
		// entrantes
		// que puedan hacer que comience la participaci�n del agente en una
		// conversaci�n

		this.addFactoryAsParticipant(talk);
	}

	protected void Finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {
	}
}