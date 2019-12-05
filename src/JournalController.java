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
    
    private static int currentJournal;
    protected static LinkedList<Integer> articlesForEditor = new LinkedList<Integer>();
    
    /**
     * Get list of submission ids of articles to publish
     * @return list of submissionIds 
     */
    public static LinkedList<Integer> getArticleList() {
        return articlesForEditor;
    }

    
    /**
     * Set currently viewed journal
     * @param ISSN
     */
    public static void setCurrentJournal(int issn) {
        currentJournal = issn;
    }
    
    
    /**
     * Get currently viewed journal
     * @param ISSN
     */
    public static int getCurrentJournal(int issn) {
        return currentJournal;
    }

    
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
     * @return result true if issn exists, false otherwise
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
        int volNum = 0;
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
                pstmt2 = con.prepareStatement("SELECT COUNT(*) FROM `team021`.`volume` WHERE (`ISSN` = ?)");
                pstmt2.setInt(1, issn);
                ResultSet res2 = pstmt2.executeQuery();
                if (res2.next()) {
                    volNum = res2.getInt(1) + 1;
                }
                
                // update database
                pstmt3 = con.prepareStatement("INSERT INTO `team021`.`volume` (`volNum`, `pubYear`, `ISSN`) VALUES (?, ?, ?)");
                pstmt3.setInt(1, volNum);
                pstmt3.setInt(2, pubYear);
                pstmt3.setInt(3, issn);

                int count = pstmt3.executeUpdate();
                if (count != 0) {
                    result = true;
                    System.out.println("Volume " + volNum + " " + issn + " added");
                }

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
     * Create a new edition for a given journal volume
     * @param ISSN
     * @param volNum
     * @return result true if edition is created successfully
     * @throws SQLException
     */
    public static boolean createEdition(int issn, int volNum) throws SQLException {
        boolean result = false;
        int noNum = 0;
        openConnection();  ;
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
            try {

                // find what's the noNum of the new edition
                pstmt1 = con.prepareStatement("SELECT COUNT(*) FROM `team021`.`edition` WHERE (`ISSN` = ?) and (`volNum` = ?)");
                pstmt1.setInt(1, issn);
                pstmt1.setInt(2, volNum);
                ResultSet res = pstmt1.executeQuery();
                if (res.next()) {
                    noNum = res.getInt(1) + 1;
                }
                
                // update database
                pstmt2 = con.prepareStatement("INSERT INTO `team021`.`edition` (`noNum`, `volNum`, `ISSN`) VALUES (?, ?, ?)");
                pstmt2.setInt(1, noNum);
                pstmt2.setInt(2, volNum);
                pstmt2.setInt(3, issn);

                int count = pstmt2.executeUpdate();
                if (count != 0) {
                    result = true;
                    System.out.println("Edition number: " + noNum + " volume: " + volNum + " journal: " + issn + " added");
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
    
    
    /**
     * Publish the next edition of a given journal
     * @param issn
     * @param volNum
     * @param noNum
     * @return result true if edition is published successfully
     * @throws SQLException
     */
    public static boolean publishNextEdition(int issn, int volNum, int noNum) throws SQLException {
        boolean result = false;
        int artCount = getArtCount(issn, volNum, noNum);
        
        // check if the number of articles in edition is allowed
        if (artCount >= 3 && artCount <= 8) {
            openConnection();
            PreparedStatement pstmt = null;
                try {
                    int pubMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
                    System.out.println(pubMonth);
                    // update database 
                    pstmt = con.prepareStatement("UPDATE `team021`.`edition` SET `pubMonth` = ?, `isPublished` = '1' WHERE (`ISSN` = ?) and (`volNum` = ?) and (`noNum` = ?)");
                    pstmt.setInt(1, pubMonth);
                    pstmt.setInt(2, issn);
                    pstmt.setInt(3, volNum);
                    pstmt.setInt(4, noNum);

                    int count = pstmt.executeUpdate();
                    if (count != 0) {
                        result = true;
                        System.out.println("Edition number: " + noNum + ", volume: " + volNum + ", journal: " + issn + " published");
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
     * Get article counter of a given edition of a journal
     * @param issn
     * @param volNum
     * @param noNum
     * @return number of articles in the edition
     * @throws SQLException
     */
    public static int getArtCount(int issn, int volNum, int noNum) throws SQLException {
        int artCount = 0;
        openConnection();  ;
        PreparedStatement pstmt = null;
            try {

                pstmt = con.prepareStatement("SELECT * FROM `team021`.`edition` WHERE (`ISSN` = ?) and (`volNum` = ?) and (`noNum` = ?)");
                pstmt.setInt(1, issn);
                pstmt.setInt(2, volNum);
                pstmt.setInt(3, noNum);
                ResultSet res = pstmt.executeQuery();
                if (res.next()) {
                    artCount = res.getInt("artCount");
                }
                
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                if (pstmt != null) pstmt.close();
                closeConnection();
            }
        return artCount;
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
     * Get a journal by issn
     * @param issn
     * @return a journal object
     * @throws SQLException
     */
    public static Journal getJournal(int issn) throws SQLException {
        openConnection();
        PreparedStatement pstmt = null;
        Journal journal = null;
        try {
            pstmt = con.prepareStatement("SELECT * FROM `journal` WHERE issn = ?");
            pstmt.setInt(1, issn);
            ResultSet res = pstmt.executeQuery();      
                       
            if (res.next()) {
                String title = res.getString("title"); 
                String chief = res.getString("chiefEditorEmail");
                journal = new Journal(issn, title, chief);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            closeConnection();
        }
        return journal;
    }
    
    /**
     * Get journal by article's submissionID
     * @param submissionId
     * @return journal 
     * @throws SQLException
     * @throws IOException
     */
    public static Journal getJournalByArticle(int submissionId) throws SQLException, IOException {
        openConnection();
        PreparedStatement pstmt = null;
        int issn = 0;
        Journal journal = null;
        try {
            
            pstmt = con.prepareStatement("SELECT * FROM article WHERE (submissionID = ?) ");
            pstmt.setInt(1, submissionId); 
            ResultSet res = pstmt.executeQuery();
            
            while (res.next()) {
                issn = res.getInt("ISSN");
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            closeConnection();
        }
        journal = getJournal(issn);
        return journal;
    }

    
    /**
     * Get a list of all editions by issn and volume number
     * @param issn
     * @param volNum
     * @return list of editions
     * @throws SQLException
     */
    public static LinkedList<Edition> getEditions(int issn, int volNum) throws SQLException {
        LinkedList<Edition> editions = new LinkedList<Edition>();
        openConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement("SELECT * FROM `edition` WHERE (`ISSN` = ?) and (`volNum` = ?)");
            pstmt.setInt(1, issn);
            pstmt.setInt(2, volNum);
            ResultSet res = pstmt.executeQuery();
            

            while (res.next()) {
            	int noNum = res.getInt("noNum");
                int pubMonth = res.getInt("pubMonth");
                int artCount = res.getInt("artCount");
                int published = res.getInt("isPublished");
                boolean isPublished = false;
                if (published == 1) isPublished = true;
            	
                Edition edition = new Edition(issn, volNum, noNum, pubMonth, artCount, isPublished);
                editions.add(edition);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            closeConnection();
        }
        return editions;
    }
    
    
    /**
     * Get all published articles by journal, volume and edition
     * @param issn
     * @param volNum
     * @param noNum
     * @return list of editions
     * @throws SQLException
     */
    public static LinkedList<Article> getPublishedArticles(int issn, int volNum, int noNum) throws SQLException {
        LinkedList<Article> articles = new LinkedList<Article>();
        openConnection();
        PreparedStatement pstmt = null;
        try {
            
            pstmt = con.prepareStatement("SELECT * FROM article a, published_article p WHERE (a.submissionID = p.submissionID) "
                    + "and (a.ISSN = ?) and (a.isPublished = 1) and (p.volNum = ?) and (p.noNum = ?)");
            pstmt.setInt(1, issn);
            pstmt.setInt(2, volNum);
            pstmt.setInt(3, noNum); 
            ResultSet res = pstmt.executeQuery();
            
            while (res.next()) {
                int submissionID = res.getInt("submissionID");
                String title = res.getString("title");
                String artAbstract = res.getString("abstract");
                boolean isPublished = res.getBoolean("isPublished");
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
     * Get a list of journals issns of a given editor
     * @param email
     * @return list of journals
     * @throws SQLException
     */
    public static LinkedList<Integer> getEditorJournals(String email) throws SQLException {
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
            if (editors.size() >= 2) {
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
    
    
    /**
     * Checks if there exists a conflict between an editor and a submission
     * @param email - editor's email
     * @param submissionId
     * @return true if there is a conflict, false otherwise
     * @throws SQLException
     */
    public static boolean checkEditorConflict(String editorEmail, int submissionId) throws SQLException {
        boolean result = true;
        LinkedList<String> authors = ArticleController.getAuthors(submissionId);
        LinkedList<String> authorsUnis = new LinkedList<String>();
        openConnection();
        PreparedStatement pstmt = null;
        try {
            
            // get uni affiliation of the editor
            pstmt = con.prepareStatement("SELECT * FROM `user` WHERE `email` = ?");
            pstmt.setString(1, editorEmail);
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
            
            // if no return up to this point then there is no conflict
            result = false;
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            closeConnection();
        }
        return result;
    }
    
    
    public static LinkedList<Verdict> getFinalVerdicts(int submissionID) throws SQLException {
        LinkedList<Verdict> verdicts = new LinkedList<Verdict>();
        openConnection();
        PreparedStatement pstmt = null;
        try {
            // get all others editors of the journal
            pstmt = con.prepareStatement("SELECT * FROM `verdict` WHERE `submissionID` = ?");
            pstmt.setInt(1, submissionID);
            ResultSet res = pstmt.executeQuery();
            
            while(res.next()) {
                Verdict verdict = Verdict.valueOf(res.getString("value"));
                verdicts.add(verdict);
            }
            

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            closeConnection();
        }
        return verdicts;
        
    }
    
    /**
     * Get a list of all articles by status and journal
     * @param status
     * @param issn
     * @return list of articles
     * @throws SQLException
     */
    public static LinkedList<Article> getArtByStatusAndJournal(Status status, int issn) throws SQLException {
        LinkedList<Article> articles = new LinkedList<Article>();
        openConnection();
        PreparedStatement pstmt = null;
        try {
        	pstmt = con.prepareStatement("SELECT * FROM article a, submission s WHERE (a.ISSN =?) and (s.submissionID = a.submissionID) and (s.status = ?) ");
            pstmt.setInt(1, issn);
            pstmt.setString(2, status.name());
            ResultSet res = pstmt.executeQuery();
            while (res.next()) {
            	int submissionId = res.getInt("submissionID");
            	String title = res.getString("title");
                String artAbstract = res.getString("abstract");
                boolean isPublished = res.getBoolean("isPublished");
                String mAuthorEmail = res.getString("mAuthorEmail");
                int costCovered = res.getInt("costCovered");
                if (costCovered >= 3) {
	                Article article = new Article(submissionId, title, artAbstract, isPublished, issn, mAuthorEmail);
	                articles.add(article);
                }
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
     * Add article to articles to publish list
     * @param answer
     */
    public static void addToToPublish(int submissionId) {
    	 articlesForEditor.add(Integer.valueOf(submissionId));
    }
    
    /**
     * Publish articles from list to publish
     * @param submissionId
     * @return list of articles
     * @throws SQLException
     */
    public static boolean publishArticles(int submissionId) throws SQLException {
    	boolean result = false;
        openConnection();
        PreparedStatement pstmt = null;
        try {
        	pstmt = con.prepareStatement("SELECT * FROM article WHERE submissionID = ? ");
            pstmt.setInt(1, submissionId);
            ResultSet res = pstmt.executeQuery();
            if (res.next()) {
            	String title = res.getString("title");
                String artAbstract = res.getString("abstract");
                boolean isPublished = res.getBoolean("isPublished");
                int issn = res.getInt("ISSN");
                String mAuthorEmail = res.getString("mAuthorEmail");
                Article article = new Article(submissionId, title, artAbstract, isPublished, issn, mAuthorEmail);
                articlesForEditor.add(article);
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
    	//File pdfFile = new File("./Systems Design Project.pdf");
        try {
            //System.out.println(getJournalByArticle(1));
            System.out.println(getArtByStatusAndJournal(Status.FINAL_VERDICTS_RECEIVED,77777777));
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }
}
