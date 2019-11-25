/**
 * Class for Volume object
 * @author Urszula Talalaj
 */
public class Volume {
    
    private int volNum;
    private int pubYear;
    private int issn;
    private int editionCount;
    
    Volume(int volNum, int pubYear, int issn, int editionCount) {
        this.volNum = volNum;
        this.pubYear = pubYear;
        this.issn = issn;
        this.editionCount = editionCount;
    }
    
    public int getVolNum() {
        return volNum;
    }
    
    public int getIssn() {
        return issn;
    }
    
    public int getPubYear() {
        return pubYear;
    }
    
    public int getEditionCount() {
        return editionCount;
    }

}
