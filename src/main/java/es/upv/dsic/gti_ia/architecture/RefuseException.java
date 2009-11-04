package es.upv.dsic.gti_ia.architecture;



import es.upv.dsic.gti_ia.core.ACLMessage;

/**
This class represents a generic RefuseException 
@author Fabio Bellifemine - CSELT S.p.A. 
@version $Date: 2009-03-03 15:02:51 +0100 (mar, 03 mar 2009) $ $Revision: 6097 $
 */
public class RefuseException extends FIPAException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RefuseException(String message) {
		super(message); 
	}

	public RefuseException(ACLMessage refuse) {
		super(refuse); 
	}

	public ACLMessage getACLMessage() {
		if (msg == null) {
			msg = new ACLMessage(ACLMessage.REFUSE);
			msg.setContent(getMessage()); 
		} 
		return msg;
	}
}
