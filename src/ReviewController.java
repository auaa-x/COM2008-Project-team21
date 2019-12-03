/**
 * Class for review data manipulation in MySQL database
 * @author Urszula Talalaj
 * @author Julia Derebecka
 */

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

public class ReviewController extends SqlController {

    protected static LinkedList<String> questionList = new LinkedList<String>();
    protected static LinkedList<String> answerList = new LinkedList<String>();

    /**
     * Get question list
     * @return current list of questions
     */
    public LinkedList<String> getQuestionList() {
        return questionList;
    }


    /**
     * Get answer list
     * @return current list of questions
     */
    public LinkedList<String> getAnswerList() {
        return answerList;
    }


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
        LinkedList<Submission> taken = getReviewingSubmissions(reviewerEmail);
        Iterator<Submission> subItertor = submissions.iterator();
        Iterator<Submission> takenIterator = taken.iterator();
        while(subItertor.hasNext()) {
            Submission s = subItertor.next();
            while(takenIterator.hasNext()) {
                int id = takenIterator.next().getSubmissionID();
                if (s.getSubmissionID() == id) subItertor.remove();
            }
        }
        return submissions;
    }


    /**
     * Get a list of submissions selected by a reviewer to review but not yet submitted
     * @param reviewerEmail
     * @param anonId
     * @return submissions selected by a reviewer but not yet submitted
     * @throws SQLException
     */
    public static LinkedList<Submission> getSubmissionsSelected(String reviewerEmail, String anonId) throws SQLException {
        LinkedList<Submission> submissions = new LinkedList<Submission>();

        // return empty list if user is not a reviewer
        if (!UserController.checkUsertype(reviewerEmail, 3)) return submissions;

        // get all submissions which the reviewer is reviewing
        submissions = getReviewingSubmissions(reviewerEmail);

        // remove the ones that have status different to SUBMITTED
        Iterator<Submission> iterator = submissions.iterator();
        while(iterator.hasNext()) {
            Submission s = iterator.next();
            if (!s.getStatus().equals(Status.SUBMITTED)) {
                System.out.println("NOT EQUAL");
                iterator.remove();
            }
        }

        // REMOVE THE ONES THAT ARE ALREADY SUBMITTED use isSubmitted(reviewerEmail, anonId)
        // ------------ code goes here -------------

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

        // create anonymous reviewer ID
        String anonId = "reviewer" + Integer.toString(getReviewCount(submissionId) + 1);

        // update the review count, create a reviewer, create a review
        if (updateReviewCount(submissionId) && UserController.createReviewer(anonId, reviewerEmail, submissionId)
                && createReview(submissionId, anonId)) result = true;

        // get all submissions of the reviewer
        LinkedList<Submission> authorSubmissions = ArticleController.getSubmissions(reviewerEmail);

        // update the cost covered counter of one of them
        boolean found = false;
        for(Submission s : authorSubmissions) {
            if (!found && s.getReviewCount() <= 2) {
                updateCostCovered(s.getSubmissionID());
                found = true;
            }
        }

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
     * Check if review is submitted
     * @param submissionID
     * @param anonID
     * @return result true if it is , otherwise false
     */
    public static boolean isSubmitted(int submissionID, String anonID) throws SQLException {
        openConnection();
        boolean submitted = false;
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement("SELECT * FROM `review` WHERE (`submissionID` = ?) and (`anonID` = ?) ");
            pstmt.setInt(1, submissionID);
            pstmt.setString(2, anonID);
            ResultSet res = pstmt.executeQuery();
            while (res.next()) {
            	submitted = res.getBoolean("isSubmitted");

            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            closeConnection();
        }
     return submitted;
    }


    /**
     * Get a cost covered counter for a given submission (reviews started to cover the cost of publication)
     * @param submissionId
     * @return number of started reviews
     * @throws SQLException
     */
    public static int getCostCovered(int submissionId) throws SQLException {
        int cost = 0;
        openConnection();
        PreparedStatement pstmt = null;
        try {

            // get the current costCovered of the submission
            pstmt = con.prepareStatement("SELECT * FROM `submission` WHERE `submissionID` = ?");
            pstmt.setInt(1, submissionId);
            ResultSet res = pstmt.executeQuery();

            if (res.next()) {
                cost = res.getInt("costCovered");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            closeConnection();
        }
        return cost;
    }


    /**
     * Get remaining review counter for a given reviewer
     * @param reviewerEmail
     * @return number of remaining reviews
     * @throws SQLException
     */
    public static int remainingCostToCover(String reviewerEmail) throws SQLException {

        // get all submissions done by this author
        LinkedList<Submission> submissions = ArticleController.getSubmissions(reviewerEmail);

        // count all reviews that he is allowed to make
        int remaining = 3 * submissions.size();

        for(Submission s : submissions) {
            // subtract the submissions that had been already started
            int started = getCostCovered(s.getSubmissionID());
            System.out.println("Started reviews for sumbissionID " + s.getSubmissionID() + ": " + started);
            remaining -= started;
        }
        return remaining;
    }


    /**
     * Get a list of all submissions which are being reviewed by a given reviewer
     * @param reviewerEmail
     * @return list of submissions
     * @throws SQLException
     */
    public static LinkedList<Submission> getReviewingSubmissions(String reviewerEmail) throws SQLException {
        LinkedList<Submission> submissions = new LinkedList<Submission>();
        openConnection();
        PreparedStatement pstmt = null;
        try {

            pstmt = con.prepareStatement("SELECT * FROM reviewer r, submission s WHERE (s.submissionID = r.submissionID) and (r.email = ?)");
            pstmt.setString(1, reviewerEmail);
            ResultSet res = pstmt.executeQuery();

            while (res.next()) {
                int submissionID = res.getInt("submissionID");
                int reviewCount = res.getInt("reviewCount");
                String stringStatus = res.getString("status").replaceAll(" ", "_");
                Status status = Status.valueOf(stringStatus);
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
     * Update cost covered for a given submission
     * @param submissionId
     * @return true if update successful, otherwise false
     * @throws SQLException
     */
    public static boolean updateCostCovered(int submissionId) throws SQLException {
        boolean result = false;

        // get current review count
        int count = getCostCovered(submissionId);

        // return false if there are too many reviews already
        if (count >= 3) return result;

        openConnection();
        PreparedStatement pstmt = null;
        try {

            // update review counter
            count++;
            pstmt = con.prepareStatement("UPDATE `team021`.`submission` SET `costCovered` = ? WHERE (`submissionID` = ?)");
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


    /**
     * Add a summary of a review for a given submissionID and anonID
     * @param submissionId
     * @param anonID
     * @param summary
     * @return true if update successful, otherwise false
     * @throws SQLException
     */
    public static boolean addSummaryToReview(int submissionId, String anonId, String summary) throws SQLException {
        boolean result = false;

        openConnection();
        PreparedStatement pstmt = null;
        try {

            pstmt = con.prepareStatement("UPDATE `team021`.`review` SET `summary` = ? WHERE (`submissionID` = ?) and (`anonID` = ?)");
            pstmt.setString(1, summary);
            pstmt.setInt(2, submissionId);
            pstmt.setString(3, anonId);
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
     * Add typos to a review for a given submissionID and anonID
     * @param submissionId
     * @param anonID
     * @param typoErrors
     * @return true if update successful, otherwise false
     * @throws SQLException
     */
    public static boolean addTyposToReview(int submissionId, String anonId, String typoErrors) throws SQLException {
        boolean result = false;

        openConnection();
        PreparedStatement pstmt = null;
        try {

            pstmt = con.prepareStatement("UPDATE `team021`.`review` SET `typoErrors` = ? WHERE (`submissionID` = ?) and (`anonID` = ?)");
            pstmt.setString(1, typoErrors);
            pstmt.setInt(2, submissionId);
            pstmt.setString(3, anonId);
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
     * Submit a review with summary, typos, and a verdict
     * @param submissionId
     * @param anonID
     * @param summary
     * @param typos
     * @param verdict
     * @return true if submission successful, otherwise false
     * @throws SQLException
     */
    public static boolean submitReview(int submissionId, String anonId, String summary, String typos, Verdict verdict) throws SQLException {
        boolean result = false;
        if (addSummaryToReview(submissionId, anonId, summary) && addTyposToReview(submissionId, anonId, typos) &&
                addAllQuestions(submissionId, anonId) && addVerdict(submissionId, verdict, anonId)) {
            result = true;

            // update the isSubmitted field
            openConnection();
            PreparedStatement pstmt = null;
            try {

                pstmt = con.prepareStatement("UPDATE `team021`.`review` SET `isSubmitted` = ? WHERE (`submissionID` = ?) and (`anonID` = ?)");
                pstmt.setInt(1, 1);
                pstmt.setInt(2, submissionId);
                pstmt.setString(3, anonId);
                pstmt.executeUpdate();

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
     * Add all the questions to the review
     * Make an entry for each one in the question table
     * @param sharedPassword
     * @param submissionID
     * @return true if addition of all the questions is successful, otherwise false
     * @throws SQLException
     */
    public static boolean addAllQuestions(int submissionId, String anonId) throws SQLException {
        boolean result = true;
        ListIterator<String> iterator = questionList.listIterator();

        openConnection();
        PreparedStatement pstmt = null;
        try {
            int noNum = 1;
            // create a question for each item in the list
            while(iterator.hasNext()) {
                String question = iterator.next();
                pstmt = con.prepareStatement("INSERT INTO `team021`.`question` (`submissionID`, `noNum`, `value`, `anonID`) VALUES (?, ?, ?, ?)");
                pstmt.setInt(1, submissionId);
                pstmt.setInt(2, noNum);
                pstmt.setString(3, question);
                pstmt.setString(4, anonId);
                int res = pstmt.executeUpdate();
                noNum++;
                if (res == 0) result = false;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            closeConnection();
        }

        questionList.clear();
        return result;
    }


    /**
     * Add a verdict to a given submission
     * @param verdict
     */
    public static boolean addVerdict(int submissionId, Verdict verdict, String anonId) throws SQLException {
        boolean result = false;
        openConnection();
        PreparedStatement pstmt = null;
        try {
            String verdictName = verdict.name();
            pstmt = con.prepareStatement("INSERT INTO `team021`.`verdict` (`submissionID`, `value`, `anonID`) VALUES (?, ?, ?);");
            pstmt.setInt(1, submissionId);
            pstmt.setString(2, verdictName);
            pstmt.setString(3, anonId);

            int res = pstmt.executeUpdate();
            if (res != 0) result = true;

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            closeConnection();
        }
        return result;
    }

    /**
     * Update verdict to a given submission
     * @param verdict
     */
    public static boolean updateVerdict(int submissionId, Verdict verdict, String anonId) throws SQLException {
        boolean result = false;
        openConnection();
        PreparedStatement pstmt = null;
        try {
            String verdictName = verdict.name();
            pstmt = con.prepareStatement("UPDATE `team021`.`verdict` SET `value` = ? WHERE (`submissionID` = ?) and (`anonID` = ?)");
            pstmt.setString(1, verdictName);
            pstmt.setInt(2, submissionId);
            pstmt.setString(3, anonId);

            int res = pstmt.executeUpdate();
            if (res != 0) result = true;

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            closeConnection();
        }
        return result;
    }

    /**
     * Get a list of all questions for this review
     * @param submissionID
     * @param anonID
     * @return list of questions
     * @throws SQLException
     */
    public static LinkedList<Question> getQuestions(int submissionID, String anonID) throws SQLException {
        LinkedList<Question> questions = new LinkedList<Question>();
        openConnection();
        PreparedStatement pstmt = null;
        try {
        	pstmt = con.prepareStatement("SELECT * FROM `question` WHERE (`submissionID` = ?) and (`anonID` = ?) ");
            pstmt.setInt(1, submissionID);
            pstmt.setString(2, anonID);
            ResultSet res = pstmt.executeQuery();
            while (res.next()) {
            	int noNum = res.getInt("noNum");
                String value = res.getString("value");
                Question question = new Question(submissionID, noNum, value, anonID);
                questions.add(question);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            closeConnection();
        }
        return questions;
    }

    /**
     * Get a list of all answers for this review
     * @param submissionID
     * @param anonID
     * @return list of questions
     * @throws SQLException
     */
    public static LinkedList<Answer> getAnswers(int submissionID, String anonID) throws SQLException {
        LinkedList<Answer> answers = new LinkedList<Answer>();
        openConnection();
        PreparedStatement pstmt = null;
        try {
        	pstmt = con.prepareStatement("SELECT * FROM `answer` WHERE (`submissionID` = ?) and (`anonID` = ?) ");
            pstmt.setInt(1, submissionID);
            pstmt.setString(2, anonID);
            ResultSet res = pstmt.executeQuery();
            while (res.next()) {
            	int noNum = res.getInt("noNum");
                String value = res.getString("value");
                Answer answer = new Answer(submissionID, noNum, value, anonID);
                answers.add(answer);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            closeConnection();
        }
        return answers;
    }


    /**
     * Submits response
     * Make an entry for each one in the response table
     * @param submissionId
     * @param anonId
     * @param pdfFile
     * @return true if addition of all the responses is successful, otherwise false
     * @throws SQLException
     */
    public static boolean submitResponse(int submissionId, String anonId,  File pdfFile) throws SQLException {
        boolean result = false;
        if (addAllAnswers(submissionId, anonId) && ArticleController.updatePDFFile(submissionId, pdfFile)) {
	        	openConnection();
	        PreparedStatement pstmt = null;
	        try {

	            pstmt = con.prepareStatement("INSERT INTO `team021`.`response` (`submissionID`, `anonID`) VALUES (?, ?)");
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
        }
        return result;
    }


    /**
     * Add all the answers to the review
     * Make an entry for each one in the answers table
     * @param sharedPassword
     * @param submissionID
     * @return true if addition of all the answers is successful, otherwise false
     * @throws SQLException
     */
    public static boolean addAllAnswers(int submissionId, String anonId) throws SQLException {
        boolean result = true;
        ListIterator<String> iterator = answerList.listIterator();

        openConnection();
        PreparedStatement pstmt = null;
        try {
            int noNum = 1;
            // create an answer for each item in the list
            while(iterator.hasNext()) {
                String answer = iterator.next();
                pstmt = con.prepareStatement("INSERT INTO `team021`.`answer` (`submissionID`, `noNum`, `value`, `anonID`) VALUES (?, ?, ?, ?)");
                pstmt.setInt(1, submissionId);
                pstmt.setInt(2, noNum);
                pstmt.setString(3, answer);
                pstmt.setString(4, anonId);
                int res = pstmt.executeUpdate();
                noNum++;
                if (res == 0) {
                    result = false;
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            closeConnection();
        }

        answerList.clear();
        return result;
    }


    /**
     * Add an answer to the answer list
     * @param answer
     */
    public static void addAnswer(String answer) {
        answerList.add(answer);
    }


    /**
     * Add a question to the question list
     * @param question
     */
    public static void addQuestion(String question) {
        answerList.add(question);
    }
    
    //methods for author
    /**
     * Get submission
     * @param submissionId
     * @return submissionId,reviewCount,status,costCovered
     * @throws SQLException
     */
    public static Submission getSubmission(int submissionId) throws SQLException {
        Submission submission = null;
        openConnection();
        PreparedStatement pstmt = null;
        try {
        	pstmt = con.prepareStatement("SELECT * FROM `submission` WHERE (`submissionID` = ?) ");
            pstmt.setInt(1, submissionId);
            ResultSet res = pstmt.executeQuery();
            while (res.next()) {
            	int reviewCount = res.getInt("reviewCount");
                Status status= Status.valueOf(res.getString("status"));
                int costCovered = res.getInt("costCovered");
                submission = new Submission (submissionId, reviewCount,status, costCovered);
                
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            closeConnection();
        }
        return submission;
    }
    
    /**
     * Get review
     * @param submissionId
     * @return submissionId,summary,typoErrors,anonId,isSubmitted
     * @throws SQLException
     */
    public static Review getReview(int submissionId) throws SQLException {
        Review review = null;
        openConnection();
        PreparedStatement pstmt = null;
        try {
        	pstmt = con.prepareStatement("SELECT * FROM `review` WHERE (`submissionID` = ?) ");
            pstmt.setInt(1, submissionId);
            ResultSet res = pstmt.executeQuery();
            while (res.next()) {
            	String summary = res.getString("summary");
            	String typoErrors = res.getString("typoErrors");
            	String anonID = res.getString("anonID");
            	boolean isSubmitted = res.getBoolean("isSubmitted");
                review = new Review (submissionId, summary, typoErrors, anonID, isSubmitted);
                
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            closeConnection();
        }
        return review;
    }




    public static void main (String[] args) throws IOException {
    	//plzzz dont delete
    	//File pdfFile = new File("./Systems Design Project.pdf");
        try {
            /*
            System.out.println("Remaining review count: " + remainingCostToCover("chaddock@illinois.ac.uk"));
            System.out.println("Submissions to review: " + getSubmissionsToReview("chaddock@illinois.ac.uk"));
            System.out.println(selectToReview("chaddock@illinois.ac.uk", 1));
            System.out.println("Selected submission 1 to review.");
            System.out.println("Remaining review count: " + remainingCostToCover("chaddock@illinois.ac.uk"));
            System.out.println("Submissions to review: " + getSubmissionsToReview("chaddock@illinois.ac.uk"));
            System.out.println("Reviewing submission: " + getReviewingSubmissions("chaddock@illinois.ac.uk"));
            */
            //System.out.println("Reviewing submission: " + getReviewingSubmissions("chaddock@illinois.ac.uk"));
            //System.out.println(getSubmissionsSelected("chaddock@illinois.ac.uk","reviewer1"));
            System.out.println(getReview(1).getAnonId());

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
