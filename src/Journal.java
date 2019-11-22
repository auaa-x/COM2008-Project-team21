
public class Journal {

    private String title;
    private int issn;
    private String chief;
    
    Journal(String title, int issn, String chief) {
        this.title = title;
        this.issn = issn;
        this.chief = chief;              
    }
    
    public String getTitle() {
        return title;
    }
    
    public int getIssn() {
        return issn;
    }
    
    public String getChief() {
        return chief;
    }
}