/**
 * Class for Submissions object
 * @author Julia Derebecka
 */
public class Submission {
	private int submissionID;
    private int reviewCount;
    private Status status;

    Submission(int submissionID, int reviewCount, Status status) {
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

    public Status getStatus() {
        return status;
    }

}
