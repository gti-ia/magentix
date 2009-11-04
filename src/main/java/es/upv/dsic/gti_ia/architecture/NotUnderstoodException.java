package es.upv.dsic.gti_ia.architecture;


import es.upv.dsic.gti_ia.core.ACLMessage;

/** 
 * 
 * @see jade.domain.FIPAAgentManagement.FIPAManagementOntology
 * @author Fabio Bellifemine - CSELT S.p.A.
 * @version $Date: 2008-10-06 16:36:33 +0200 (lun, 06 ott 2008) $ $Revision: 6048 $
 */



public class NotUnderstoodException extends FIPAException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotUnderstoodException(String msg) {
		super(msg);
	}

	public NotUnderstoodException(ACLMessage notUnderstood) {
		super(notUnderstood); 
	}

}
