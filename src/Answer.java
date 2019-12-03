/**
 * Class for Question object
 * @author Julia Derebecka
 */
public class Answer {
	
	private int submissionID;
    private int noNum;
    private String value;
    private String anonID;
    
    
    Answer(int submissionID, int noNum, String value, String anonID) {
        this.submissionID = submissionID;
        this.noNum = noNum;
        this.value = value;
        this.anonID = anonID;
    }
    
    public int getSubmissionID() {
        return submissionID;
    }
    
    public int getNoNum() {
        return noNum;
    }
    
    public String getValue() {
    	return value;
    }
    
    public String getAnonID() {
    	return anonID;
    }
    
    public String toString() {
		return value;
    	
    }

}
