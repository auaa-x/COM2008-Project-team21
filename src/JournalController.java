/**
 * Class for journal data manipulation in MySQL database
 * @author Urszula Talalaj
 * @author Julia Derebecka
 */
import java.sql.*;
import java.util.Calendar;
import java.util.LinkedList;
import java.io.*;

public class JournalController extends SqlController {
    
    private String currentJournal;


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
     * Create a new volume for a given journal
     * @param ISSN
     * @return result true if volume is created successfully
     * @throws SQLException
     */
    public static boolean createVolume(int issn) throws SQLException {
        boolean result = false;
        int volNum = 2;
        int pubYear = Calendar.getInstance().get(Calendar.YEAR);
        openConnection();  ;
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
        PreparedStatement pstmt3 = null;
            try {
                // check if this year's volume already exists
                pstmt1 = con.prepareStatement("SELECT * FROM `team021`.`volume` WHERE (`ISSN` = ?) and (`pubYear` = ?)");
                pstmt1.setInt(1, issn);
                pstmt1.setInt(2, pubYear);
                ResultSet res1 = pstmt1.executeQuery();
                
                // don't create new volume if one already exists this year
                if (res1.next()) return false;
                
                // find out what's the volNum of the new volume
                pstmt2 = con.prepareStatement("SELECT * FROM `team021`.`volume` WHERE (`ISSN` = ?)");
                pstmt2.setInt(1, issn);
                ResultSet res2 = pstmt2.executeQuery();
                while (res2.next()) {
                    volNum++;
                }
                
                // update database
                pstmt3 = con.prepareStatement("INSERT INTO `team021`.`volume` (`volNum`, `pubYear`, `ISSN`) VALUES (?, ?, ?)");
                pstmt3.setInt(1, volNum);
                pstmt3.setInt(2, pubYear);
                pstmt3.setInt(3, issn);

                int count = pstmt3.executeUpdate();
                if (count != 0) result = true;
                System.out.println("Volume " + volNum + " " + issn + " added");

            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                if (pstmt1 != null) pstmt1.close();
                if (pstmt2 != null) pstmt2.close();
                if (pstmt3 != null) pstmt3.close();
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
    public static LinkedList<Journal> getAllJournals() throws SQLException {
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
    
  //put into JournalController after done
    /**
     * Get a list of all articles from database
     * @return list of articles
     * @throws SQLException
     */
    public static LinkedList<Article> getAllArticles() throws SQLException {
        LinkedList<Article> articles = new LinkedList<Article>();
        openConnection();
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            ResultSet res = stmt.executeQuery("SELECT * FROM `article`");

            while (res.next()) {
            	int submissionID = res.getInt("submissionID"); 
                String title = res.getString("title");
                String artAbstract = res.getString("abstract");
                //without linkedFinalPDF
                boolean isPublished = res.getBoolean("isPublished");
                int issn = res.getInt("ISSN");
                String mAuthorEmail = res.getString("mAuthorEmail");
                Article article = new Article(submissionID, title, artAbstract, isPublished, issn, mAuthorEmail);
                articles.add(article);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (stmt != null) stmt.close();
            closeConnection();
        }
        return articles;
    }

    /**
     * Get a list of all editions from database
     * @return list of editions
     * @throws SQLException
     */
    public static LinkedList<Edition> getEditions() throws SQLException {
        LinkedList<Edition> editions = new LinkedList<Edition>();
        openConnection();
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            ResultSet res = stmt.executeQuery("SELECT * FROM `edition`");

            while (res.next()) {
            	int noNum = res.getInt("noNum");
                int pubMonth = res.getInt("pubMonth");
                int volNum = res.getInt("volNum");
                int artCount = res.getInt("artCount");
            	
                Edition edition = new Edition(noNum, pubMonth, volNum, artCount);
                editions.add(edition);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (stmt != null) stmt.close();
            closeConnection();
        }
        return editions;
    }
    
    /**
     * Get a list of a given editor journals
     * @return list of journals
     * @throws SQLException
     */
    public static LinkedList<Integer> getEditorsJournals(String email) throws SQLException {
        LinkedList<Integer> journals = new LinkedList<Integer>();
        openConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement("SELECT * FROM `editor` WHERE email = ?");
            pstmt.setString(1, email);
            ResultSet res = pstmt.executeQuery();

            while (res.next()) {
                int issn = res.getInt("ISSN");
                System.out.println(issn);
                journals.add(issn);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
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
    
    
    /**
     * Get a list of all editors' emails of a given journal
     * @param issn
     * @return a list of editors' emails
     * @throws SQLException
     */
    public static LinkedList<String> getEditors(int issn) throws SQLException {
        LinkedList<String> editors = new LinkedList<String>();
        openConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement("SELECT * FROM `editor` WHERE ISSN = ?");
            pstmt.setInt(1, issn);
            ResultSet res = pstmt.executeQuery();

            while (res.next()) {
                String email = res.getString("email");
                editors.add(email);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            closeConnection();
        }
        return editors;
    }
    
    
    /**
     * Allows a chief editor of a given journal to retire if possible
     * and automatically appoints the new chief editor
     * @param issn
     * @return true if retiring successful and new chief editor appointed, false otherwise
     * @throws SQLException
     */
    public static boolean chiefEditorRetire(String email, int issn) throws SQLException {
        boolean result = false;
        String oldChiefEmail = email;
        String newChiefEmail = null;
        
        // get all editors of a given journal
        LinkedList<String> editors = getEditors(issn);
        
        openConnection();
        PreparedStatement pstmt = null;
        try {
            // continue only if 2 or more editors are on board now
            if (editors.size() <= 2) {
                // remove the old chief editor from the list
                editors.remove(oldChiefEmail);
                // find a new editor
                newChiefEmail = editors.getFirst();
                // appoint the new chief editor
                pstmt = con.prepareStatement("UPDATE `team021`.`journal` SET `chiefEditorEmail` = ? WHERE (`ISSN` = ?)");
                pstmt.setString(1, newChiefEmail);
                pstmt.setInt(2, issn);
                int count = pstmt.executeUpdate();
                if (count != 0) {
                    result = true;
                    System.out.println("New chief editor " + newChiefEmail + " appointed");
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            closeConnection();
        }
        // retire the old chief editor
        if (result) {
            editorRetire(oldChiefEmail, issn);
            System.out.println("Old chief editor " + oldChiefEmail + " retired");
        }
        return result;
    }

    
    /**
     * Allows an editor of a given journal to retire if possible
     * @param email
     * @param issn
     * @return true if retiring successful, false otherwise
     * @throws SQLException
     */
    public static boolean editorRetire(String email, int issn) throws SQLException {
        boolean result = false;
        openConnection();
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
        try {
            // get all others editors of the journal
            pstmt1 = con.prepareStatement("SELECT * FROM `editor` WHERE `ISSN` = ?");
            pstmt1.setInt(1, issn);
            ResultSet res = pstmt1.executeQuery();
            
            // ensure at least two editors are in the board now
            if (res.next()) {
                if (res.next()) {
                    pstmt2 = con.prepareStatement("DELETE FROM `team021`.`editor` WHERE (`email` = ?) and (`ISSN` = ?)");
                    pstmt2.setString(1, email);
                    pstmt2.setInt(2, issn);
                    int count = pstmt2.executeUpdate();
                    if (count != 0) result = true;
                    System.out.println("Editor " + email + " deleted");
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt1 != null) pstmt1.close();
            if (pstmt2 != null) pstmt2.close();
            closeConnection();
        }
        return result;
        
    }


    public static void main (String[] args) throws IOException {
    	//File pdfFile = new File("./Systems Design Project.pdf");
        try {

            System.out.println(getAllJournals());
            //create article test
            //System.out.println(createArticle("Long and Dark11", "long and dark nights11", pdfFile, 2934554, "john.barker@dheffff11.ac.uk" ));
            //System.out.println(createSubmission(pdfFile));

            System.out.println(getVolumes(65432345));
            
            //UserController.createEditor("neweidtorr", 65432345);
            //chiefEditorRetire("james.potter@warwick.ac.uk", 65432345);
            
            getEditorsJournals("neweidtorr");
            
           // System.out.println("Create volume test:");
           // System.out.println(createVolume(87645312)); // false
           // System.out.println(createVolume(65432345)); // true
            System.out.println(getAllArticles());
            System.out.println(getEditions());
            
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }
}
