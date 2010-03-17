package es.upv.dsic.gti_ia.cAgents;

import es.upv.dsic.gti_ia.core.ACLMessage;

public class CInitiatorFactory extends CProcessorFactory {

	public CInitiatorFactory(String name, ACLMessage template,
			int availableConversations) {
		super(name, template, availableConversations, CProcessorFactory.FactoryRole.Initiator);
	}

}
