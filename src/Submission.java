/**
 * Class for Submissions object
 * @author Julia Derebecka
 */
public class Submission {
	private int submissionID;
    private int reviewCount;
    private String status;

    Submission(int submissionID, int reviewCount, String status) {
        this.submissionID = submissionID;
        this.reviewCount = reviewCount;
        this.status = status;
    }

    public int getSubmissionID() {
        return submissionID;
    }

    public int getIssn() {
        return reviewCount;
    }

    public String getStatus() {
        return status;
    }

}
