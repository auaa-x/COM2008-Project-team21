/**
 * Class for Edition object
 * @author Urszula Talalaj
 */
public class Edition {

    private int issn;
    private int volNum;
    private int noNum;
    private int pubMonth;
    
    
    Edition(int issn, int volNum, int noNum, int pubMonth) {
        this.noNum = noNum;
        this.pubMonth = pubMonth;
        this.volNum = volNum;
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
 
    
    public String toString() {
    	return "no. " + getNoNum();
    }
}
