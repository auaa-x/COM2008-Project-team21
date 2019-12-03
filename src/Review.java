/**
 * Class for Review object
 * @author Julia Derebecka
 */
public class Review {
	private int submissionID;
    private String summary;
    private String typoErrors;
    private String anonID;
    private boolean isSubmitted;

    Review(int submissionID, String summary, String typoErrors, String anonID, boolean isSubmitted) {
        this.submissionID = submissionID;
        this.summary = summary;
        this.typoErrors = typoErrors;
        this.anonID = anonID;
        this.isSubmitted = isSubmitted;
    }

    public int getSubmissionID() {
        return submissionID;
    }

    public String getSummary() {
        return summary;
    }

    public String getTypoErrors() {
        return typoErrors;
    }
    
    public String getAnonId() {
        return anonID;
    }
    
    public boolean getIsSubmitted() {
    	return isSubmitted;
    }
    
    @Override
    public String toString() {
        return ("Review's submissionID: " + Integer.toString(submissionID));
    }

}
