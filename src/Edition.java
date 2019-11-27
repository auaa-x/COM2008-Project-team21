/**
 * Class for Edition object
 * @author Urszula Talalaj
 */
public class Edition {

    private int noNum;
    private int pubMonth;
    private int volNum;
    private int artCount;
    
    Edition(int noNum, int pubMonth, int volNum, int artCount) {
        this.noNum = noNum;
        this.pubMonth = pubMonth;
        this.volNum = volNum;
        this.artCount = artCount;
    }
    
    public int getNoNum() {
        return noNum;
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
