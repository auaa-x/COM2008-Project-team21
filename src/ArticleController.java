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
import java.util.LinkedList;

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
        Statement stmt = null;
        int submissionID = 0;
        try {
            FileInputStream inputStream = new FileInputStream(pdfFile);
            try {
                ResultSet res = stmt.executeQuery("SELECT COUNT(*) FROM `article`");
                res.next();
                submissionID = res.getInt(1) + 1;
                
                pstmt = con.prepareStatement(" INSERT INTO `team021`.`article` (`submissionID`, `title`, `abstract`, `linkedFinalPDF`, `ISSN`, `mAuthorEmail`)"
                        + " VALUES (?, ?, ?, ?, ?, ?)");
                pstmt.setInt(1, submissionID);
                pstmt.setString(2, title);
                pstmt.setString(3, description);
                pstmt.setBlob(4,inputStream);
                pstmt.setInt(5, ISSN);
                pstmt.setString(6, email);

                int count = pstmt.executeUpdate();
                System.out.println("Rows updated " + count);
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                if (pstmt != null) pstmt.close();
                if (stmt != null) stmt.close();
                closeConnection();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return submissionID;
    }

    /**
     * Create a new submission linked to article by submissionID
     * @param submissionID
     * @param pdfFile
     * @return result true if article is created successfully
     * @throws SQLException
     * @throws FileNotFoundException
     */
    public static int createSubmission(int submissionID, File pdfFile) throws SQLException, FileNotFoundException {
        openConnection();
        PreparedStatement pstmt = null;
        FileInputStream inputStream= new FileInputStream(pdfFile);
        try {
             pstmt = con.prepareStatement(" INSERT INTO `team021`.`submission` (`submissionID`, `linkedDraftPDF`) "
                    + " VALUES (?, ?)");
             pstmt.setInt(1, submissionID);
             pstmt.setBlob(2, inputStream);

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
    
    /**
     * Get all published articles by author
     * @return list of author's articles
     * @throws SQLException
     */
    public static LinkedList<Article> getAuthorsArticles(String email) throws SQLException {
        LinkedList<Article> articles = new LinkedList<Article>();
        openConnection();
        PreparedStatement pstmt = null;
        try {
            
            pstmt = con.prepareStatement("SELECT * FROM article a, author p WHERE (a.submissionID = p.submissionID) and (p.email = ?) and (isPublished = 0)");
            pstmt.setString(1, email); 
            ResultSet res = pstmt.executeQuery();
            
            while (res.next()) {
                int submissionID = res.getInt("submissionID");
                String title = res.getString("title");
                String artAbstract = res.getString("abstract");
                boolean isPublished = res.getBoolean("isPublished");
                int issn = res.getInt("ISSN");
                String mAuthorEmail = res.getString("mAuthorEmail");
                
                Article article = new Article(submissionID, title, artAbstract, isPublished, issn, mAuthorEmail);
                articles.add(article);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            closeConnection();
        }
        return articles;
    }
    
    /**
     * Get all submissions by author
     * @return list of author's submissions
     * @throws SQLException
     */
    public static LinkedList<Submission> getSubmissions(String email) throws SQLException {
        LinkedList<Submission> submissions = new LinkedList<Submission>();
        openConnection();
        PreparedStatement pstmt = null;
        try {
            
            pstmt = con.prepareStatement("SELECT * FROM submission s, author a WHERE (s.submissionID = a.submissionID) and (a.email = ?)");
            pstmt.setString(1, email); 
            ResultSet res = pstmt.executeQuery();
            
            while (res.next()) {
                int submissionID = res.getInt("submissionID");
                int reviewCount = res.getInt("reviewCount");
                String status = res.getString("status");
                
                Submission submission = new Submission(submissionID, reviewCount, status);
                submissions.add(submission);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            closeConnection();
        }
        return submissions;
    }
    
    
    public static void main (String[] args) throws IOException {
    
        try {

            System.out.println(getSubmissions("severus.snape@hogwarts.co.uk"));
            System.out.println(getSubmissions("john.barker@dheffff11.ac.uk"));
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

}
