/**
 * Enum for verdict
 * @author Urszula Talalaj
 */

public enum Verdict {
    
    STRONG_ACCEPT("Strong Accept"),
    WEAK_ACCEPT("Weak Accept"),
    WEAK_REJECT("Weak Reject"),
    STRONG_REJECT("Strong Reject");
    
    private String statusName;
    
    private Verdict(String s) {
        this.statusName = s;
    }
    
    @Override
    public String toString() {
        return statusName;
    }

}
