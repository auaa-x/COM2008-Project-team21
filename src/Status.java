/**
 * Enum for submission status
 * @author Urszula Talalaj
 */

public enum Status {
    
    SUBMITTED("SUBMITTED"),
    REVIEWS_RECEIVED("REVIEWS RECEIVED"), // all 3 reviews received
    RESPONSES_RECEIVED("RESPONSES RECEIVED"), // all 3 responses
    COMPLETED("COMPLETED"); // all 3 final verdicts
    
    private String statusName;
    
    private Status(String s) {
        this.statusName = s;
    }
    
    @Override
    public String toString() {
        return statusName;
    }
}
