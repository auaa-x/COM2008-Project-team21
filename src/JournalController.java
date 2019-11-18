/**
 * Class for journal data manipulation in MySQL database
 * @author Urszula Talalaj
 * @author Julia Derebecka
 */
import java.sql.*;
import java.io.*;

public class JournalController extends SqlController{
	
	/**
     * Create a new journal with all parameters
     * @param email
     * @param journal
     * @param ISSN
     * @return result true if journal is created successfully
     * @throws SQLException
     */
    public static boolean createJournal(String email, String journal, int ISSN) throws SQLException {
        openConnection();
        boolean result = false;
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement(" INSERT INTO `team021`.`journal` (`ISSN`, `title`, `chiefEditorEmail`)"
            		+ " VALUES (?, ?, ?)");
            pstmt.setInt(1, ISSN);
            pstmt.setString(2, journal);
            pstmt.setString(3, email);

            int count = pstmt.executeUpdate();
            if (count != 0) result = true;
            System.out.println("Rows updated" + count);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            closeConnection();
        }
        return result;
    }


	/**
     * Create a new article with all parameters
     * @param title
     * @param description
     * @param pdfFile
     * @param email
     * @return result true if article is created successfully
     * @throws SQLException 
	 * @throws FileNotFoundException 
     */
    public static int createArticle(String title, String description, File pdfFile, int ISSN, String email ) throws SQLException, FileNotFoundException {
        openConnection();
        PreparedStatement pstmt = null;
        FileInputStream inputStream= new FileInputStream(pdfFile);
        int submissionID = 0;
        try {
            pstmt = con.prepareStatement(" INSERT INTO `team021`.`article` (`title`, `abstract`, `linkedFinalPDF`, `isPublished`, `ISSN`, `mAuthorEmail`)"
            		+ " VALUES (?, ?, ?, 0, ?, ?)");
            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setBlob(3,inputStream);
            pstmt.setInt(4, ISSN);
            pstmt.setString(5, email);
            
            ResultSet res = pstmt.executeQuery("SELECT * FROM `article` ORDER BY `submissionID` DESC LIMIT 1");
            res.next();
            submissionID = res.getInt(1) + 1;
            
            int count = pstmt.executeUpdate();
            System.out.println("Rows updated " + count);
            //add catch filenotfound exception
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            closeConnection();
        }
        return submissionID;       
    }

    /**
     * Create a new submission linked to article by submissionID
     * @param pdfFile
     * @return result true if article is created successfully
     * @throws SQLException 
	 * @throws FileNotFoundException 
     */
    public static int createSubmission(File pdfFile) throws SQLException, FileNotFoundException {
        openConnection();
        PreparedStatement pstmt = null;
        Statement stmt = null;
        FileInputStream inputStream= new FileInputStream(pdfFile);
        int submissionID = 0;
        try {
        	 stmt = con.createStatement();
        	 ResultSet res = stmt.executeQuery("SELECT * FROM `article` ORDER BY `submissionID` DESC LIMIT 1");
             res.next();
             submissionID = res.getInt(1);
             
             pstmt = con.prepareStatement(" INSERT INTO `team021`.`submission` (`submissionID`, `linkedDraftPDF`, `reviewCount`, `status`) "
             		+ " VALUES (?, ?, 0, ?)");
             pstmt.setInt(1, submissionID);
             pstmt.setBlob(2, inputStream);
             pstmt.setString(3,"submitted");
             
             int count = pstmt.executeUpdate();
             System.out.println("Rows updated " + count);
         } catch (SQLException ex) {
             ex.printStackTrace();
         } finally {
             if (pstmt != null) pstmt.close();
             if (stmt != null) stmt.close();
             closeConnection();
         }
         return submissionID;       
     }
       
    /**
     * Get an article with all parameters by submissionID
     * @param submissionID
     * @return selected article
     * @throws SQLException 
	 * @throws FileNotFoundException 
     */
    public static int getArticle(int submissionId) throws SQLException, FileNotFoundException {
        openConnection();
        PreparedStatement pstmt = null;
        try {
            
            ResultSet res = pstmt.executeQuery("SELECT * FROM article WHERE submissionID=?");
            pstmt.setInt(1, submissionId);
            res.next();
            articlePdf = res.getBlob(4);
            
            int count = pstmt.executeUpdate();
            System.out.println("Rows updated " + count);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            closeConnection();
        }
        return submissionID;       
    }
    
    
    public static void main (String[] args) throws FileNotFoundException {
    	File pdfFile = new File("./Systems Design Project.pdf");
        try {

            //create article test
            System.out.println(createArticle("Long and Dark11", "long and dark nights11", pdfFile, 2934554, "john.barker@dheffff11.ac.uk" ));
            System.out.println(createSubmission(pdfFile));

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
