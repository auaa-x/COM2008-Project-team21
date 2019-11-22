/**
 * Class for journal data manipulation in MySQL database
 * @author Urszula Talalaj
 * @author Julia Derebecka
 */
import java.sql.*;
import java.util.LinkedList;
import java.io.*;

public class JournalController extends SqlController {


	/**
     * Create a new journal with all parameters
     * @param email
     * @param journal
     * @param ISSN
     * @return result true if journal is created successfully
     * @throws SQLException
     */
    public static boolean createJournal(String email, String journal, int issn) throws SQLException {
        boolean result = false;
        if (!checkIssn(issn)) {
            openConnection();
            PreparedStatement pstmt = null;
            try {
                pstmt = con.prepareStatement(" INSERT INTO `team021`.`journal` (`ISSN`, `title`, `chiefEditorEmail`)"
                		+ " VALUES (?, ?, ?)");
                pstmt.setInt(1, issn);
                pstmt.setString(2, journal);
                pstmt.setString(3, email);

                int count = pstmt.executeUpdate();
                if (count != 0) result = true;
                System.out.println("Journal " + journal + " " + issn + " added");
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
     * Check if ISSN exist in the database
     * @param issn
     * @return result true if email exists, otherwise false
     */
    public static boolean checkIssn(int issn) throws SQLException {
        openConnection();
        boolean result = false;
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement("SELECT * FROM journal WHERE ISSN=?");
            pstmt.setInt(1, issn);
            ResultSet res = pstmt.executeQuery();

            result = res.next();
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

    /**
         * Get all journals from database
         * @return list of journals
         * @throws SQLException
         * @throws IOException
         */
        public static LinkedList<String> getJournals() throws SQLException, IOException {
        	LinkedList<String> journals = new LinkedList<String>();
            openConnection();
            Statement stmt = null;
            boolean result = false;
            try {

                ResultSet res = stmt.executeQuery("SELECT * FROM journals");

                while(res.next()) {
                	int issn = res.getInt(1);
                	String title = res.getString(2);
                	String email = res.getString(3);

                }

    /**
     * Get a list of all journals
     * @return a list of journals
     * @throws SQLException
     * @throws IOException
     */
    public static LinkedList<Integer> getVolumes(int issn) throws SQLException {
        LinkedList<Integer> volumes = new LinkedList<Integer>();
        openConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement("SELECT * FROM `volume` WHERE ISSN = ?");
            pstmt.setInt(1, issn);
            ResultSet res = pstmt.executeQuery();

            while (res.next()) {
                int volNum = res.getInt("volNum");
                volumes.add(volNum);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            closeConnection();
        }
        return volumes;
    }

    public static void main (String[] args) throws IOException {
    	File pdfFile = new File("./Systems Design Project.pdf");
        try {

            //create article test
            //System.out.println(createArticle("Long and Dark11", "long and dark nights11", pdfFile, 2934554, "john.barker@dheffff11.ac.uk" ));
            //System.out.println(createSubmission(pdfFile));

            System.out.println(getVolumes(65432345));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }
}
