package es.upv.dsic.gti_ia.argAgents.knowledgeResources;

/**
 * Implementation of the concept <i>Author</i>
 * 
 */
public class Author {
	
	private String authorName;

    public Author(String authorName) {
        this.authorName = authorName;
    }


    public Author() {
    	authorName = "";
    }


    // Property authorName

    public String getAuthorName() {
        return authorName;
    }


    public void setAuthorName(String newAuthorName) {
        authorName = newAuthorName;
    }
}
