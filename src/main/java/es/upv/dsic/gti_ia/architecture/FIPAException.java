package es.upv.dsic.gti_ia.architecture;

import es.upv.dsic.gti_ia.core.ACLMessage;

public class FIPAException extends Exception {

	protected ACLMessage msg; // can be accessed by subclasses

	private String content;

	/**
	 * Constructs a generic <code>FIPAException</code>. The ACL message
	 * performative is defaulted to <code>not-understood</code>.
	 * 
	 * @param message
	 *            is the content of the ACLMessage
	 */
	public FIPAException(String message) {
		super();
		content = message;
	}

	/**
	 * Constructs a <code>FIPAException</code> from the given ACL message.
	 * 
	 * @param message
	 *            is the ACL message representing this exception
	 */
	public FIPAException(ACLMessage message) {
		this(message.getContent());
		msg = (ACLMessage) message;
	}

	/**
	 * Retrieve the ACL message whose content is represented by this exception.
	 * 
	 * @return the ACLMessage representing this exception
	 */
	public ACLMessage getACLMessage() {
		if (msg == null) {
			msg = new ACLMessage(ACLMessage.NOT_UNDERSTOOD);
			msg.setContent(getMessage());
		}
		return msg;
	}

	/**
	 * Set the content of the ACL message representing this exception
	 * 
	 * @param message
	 *            is the content
	 */
	protected void setMessage(String message) {
		content = message;
		if (msg != null)
			msg.setContent(message);
	}

	/**
	 * Get the content of the ACL message representing this exception
	 * 
	 * @return A string representing the message content that describes this
	 *         FIPA exception.
	 */
	public String getMessage() {
		return content;
	}

}
