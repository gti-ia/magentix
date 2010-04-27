package contractNetFactory;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.CProcessorFactory;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_CONTRACTNET_Participant;

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

		class myFIPA_CONTRACTNET extends FIPA_CONTRACTNET_Participant {

			@Override
			protected String doReceiveSolicit(CProcessor myProcessor,
					ACLMessage msg) {
				// acceptem totes les sol·licituds
				return "SEND_PROPOSAL";
			}

			@Override
			protected void doSendInfo(CProcessor myProcessor,
					ACLMessage messageToSend) {
				messageToSend.setSender(getAid());
				messageToSend.setReceiver(myProcessor.getLastReceivedMessage().getSender());
				System.out.println("Receiver inform "+myProcessor.getLastReceivedMessage().getSender()+" content "+myProcessor.getLastReceivedMessage().getContent());
				messageToSend.setContent("Soc "+getAid()+" Dema a les 20:00");
				messageToSend.setPerformative(ACLMessage.INFORM);
				messageToSend.setProtocol("fipa-contract-net");				
			}

			@Override
			protected void doSendProposal(CProcessor myProcessor,
					ACLMessage messageToSend) {
				messageToSend.setSender(getAid());
				messageToSend.setReceiver(myProcessor.getLastReceivedMessage().getSender());
				messageToSend.setContent("Dema a les 20:00");
				messageToSend.setPerformative(ACLMessage.PROPOSE);
				messageToSend.setProtocol("fipa-contract-net");						
			}

			@Override
			protected String doTask(CProcessor myProcessor,
					ACLMessage solicitMessage) {
				System.out.println(getAid()+" Pensem que fer");
				return "SEND_INFORM";
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

		CProcessorFactory talk = new myFIPA_CONTRACTNET().newFactory("TALK", null, 
				null, 1, myProcessor.getMyAgent(), 0);
		
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