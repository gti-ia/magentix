package es.upv.dsic.gti_ia.organization;

/**
 * Class that represents a service provider (agent or organization)
 * @author Jaume Jordan
 *
 */
public class Provider {

	private String entityID;
	private String entityType;
	private String language;
	private String performative;
	
	public Provider(String entityID, String entityType, String language,
			String performative) {
		super();
		this.entityID = entityID;
		this.entityType = entityType;
		this.language = language;
		this.performative = performative;
	}

	public Provider(){
		this.entityID = "";
		this.entityType = "";
		this.language = "";
		this.performative = "";
	}
	
	public String getEntityID() {
		return entityID;
	}

	public void setEntityID(String entityID) {
		this.entityID = entityID;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getPerformative() {
		return performative;
	}

	public void setPerformative(String performative) {
		this.performative = performative;
	}
	
	
	
}
