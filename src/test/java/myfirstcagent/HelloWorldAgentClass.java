package myfirstcagent;

import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;

// Para poder crear un CAgent es necesario en primer lugar
// extender la clase CAgent e implementar los m�todos Initialize y Finalize

class HelloWorldAgentClass extends CAgent {

	public HelloWorldAgentClass(AgentID aid) throws Exception {
		super(aid);
	}

	// La plataforma inicia una conversaci�n con cada agente reci�n creado,
	// envi�ndole un mensaje de bienvenida. Este env�o hace que se cree el
	// primer CProcessor del agente. Para tratar este mensaje, el usuario debe
	// implementar el m�todo Initialize definido por la clase CAgent, m�todo que
	// es ejecutado por el primer CProcessor.

	protected void Initialize(CProcessor myProcessor, ACLMessage welcomeMessage) {

		System.out.println(myProcessor.getMyAgent().getName()
				+ ": the welcome message is " + welcomeMessage.getContent());
		System.out.println(myProcessor.getMyAgent().getName()
				+ ":  inevitably I have to say hello world");
		
		
		
		// El m�todo ShutdownAgent inicia el proceso de finalizar las conversaciones 
		// activas del agente. Cuando este proceso concluye, la plataforma
		// env�a al agente el mensaje de finalizaci�n.
		myProcessor.ShutdownAgent();
	}

	// Para tratar el mensaje de finalizaci�n, el usuario debe
	// implementar el m�todo Finalize definido por la clase CAgent
	
	protected void Finalize(CProcessor myProcessor, ACLMessage finalizeMessage) {

		System.out.println(myProcessor.getMyAgent().getName()
				+ ": the finalize message is " + finalizeMessage.getContent());
	}
}