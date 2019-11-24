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
     * Check if ISSN has at most 8 digits
     * @param issn
     * @return result true if issn is valid, false otherwise
     */
    public static boolean isValidIssn(int issn) {
        return (issn <= 99999999);
    }
    

    /**
     * Check if ISSN exist in the database
     * @param issn
     * @return result true if email exists, false otherwise
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
     * Get a list of all journals from database
     * @return list of journals
     * @throws SQLException
     */
    public static LinkedList<Journal> getJournals() throws SQLException {
        LinkedList<Journal> journals = new LinkedList<Journal>();
        openConnection();
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            ResultSet res = stmt.executeQuery("SELECT * FROM `journal`");

            while (res.next()) {
                int issn = res.getInt("ISSN");
                String title = res.getString("title");
                String chiefEditorEmail = res.getString("chiefEditorEmail");
                Journal journal = new Journal(issn, title, chiefEditorEmail);
                journals.add(journal);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (stmt != null) stmt.close();
            closeConnection();
        }
        return journals;
    }

    /**
     * Get a list of all volumes of a given journal
     * @param issn
     * @return a list of volumes
     * @throws SQLException
     */
    public static LinkedList<Volume> getVolumes(int issn) throws SQLException {
        LinkedList<Volume> volumes = new LinkedList<Volume>();
        openConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement("SELECT * FROM `volume` WHERE ISSN = ?");
            pstmt.setInt(1, issn);
            ResultSet res = pstmt.executeQuery();

            while (res.next()) {
                int volNum = res.getInt("volNum");
                int pubYear = res.getInt("pubYear");
                int editionCount= res.getInt("ISSN");
                Volume volume = new Volume(volNum, pubYear, issn, editionCount);
                volumes.add(volume);
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

            System.out.println(getJournals());
            //create article test
            //System.out.println(createArticle("Long and Dark11", "long and dark nights11", pdfFile, 2934554, "john.barker@dheffff11.ac.uk" ));
            //System.out.println(createSubmission(pdfFile));

            System.out.println(getVolumes(65432345));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }
}
