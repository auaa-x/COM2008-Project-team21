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
        Statement stmt = con.createStatement();
        int submissionId = 0;
        try {
            FileInputStream inputStream = new FileInputStream(pdfFile);
            try {
                ResultSet res = stmt.executeQuery("SELECT COUNT(*) FROM `article`");
                res.next();
                submissionId = res.getInt(1) + 1;
                
                pstmt = con.prepareStatement(" INSERT INTO `team021`.`article` (`submissionID`, `title`, `abstract`, `linkedFinalPDF`, `ISSN`, `mAuthorEmail`)"
                        + " VALUES (?, ?, ?, ?, ?, ?)");
                pstmt.setInt(1, submissionId);
                pstmt.setString(2, title);
                pstmt.setString(3, description);
                pstmt.setBlob(4,inputStream);
                pstmt.setInt(5, ISSN);
                pstmt.setString(6, email);

                int count = pstmt.executeUpdate();
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
        return submissionId;
    }
    
    
    /**
     * Delete an article from the database
     * @param submissionID
     * @throws SQLException
     */
    public static boolean deleteArticle(int submissionID) throws SQLException {
        boolean result = false;
        openConnection();
        PreparedStatement pstmt = null;
        try {
            // delete the entry from article table
            pstmt = con.prepareStatement("DELETE FROM `team021`.`article` WHERE (`submissionID` = ?)");
            pstmt.setInt(1, submissionID);
            int count = pstmt.executeUpdate();
            if (count == 1) result = true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            closeConnection();
        }
        return result;
    }
    
    
    /**
     * Update the PDF in article table
     * @param submissionId
     * @param pdfFile
     * @return true if update successful, otherwise false
     * @throws SQLException
     */
    public static boolean updatePDFFile(int submissionId, File pdfFile) throws SQLException {
        boolean result = false;
        if (checkArticle(submissionId)) {
            openConnection();
            PreparedStatement pstmt = null;
            try {
            	FileInputStream inputStream = new FileInputStream(pdfFile);
            	try {
                    pstmt = con.prepareStatement("UPDATE `team021`.`article` SET `linkedFinalPDF` = ? WHERE (`submissionId` = ?)");
                    pstmt.setBlob(1, inputStream);
                    pstmt.setInt(2, submissionId);

                    int count = pstmt.executeUpdate();
                    if (count != 0) result = true;
                } catch (SQLException ex) {
                    ex.printStackTrace();
                } finally {
                    if (pstmt != null) pstmt.close();
                    closeConnection();
	            }
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        }
    	}
        return result;
    }
    
    
    /**
     * Check if an article with give submissionID exists in the database
     * @param submissionId
     * @return true if article exists, otherwise false
     * @throws SQLException
     * @throws IOException
     */
    public static boolean checkArticle(int submissionId) throws SQLException {
        openConnection();
        PreparedStatement pstmt = null;
        boolean result = false;
        try {

            pstmt = con.prepareStatement("SELECT * FROM article WHERE submissionID = ?");
            pstmt.setInt(1, submissionId);
            ResultSet res = pstmt.executeQuery();
            if (res.next()) result = true;

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            closeConnection();
        }
        return result;
    }
    

    /**
     * Create a new submission linked to article by submissionID
     * @param submissionId
     * @param pdfFile
     * @return result true if article is created successfully
     * @throws SQLException
     * @throws FileNotFoundException
     */
    public static int createSubmission(int submissionId, File pdfFile) throws SQLException, FileNotFoundException {
        openConnection();
        PreparedStatement pstmt = null;
        FileInputStream inputStream= new FileInputStream(pdfFile);
        try {
             pstmt = con.prepareStatement(" INSERT INTO `team021`.`submission` (`submissionID`, `linkedDraftPDF`) "
                    + " VALUES (?, ?)");
             pstmt.setInt(1, submissionId);
             pstmt.setBlob(2, inputStream);

             int count = pstmt.executeUpdate();
             System.out.println("Rows updated " + count);
         } catch (SQLException ex) {
             ex.printStackTrace();
         } finally {
             if (pstmt != null) pstmt.close();
             closeConnection();
         }
         return submissionId;
     }
    
    
    /**
     * Delete a submission from the database
     * @param submissionID
     * @throws SQLException
     */
    public static boolean deleteSubmission(int submissionID) throws SQLException {
        boolean result = false;
        openConnection();
        PreparedStatement pstmt = null;
        try {
            // delete the entry from article table
            pstmt = con.prepareStatement("DELETE FROM `team021`.`submission` WHERE (`submissionID` = ?)");
            pstmt.setInt(1, submissionID);
            int count = pstmt.executeUpdate();
            if (count == 1) result = true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            closeConnection();
        }
        return result;
    }

    
    /**
     * Get a PDF of an article by submissionID
     * @param submissionId
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
     * Get a PDF of a submission by submissionID
     * @param submissionId
     * @return selected article
     * @throws SQLException
     * @throws IOException
     */
    public static boolean getSubmissionPDF(int submissionId) throws SQLException, IOException {
        openConnection();
        PreparedStatement pstmt = null;
        boolean result = false;
        InputStream input = null;
        FileOutputStream output = null;
        try {

            pstmt = con.prepareStatement("SELECT * FROM submission WHERE submissionID = ?");
            pstmt.setInt(1, submissionId);
            ResultSet res = pstmt.executeQuery();
            File articlePDF = new File("article.pdf");
            output = new FileOutputStream(articlePDF);
            if (res.next()) {
                result = true;
                input = res.getBinaryStream("linkedDraftPDF");
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
     * Get an article by submissionID
     * @param submissionId
     * @return article object
     * @throws SQLException
     * @throws IOException
     */
    public static Article getArticle(int submissionId) throws SQLException {
        openConnection();
        PreparedStatement pstmt = null;
        Article article = null;
        try {
            
            pstmt = con.prepareStatement("SELECT * FROM article WHERE (submissionID = ?) ");
            pstmt.setInt(1, submissionId); 
            ResultSet res = pstmt.executeQuery();
            
            if (res.next()) {
                String title = res.getString("title");
                String artAbstract = res.getString("abstract");
                boolean isPublished = res.getBoolean("isPublished");
                int issn = res.getInt("ISSN");
                String mAuthorEmail = res.getString("mAuthorEmail");
                article = new Article(submissionId, title, artAbstract, isPublished, issn, mAuthorEmail);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            closeConnection();
        }
        return article;
    }
    
    
    /**
     * Get all published articles by author
     * @return list of author's articles
     * @throws SQLException
     */
    public static LinkedList<Article> getPublishedArticles(String email) throws SQLException {
        LinkedList<Article> articles = new LinkedList<Article>();
        openConnection();
        PreparedStatement pstmt = null;
        try {
            
            pstmt = con.prepareStatement("SELECT * FROM article a, author p WHERE (a.submissionID = p.submissionID) and (p.email = ?) and (isPublished = 1)");
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
                Status status = Status.valueOf(res.getString("status"));
                int costCovered = res.getInt("costCovered");
                
                Submission submission = new Submission(submissionID, reviewCount, status, costCovered);
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
    
    
    /**
     * Get all authors of the submission
     * @param submissionId
     * @return list of submission's authors
     * @throws SQLException
     */
    public static LinkedList<String> getAuthors(int submissionId) throws SQLException {
        LinkedList<String> authors = new LinkedList<String>();
        openConnection();
        PreparedStatement pstmt = null;
        try {
            
            pstmt = con.prepareStatement("SELECT * FROM author WHERE (submissionID = ?)");
            pstmt.setInt(1, submissionId); 
            ResultSet res = pstmt.executeQuery();
            
            while (res.next()) {
                String email = res.getString("email");
                
                authors.add(email);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            closeConnection();
        }
        return authors;
    }
    
    
    /**
     * Update status of the submission
     * @param submissionId
     * @param status
     * @return true if update successful, otherwise false
     * @throws SQLException
     */
    public static boolean updateStatus(int submissionId, Status status) throws SQLException {
        boolean result = false;
        if (checkArticle(submissionId)) {
            openConnection();
            PreparedStatement pstmt = null;
            try {
                // use status name to insert into database the exact status name of enum (easier to read status later)
                String statusName = status.name();
                pstmt = con.prepareStatement("UPDATE `team021`.`submission` SET `status` = ? WHERE (`submissionId` = ?)");
                pstmt.setString(1, statusName);
                pstmt.setInt(2, submissionId);

                int count = pstmt.executeUpdate();
                if (count != 0) result = true;
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                if (pstmt != null) pstmt.close();
                closeConnection();
            }
        }
        return result;
    }
    
    
    /**
     * Get all submissions with given status
     * @param status
     * @return list of submissions
     * @throws SQLException
     */
    public static LinkedList<Submission> getSubmissionByStatus(Status status) throws SQLException {
        LinkedList<Submission> submissions = new LinkedList<Submission>();
        openConnection();
        PreparedStatement pstmt = null;
        try {
            String statusName = status.name();
            pstmt = con.prepareStatement("SELECT * FROM submission WHERE (status = ?)");
            pstmt.setString(1, statusName); 
            ResultSet res = pstmt.executeQuery();
            
            while (res.next()) {
                int submissionID = res.getInt("submissionID");
                int reviewCount = res.getInt("reviewCount");
                int costCovered = res.getInt("costCovered");
                
                Submission submission = new Submission(submissionID, reviewCount, status, costCovered);
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
    	File pdfFile = new File("./Systems Design Project.pdf");
        try {

            //System.out.println(getSubmissionByStatus(Status.SUBMITTED));
            //System.out.println(getAuthors(4));
            //System.out.println(updatePDFFile(4, pdfFile)););
            
            System.out.println(deleteArticle(78));
            System.out.println(deleteSubmission(78));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

}
