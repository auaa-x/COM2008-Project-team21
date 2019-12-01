/**
 * Class for review data manipulation in MySQL database
 * @author Urszula Talalaj
 */

import java.io.IOException;
import java.sql.*;
import java.util.Iterator;
import java.util.LinkedList;

public class ReviewController extends SqlController {
    
    /**
     * Checks if there exists a conflict between an editor and a submission
     * @param email - editor's email
     * @param submissionId
     * @return true if there is a conflict, false otherwise
     * @throws SQLException
     */
    public static boolean checkReviewerConflict(String reviewerEmail, int submissionId) throws SQLException {
        boolean result = true;
        LinkedList<String> authors = ArticleController.getAuthors(submissionId);
        LinkedList<String> authorsUnis = new LinkedList<String>();
        openConnection();
        PreparedStatement pstmt = null;
        try {
            
            // get uni affiliation of the reviewer
            pstmt = con.prepareStatement("SELECT * FROM `user` WHERE `email` = ?");
            pstmt.setString(1, reviewerEmail);
            ResultSet res = pstmt.executeQuery();
            
            String editorUni;
            if (res.next()) {
                editorUni = res.getString("uniAffiliation");
            } else return result;
            
            
            // get uni affiliations of authors
            for (String e : authors) {
                pstmt.clearParameters();
                pstmt.setString(1, e);
                res = pstmt.executeQuery();
                
                String authorUni;
                if (res.next()) {
                    authorUni = res.getString("uniAffiliation");
                    authorsUnis.add(authorUni);
                } else return result;
            }
            
            // check if there is a conflict
            for (String u : authorsUnis) {
                if (u != null && u.equals(editorUni)) return result;
            }
            
            // if no return earlier then there is no conflict
            result = false;
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            closeConnection();
        }
        return result;
    }
    
    /**
     * Get a list of submissions available to review by a given reviewer
     * @param email - reviewer email
     * @return list of submissions available to review
     * @throws SQLException
     */
    public static LinkedList<Submission> getSubmissionsToReview(String reviewerEmail) throws SQLException {
        LinkedList<Submission> submissions = new LinkedList<Submission>();
        
        // return empty list if user is not a reviewer
        if (!UserController.checkUsertype(reviewerEmail, 3)) return submissions;
        
        // get all submissions with SUBMITTED status
        submissions = ArticleController.getSubmissionByStatus(Status.SUBMITTED);
        
        // remove the ones for which a conflict exists
        Iterator<Submission> iterator = submissions.iterator();
        while(iterator.hasNext()) {
            Submission s = iterator.next();
            if (checkReviewerConflict(reviewerEmail, s.getSubmissionID())) iterator.remove();
        }
        return submissions;
        
    }
    
    public static void main (String[] args) throws IOException {

        try {
            System.out.println(getSubmissionsToReview("chaddock@illinois.ac.uk"));
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
