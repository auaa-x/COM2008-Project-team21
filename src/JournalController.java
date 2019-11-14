/**
 * Class for article manipulation in MySQL database
 * @author Urszula Talalaj
 * @author Julia Derebecka
 */
import java.sql.*;

public class JournalController extends SqlController{
	
	/**
     * Create a new article with all parameters
     * @param articleID
     * @param title
     * @param description
     * @return result true if journal is created successfully
     * @throws SQLException 
     */
    public static boolean createArticle(String articleID, String title, String description, ) throws SQLException {
        openConnection();
        boolean result = false;
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement(" INSERT INTO `team021`.`article` (`articleID`, `title`, `abstract`, `linkedFinalPDF`, `isPublished`, `submissionID`, `mAuthorEmail`)"
            		+ " VALUES (?, ?, ?, ?, ?, ?, ?)");
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

}
