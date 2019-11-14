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
     * @param reviewCount
     * @param status
     * @return result true if article is created successfully
     * @throws SQLException 
	 * @throws FileNotFoundException 
     */
    public static boolean createArticle(String title, String description, File pdfFile, String email/*, int reviewCount, String status */ ) throws SQLException, FileNotFoundException {
        openConnection();
        boolean result = false;
        PreparedStatement pstmt = null;
        FileInputStream inputStream= new FileInputStream(pdfFile);
        try {
            pstmt = con.prepareStatement(" INSERT INTO `team021`.`article` (`title`, `abstract`, `linkedFinalPDF`, `isPublished`, `mAuthorEmail`)"
            		+ " VALUES (?, ?, ?, 0, ?)");
            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setBlob(3,inputStream);
            pstmt.setString(4, email);
            
            /*
            pstmt = con.prepareStatement(" INSERT INTO `team021`.`submission` (`submissionID`, `linkedDraftPDF`, `reviewCount`, `status`)"
            		+ " VALUES (?, ?, ?, ?,)");
            */
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

    public static void main (String[] args) throws FileNotFoundException {
    	File pdfFile = new File("./Systems Design Project.pdf");
        try {

            //create article test
            System.out.println(createArticle("Long and Dark", "long and dark nights",pdfFile,"john.barker@dheffff.ac.uk" ));

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
