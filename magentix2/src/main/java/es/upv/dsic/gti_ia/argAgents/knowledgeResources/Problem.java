package es.upv.dsic.gti_ia.argAgents.knowledgeResources;

import java.io.Serializable;

/**
 * Implementation of the owl concept <i>Problem</i>
 * 
 */

public class Problem extends CaseComponent implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8683219055016271802L;
	private DomainContext context;

    public Problem(DomainContext context) {
        this.context = context;
    }


    public Problem() {
    	context = new DomainContext();
    }

    // Property hasDomainContext

    public DomainContext getDomainContext() {
        return (DomainContext) context;
    }


   public void setDomainContext(DomainContext newDomainContext) {
        context = newDomainContext;
    }
}
