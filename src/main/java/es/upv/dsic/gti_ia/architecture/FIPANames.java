package es.upv.dsic.gti_ia.architecture;

/**
 * Provides a single access point for the set of constants already
 * defined by FIPA. 
 */
public interface FIPANames {
	
	
	/**
	 * Set of constants that identifies the Interaction Protocols and that can
	 * be assigned via
	 * <code>ACLMessage.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST)
	 * </code>
	 */
	public static interface InteractionProtocol {

		/**
		 * The FIPA-Request interaction protocol.
		 */
		public static final String FIPA_REQUEST = "fipa-request";

		/**
		 * The FIPA-Query interaction protocol.
		 */
		public static final String FIPA_QUERY = "fipa-query";

		/**
		 * The FIPA-Propose interaction protocol.
		 */
		public static final String FIPA_PROPOSE = "fipa-propose";

		/**
		 * The FIPA-Subscribe interaction protocol.
		 */
		public static final String FIPA_SUBSCRIBE = "fipa-subscribe";

		/**
		 * The FIPA-Contract-Net interaction protocol.
		 */
		public static final String FIPA_CONTRACT_NET = "fipa-contract-net";

	}
}
