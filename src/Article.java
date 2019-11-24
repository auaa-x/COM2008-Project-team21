/**
 * Class for Article object
 * @author Urszula Talalaj
 */
public class Article {

    private int submissionID;
    private String title;
    private String artAbstract;
    //private ???? linkedFinalPDF;
    private boolean isPublished;
    private int issn;
    private String mAuthorEmail;
    
    Article(int submissionID, String title, String artAbstract, boolean isPublished, int issn, String mAuthorEmail) {
        this.submissionID = submissionID;
        this.title = title;
        this.artAbstract = artAbstract;
        this.isPublished = isPublished;
        this.issn = issn;
        this.mAuthorEmail = mAuthorEmail;
    }
    
    public int getSubmissionID() {
        return submissionID;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getAbstract() {
        return artAbstract;
    }
    
    public boolean getIsPublished() {
        return isPublished;
    }
    
    public int getIssn() {
        return issn;
    }
    
    public String getMAuthorEmail() {
        return mAuthorEmail;
    }
}
