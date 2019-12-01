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
     * Checks if there exists a conflict between a reviewer and a submission
     * @param reviewerEmail
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
     * @param reviewerEmail
     * @return list of submissions available to review
     * @throws SQLException
     */
    public static LinkedList<Submission> getSubmissionsToReview(String reviewerEmail) throws SQLException {
        LinkedList<Submission> submissions = new LinkedList<Submission>();
        
        // return empty list if user is not a reviewer
        if (!UserController.checkUsertype(reviewerEmail, 3)) return submissions;
        
        // get all submissions with SUBMITTED status
        submissions = ArticleController.getSubmissionByStatus(Status.SUBMITTED);
        
        // remove the ones that already have 3 started reviews
        Iterator<Submission> iterator = submissions.iterator();
        while(iterator.hasNext()) {
            Submission s = iterator.next();
            if (s.getReviewCount() >= 3) iterator.remove();
        }
        
        // remove the ones for which a conflict exists
       iterator = submissions.iterator();
        while(iterator.hasNext()) {
            Submission s = iterator.next();
            if (checkReviewerConflict(reviewerEmail, s.getSubmissionID())) iterator.remove();
        }
        
        // remove the ones that had been already taken by this reviewer
        LinkedList<Integer> taken = getReviewingSubmissions(reviewerEmail);
        Iterator<Submission> subItertor = submissions.iterator();
        Iterator<Integer> takenItertor = taken.iterator();
        while(subItertor.hasNext()) {
            Submission s = subItertor.next();
            while(takenItertor.hasNext()) {
                int id = takenItertor.next().intValue();
                if (s.getSubmissionID() == id) subItertor.remove();
            }
        }
        return submissions;       
    }
    
    
    /**
     * Select an article to review as a reviewer
     * @param reviewerEmail
     * @param submissionId
     * @return true if selection successful, otherwise false
     * @throws SQLException
     */
    public static boolean selectToReview(String reviewerEmail, int submissionId) throws SQLException {
        boolean result = false;
        String anonId = "reviewer" + Integer.toString(getReviewCount(submissionId) + 1);
        
        // update the review count, create a reviewer, create a review
        if (updateReviewCount(submissionId) && UserController.createReviewer(anonId, reviewerEmail, submissionId) 
                && createReview(submissionId, anonId)) result = true;
        return result;
    }
    
    
    /**
     * Get a review counter for a given submission (reviews started)
     * @param submissionId
     * @return number of started reviews
     * @throws SQLException
     */
    public static int getReviewCount(int submissionId) throws SQLException {
        int count = 0;
        openConnection();
        PreparedStatement pstmt = null;
        try {
            
            // get the current reviewCount of the submission
            pstmt = con.prepareStatement("SELECT * FROM `submission` WHERE `submissionID` = ?");
            pstmt.setInt(1, submissionId);  
            ResultSet res = pstmt.executeQuery();
            
            if (res.next()) {
                count = res.getInt("reviewCount");
            }
        
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            closeConnection();
        }
        return count;
    }
    
    
    /**
     * Get a list of submissionIDs of submissions which are being reviewed by a given reviewer
     * @param reviewerEmail
     * @return list of submissions
     * @throws SQLException
     */
    public static LinkedList<Integer> getReviewingSubmissions(String reviewerEmail) throws SQLException {
        LinkedList<Integer> submissions = new LinkedList<Integer>();
        openConnection();
        PreparedStatement pstmt = null;
        try {
            
            // get the current reviewCount of the submission
            pstmt = con.prepareStatement("SELECT * FROM `reviewer` WHERE `email` = ?");
            pstmt.setString(1, reviewerEmail);  
            ResultSet res = pstmt.executeQuery();
            
            while (res.next()) {
                int id = res.getInt("submissionID");
                submissions.add(Integer.valueOf(id));
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
     * Update review counter for a given submission
     * @param submissionId
     * @return true if update successful, otherwise false
     * @throws SQLException
     */
    public static boolean updateReviewCount(int submissionId) throws SQLException {
        boolean result = false;
        
        // get current review count
        int count = getReviewCount(submissionId);
        
        // return false if there are too many reviews already
        if (count >= 3) return result;
        
        openConnection();
        PreparedStatement pstmt = null;
        try {
            
            // update review counter
            count++;
            pstmt = con.prepareStatement("UPDATE `team021`.`submission` SET `reviewCount` = ? WHERE (`submissionID` = ?)");
            pstmt.setInt(1, count);  
            pstmt.setInt(2, submissionId); 
            int res = pstmt.executeUpdate();
            
            if (res != 0) {
                result = true;
            }
        
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            closeConnection();
        }
        return result;
    }
    
    
    /**
     * Create a review for a given submission and anonID
     * @param submissionId
     * @param anonID
     * @return true if update successful, otherwise false
     * @throws SQLException
     */
    public static boolean createReview(int submissionId, String anonId) throws SQLException {
        boolean result = false;

        openConnection();
        PreparedStatement pstmt = null;
        try {

            pstmt = con.prepareStatement("INSERT INTO `team021`.`review` (`submissionID`, `anonID`) VALUES (?, ?)");
            pstmt.setInt(1, submissionId);  
            pstmt.setString(2, anonId); 
            int res = pstmt.executeUpdate();
            
            if (res != 0) {
                result = true;
            }
        
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            closeConnection();
        }
        return result;
    }
    
    
    
    public static void main (String[] args) throws IOException {

        try {
            //System.out.println(selectToReview("chaddock@illinois.ac.uk", 1));
            System.out.println(getReviewingSubmissions("chaddock@illinois.ac.uk"));
            System.out.println(getSubmissionsToReview("chaddock@illinois.ac.uk"));
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
