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
    
    
    Edition(int issn, int volNum, int noNum, int pubMonth, int artCount) {
        this.noNum = noNum;
        this.pubMonth = pubMonth;
        this.volNum = volNum;
        this.artCount = artCount;
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
    
    public String toString() {
    	return "vol. " + getVolNum() + "no. " + getNoNum();
    }
}
