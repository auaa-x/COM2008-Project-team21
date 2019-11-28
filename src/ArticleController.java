import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class for article data manipulation in MySQL database
 * @author Urszula Talalaj
 * @author Julia Derebecka
 */
public class ArticleController extends SqlController {
    
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
    public static int createArticle(String title, String description, File pdfFile, int ISSN, String email) throws SQLException, FileNotFoundException {
        openConnection();
        PreparedStatement pstmt = null;
        int submissionID = 0;
        try {
            FileInputStream inputStream = new FileInputStream(pdfFile);
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
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                if (pstmt != null) pstmt.close();
                closeConnection();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
             // get the id of article (last entry in the table)
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
     * @throws IOException
     */
    public static boolean getArticlePDF(int submissionId) throws SQLException, IOException {
        openConnection();
        PreparedStatement pstmt = null;
        boolean result = false;
        InputStream input = null;
        FileOutputStream output = null;
        try {

            pstmt = con.prepareStatement("SELECT * FROM article WHERE submissionID = ?");
            pstmt.setInt(1, submissionId);
            ResultSet res = pstmt.executeQuery();
            File articlePDF = new File("article.pdf");
            output = new FileOutputStream(articlePDF);
            if (res.next()) {
                result = true;
                input = res.getBinaryStream("linkedFinalPDF");
                byte[] buffer = new byte [1024];
                while (input.read(buffer) > 0) {
                    output.write(buffer);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            if (input != null) input.close();
            if (output != null) output.close();
            closeConnection();
        }
        return result;
    }

}
