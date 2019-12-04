/**
 * Enum for submission status
 * @author Urszula Talalaj
 */

public enum Status {
    
    SUBMITTED("SUBMITTED"),
    REVIEWS_RECEIVED("REVIEWS RECEIVED"), // all 3 reviews received
    RESPONSES_RECEIVED("RESPONSES RECEIVED"), // all 3 responses
    FINAL_VERDICTS_RECEIVED("FINAL VERDICTS RECEIVED"), // all 3 final verdicts
    COMPLETED("COMPLETED"); // accepted or rejected by an editor
    
    private String statusName;
    
    private Status(String s) {
        this.statusName = s;
    }
    
    @Override
    public String toString() {
        return statusName;
    }
}
