/**
 * Class for Edition object
 * @author Urszula Talalaj
 */
public class Edition {

    private int issn;
    private int volNum;
    private int noNum;
    private int pubMonth;
    private int artCount;
    private boolean isPublished;
    
    
    Edition(int issn, int volNum, int noNum, int pubMonth, int artCount, boolean isPublished) {
        this.noNum = noNum;
        this.pubMonth = pubMonth;
        this.volNum = volNum;
        this.artCount = artCount;
        this.isPublished = isPublished;
    }
    
    public int getNoNum() {
        return noNum;
    }
    
    public int getIssn() {
        return issn;
    }
    
    public int getPubMonth() {
        return pubMonth;
    }
    
    public int getVolNum() {
        return volNum;
    }
    
    public int getArtCount() {
        return artCount;
    }
    
    public boolean isPublished() {
        return isPublished;
    }
    
    public String toString() {
    	return "no. " + getNoNum();
    }
}
