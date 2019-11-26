/**
 * Class for Journal object
 * @author Urszula Talalaj
 */
public class Journal {

    private int issn;
    private String title;
    private String chief;
    
    Journal(int issn, String title, String chief) {
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