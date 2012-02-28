package es.upv.dsic.gti_ia.argAgents.knowledgeResources;

/**
 * Implementation of the concept <i>ArgumentProblem</i>
 * 
 */
public class ArgumentProblem extends Problem {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5983549572074070297L;
	private SocialContext socialContext;

    public ArgumentProblem(DomainContext domainContext,SocialContext socialContext) {
		super(domainContext);
		this.socialContext = socialContext;
	}

    public ArgumentProblem() {
    	socialContext = new SocialContext();
    }

    // Property hasSocialContext

    public SocialContext getSocialContext() {
        return (SocialContext) socialContext;
    }


    public void setSocialContext(SocialContext newSocialContext) {
        socialContext = newSocialContext;
    }
}
