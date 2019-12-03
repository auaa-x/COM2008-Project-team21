/**
 * Class for Submissions object
 * @author Julia Derebecka
 */
public class Submission {
	private int submissionID;
    private int reviewCount;
    private Status status;
    private int costCovered;

    Submission(int submissionID, int reviewCount, Status status, int costCovered) {
        this.submissionID = submissionID;
        this.reviewCount = reviewCount;
        this.status = status;
        this.costCovered = costCovered;
    }

    public int getSubmissionID() {
        return submissionID;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public Status getStatus() {
        return status;
    }
    
    public int getCostCovered() {
        return costCovered;
    }
    
    @Override
    public String toString() {
        return ("SubmissionID: " + Integer.toString(submissionID));
    }

}
