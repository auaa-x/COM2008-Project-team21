/**
 * Enum for submission status
 * @author Urszula Talalaj
 */

public enum Status {
    
    SUBMITTED("SUBMITTED"),
    REVIEWS_RECEIVED("REVIEWS RECEIVED"),
    INITIAL_VERDICT("INITIAL VERDICT"),
    RESPONSES_RECEIVED("RESPONSES RECEIVED"),
    FINAL_VERDICT("FINAL VERDICT"),
    COMPLETED("COMPLETED");
    
    private String statusName;
    
    private Status(String s) {
        this.statusName = s;
    }
    
    @Override
    public String toString() {
        return statusName;
    }
}
