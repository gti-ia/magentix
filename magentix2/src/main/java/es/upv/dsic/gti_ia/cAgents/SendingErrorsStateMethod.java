package es.upv.dsic.gti_ia.cAgents;

import es.upv.dsic.gti_ia.core.ACLMessage;

/**
 * This class is not currently used. It needs a revision in order to define its operation
 * @author ricard
 *
 */

public abstract class SendingErrorsStateMethod {


	protected abstract String run(CProcessor myProcessor, ACLMessage errorMessage);

}
