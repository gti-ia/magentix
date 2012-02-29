package persistence;

/**
 * This class represents a service profile with its URL and a suitability
 * factor. It is implemented as a Comparable object to order list of these
 * objects from higher to lower suitability
 * 
 * @author Jaume Jordan
 * 
 */
public class Profile implements Comparable<Object> {

	private String url;
	private float suitability;

	public Profile(String url, float suitability) {
		this.url = url;
		this.suitability = suitability;
	}

	public float getSuitability() {
		return suitability;
	}

	public void setSuitability(float suitability) {
		this.suitability = suitability;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public boolean equals(Object obj) {

		Profile prof = (Profile) obj;
		if (prof.getUrl().equalsIgnoreCase(getUrl()))
			return true;
		else
			return false;
	}

	@Override
	public int compareTo(Object obj) {
		Profile otherProfile = (Profile) obj;
		return Math.round(otherProfile.getSuitability() * 100000 - this.suitability * 100000);
	}

}
