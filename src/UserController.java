/**
 * Class for user data manipulation in MySQL database
 * @author Urszula Talalaj
 * @author Julia Derebecka
 */
import java.io.*;
import java.sql.*;
import java.util.LinkedList;
import java.util.ListIterator;

public class UserController extends SqlController {
    
    private static int loggedUserType;
    private static String loggedUserEmail;
    private static LinkedList<String> coAuthorsList = new LinkedList<String>();

    /**
     * Check if email exist in the database
     * @param email
     * @return result true if email exists, otherwise false
     */
    public static boolean checkEmail(String email) throws SQLException {
        openConnection();
        boolean result = false;
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement("SELECT * FROM user WHERE email=?");
            pstmt.setString(1, email);
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
     * Check if password is correct
     * @param email
     * @param password
     * @return result true if password is correct
     */
    public static boolean checkPassword(String email, String password) throws SQLException {
        openConnection();
        boolean result = false;
        PreparedStatement pstmt = null;
        try {
            String hashedPassword = hashPassword(password);

            pstmt = con.prepareStatement("SELECT * FROM user WHERE email=?");
            pstmt.setString(1, email);
            ResultSet res = pstmt.executeQuery();

            String trueHashedPassword = "";
            if(res.next()) {
                trueHashedPassword = res.getString("password");
                //System.out.println(truePassword);
            }

            if (trueHashedPassword.equals(hashedPassword)) result = true;

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            closeConnection();
        }
        return result;
    }


    /**
     * Hash a password using SHA2, can only be used when connection is open
     * @param password
     * @return hashed password
     */
    private static String hashPassword(String password) {
        PreparedStatement hash = null;
        String hashedPassword = "";
        try {
            hash = con.prepareStatement("SELECT SHA2(?, 256)");
            hash.setString(1, password);
            ResultSet hashRes = hash.executeQuery();
            if (hashRes.next()) {
                hashedPassword = hashRes.getString(1);
                //System.out.println(hashedPassword);
            }
        } catch (SQLException ex) {
                ex.printStackTrace();
        }
        return hashedPassword;
    }


    /**
     * Check if user is permitted to login as a chosen user type
     * @param email
     * @param usertype (1 - editor, 2 - author, 3 - reviewer)
     * @return result true if user is authorised
     */
    public static boolean checkUsertype(String email, int usertype) throws SQLException {
        openConnection();
        boolean result = false;
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement("SELECT * FROM user WHERE email=?");
            pstmt.setString(1, email);
            ResultSet res = pstmt.executeQuery();

            if(res.next()) {
                switch(usertype) {
                case 1:
                    if (res.getBoolean("isEditor")) result = true;
                    break;
                case 2:
                    if (res.getBoolean("isAuthor")) result = true;
                    break;
                case 3:
                    if (res.getBoolean("isReviewer")) result = true;
                    break;
                }
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
     * Check if login details are correct and login the user
     * @param email
     * @param password
     * @param usertype (1 - editor, 2 - author, 3 - reviewer)
     * @return result true if login details are correct
     */
    public static boolean login(String email, String password, int usertype) throws SQLException {
        if (checkEmail(email) && checkUsertype(email, usertype)) {
            //System.out.println("Email exists");
            //System.out.println("User authorised");
            if(checkPassword(email, password)) {
                //System.out.println("Password correct");
                loggedUserType = usertype;
                loggedUserEmail = email;
                System.out.println("User: " + email + " logged in");
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
    
    /**
     * Get logged user type
     * @return user type (1 - editor, 2 - author, 3 - reviewer)
     */
    public static int getLoggedUserType() {
        return loggedUserType;
    }
    
    
    /**
     * Get logged user email
     * @return email
     */
    public static String getLoggedUserEmail() {
        return loggedUserEmail;
    }


    /**
     * Create a new user with all parameters
     * @param email
     * @param title
     * @param forename
     * @param surname
     * @param university
     * @param password
     * @param usertype
     * @return result true if registration is successful
     * @throws SQLException
     */
    public static boolean createUser(String email, String title, String forename,
            String surname, String university, String password, int usertype) throws SQLException {
        boolean result = false;
        if (!checkEmail(email)) {
            openConnection();
            PreparedStatement pstmt = null;
            PreparedStatement hash = null;
            try {
                String hashed_password = hashPassword(password);
                pstmt = con.prepareStatement("INSERT INTO `team021`.`user` (`email`, `title`, `forename`,"
                        + " `surname`, `uniAffiliation`, `password`, `isEditor`, `isAuthor`, `isReviewer`)"
                        + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, 0)");
                pstmt.setString(1, email);
                pstmt.setString(2, title);
                pstmt.setString(3, forename);
                pstmt.setString(4, surname);
                pstmt.setString(5, university);
                pstmt.setString(6, hashed_password);

                // only editor or author can be created
                switch(usertype) {
                case 1:
                    pstmt.setInt(7, 1);
                    pstmt.setInt(8, 0);
                    break;
                case 2:
                    pstmt.setInt(7, 0);
                    pstmt.setInt(8, 1);
                    break;
                }

                int count = pstmt.executeUpdate();
                if (count != 0) result = true;
                System.out.println("User created: " + email);
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                if (pstmt != null) pstmt.close();
                if (hash != null) pstmt.close();
                closeConnection();
            }
        }      
        return result;
    }
    

    /**
     * Create a user with temporary password
     * @param email
     * @param password
     * @param usertype
     * @return result true if registration is successful
     * @throws SQLException
     */
    public static boolean createTempUser(String email, String password, int usertype) throws SQLException {
        boolean result = false;
        if (!checkEmail(email)) {
            openConnection();
            PreparedStatement pstmt = null;
            PreparedStatement hash = null;
            try {
                switch(usertype) {
                case 1:
                    pstmt = con.prepareStatement("INSERT INTO `team021`.`user` (`email`, `password`, `isEditor`)"
                            + " VALUES (?, ?, 1)");
                    break;
                case 2:
                    pstmt = con.prepareStatement("INSERT INTO `team021`.`user` (`email`, `password`, `isAuthor`)"
                            + " VALUES (?, ?, 1)");
                    break;
                }
                
                String hashed_password = hashPassword(password);
                pstmt.setString(1, email);
                pstmt.setString(2, hashed_password);

                int count = pstmt.executeUpdate();
                if (count != 0) result = true;
                System.out.println("User with temporary password created: " + email);
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                if (pstmt != null) pstmt.close();
                if (hash != null) pstmt.close();
                closeConnection();
            }
        }
        return result;
    }
    
    
    /**
     * Add a role for a user
     * @param email
     * @param usertype
     * @return result true if role addition is successful
     * @throws SQLException
     */
    public static boolean addRole(String email, int usertype) throws SQLException {
        boolean result = false;
        if (checkEmail(email)) {
            openConnection();
            PreparedStatement pstmt = null;
            try {

                switch(usertype) {
                case 1:
                    pstmt = con.prepareStatement("UPDATE `team021`.`user` SET `isEditor` = '1' WHERE (`email` = ?)");
                    break;
                case 2:
                    pstmt = con.prepareStatement("UPDATE `team021`.`user` SET `isAuthor` = '1' WHERE (`email` = ?)");
                    break;
                case 3:
                    pstmt = con.prepareStatement("UPDATE `team021`.`user` SET `isReviewer` = '1' WHERE (`email` = ?)");
                    break;
                }
                pstmt.setString(1, email);
                int count = pstmt.executeUpdate();
                if (count != 0) result = true;
                System.out.println("User role " + usertype +  " for user " + email + " added");
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
     * Register as a new chief editor and add a journal
     * @param email
     * @param title
     * @param forename
     * @param surname
     * @param university
     * @param password
     * @param journal
     * @param issn
     * @return result true if registration is successful
     * @throws SQLException
     */
    public static boolean chiefEditorRegistration(String email, String title, String forename,
            String surname, String university, String password, String journal, int issn) throws SQLException {
        boolean result = false;
        // create chief editor account and add his journal
        if (!checkEmail(email) && !JournalController.checkIssn(issn)) {
            if (createUser(email, title, forename, surname, university, password, 1)
                    && JournalController.createJournal(email, journal, issn) && createEditor(email, issn)) {
                result = true;
                System.out.println("Chief Editor " + email + " added and Journal " + journal + " created");
            }
        }
        return result;
    }
    
    
    /**
     * Create an editor
     * @param email
     * @param issn
     * @return result true if registration is successful
     * @throws SQLException
     */
    public static boolean createEditor(String email, int issn) throws SQLException {
        boolean result = false;
        openConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement("INSERT INTO `team021`.`editor` (`email`, `issn`) VALUES (?, ?)");
            pstmt.setString(1, email);
            pstmt.setInt(2, issn);

            int count = pstmt.executeUpdate();
            if (count != 0) result = true;
            System.out.println("Editor created: " + email);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            closeConnection();
        }
        return result;
    }


    /**
     * Register a new author and add an article
     * @param email
     * @param title
     * @param forename
     * @param surname
     * @param university
     * @param password
     * @return true if registration is successful, false otherwise
     * @throws SQLException
     */
    public static boolean mainAuthorRegistration(String email, String title, String forename,
            String surname, String university, String password, String sharedPassword, String articleTitle, String description,
            File pdfFile, int ISSN) throws SQLException, FileNotFoundException {
        boolean result = false;
        // STILL NEED TO CREATE TABLE ENTRIES FOR AUTHOR AND REVIEWER !!!!!!!!!!!!
        if (createUser(email, title, forename, surname, university, password, 2) && addRole(email, 3)) {
            result = true;
            int submissionID = JournalController.createArticle(articleTitle, description, pdfFile, ISSN, email);
            addCoAuthors(sharedPassword, submissionID);
        }
        return result;
    }
    

    /**
     * Create an author
     * @param email
     * @param submissionID
     * @return true if registration is successful
     * @throws SQLException
     */
    public static boolean createAuthor(String email, int submissionID) throws SQLException {
        boolean result = false;
        openConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement("INSERT INTO `team021`.`author` (`email`, `submissionID`) VALUES (?, ?)");
            pstmt.setString(1, email);
            pstmt.setInt(2, submissionID);

            int count = pstmt.executeUpdate();
            if (count != 0) result = true;
            System.out.println("Author created: " + email);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            closeConnection();
        }
        return result;
    }
    
    
    /**
     * Create a reviewer
     * @param anonID
     * @param email
     * @return true if registration is successful
     * @throws SQLException
     */
    public static boolean createReviewer(String anonID, String email) throws SQLException {
        boolean result = false;
        openConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement("INSERT INTO `team021`.`reviewer` (`anonID`, `email`) VALUES (?, ?)");
            pstmt.setString(1, anonID);
            pstmt.setString(2, email);

            int count = pstmt.executeUpdate();
            if (count != 0) result = true;
            System.out.println("Reviewer created: " + email);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            closeConnection();
        }
        return result;
    }
    
    
    /**
     * Register co-authors of the article with temporary password
     * @param sharedPassword
     * @param submissionID
     * @return true if registration of all authors is successful, false otherwise
     * @throws SQLException
     */
    public static boolean addCoAuthors(String sharedPassword, int submissionID) throws SQLException {
        boolean result = false;
        ListIterator<String> iterator = coAuthorsList.listIterator();
        // create user account for each co-author on the list
        while(iterator.hasNext()) {
            String email = iterator.next().toString();
            if(!createTempUser(email, sharedPassword, 2) || !addRole(email, 3)) {
                return false; // return false if user can't be created
            }
            result = true;
        }
        return result;
    }
 
    /**
     * Add co-author email to the co-authors' list
     * @param email
     */
    public static void addCoAuthor(String email) {
        coAuthorsList.add(email);
    }
    
    /**
     * Change password for one user
     * @param email
     * @param oldPassword
     * @param newPassword
     * @param newPasswordConf
     * @return true if password was changed successfully, false otherwise
     * @throws SQLException
     */
    public static boolean changePassword(String email, String oldPassword, String newPassword, String newPasswordConf) throws SQLException {
        boolean result = false;
        if (login(email, oldPassword, getLoggedUserType()) && newPassword.equals(newPasswordConf)) {
            openConnection();
            PreparedStatement pstmt = null;
            try {
                String hashed_password = hashPassword(newPassword);
                pstmt = con.prepareStatement("UPDATE `team021`.`user` SET `password` = ? WHERE (`email` = ?)");
                pstmt.setString(1, hashed_password);
                pstmt.setString(2, email);

                int count = pstmt.executeUpdate();
                if (count != 0) result = true;
                System.out.println("Password changed for user: " + email);
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                if (pstmt != null) pstmt.close();
                closeConnection();
            }
        }
        return result;
    }


    public static void main (String[] args) {

        try {

            // chief editor REGISTRATION test case true - all details correct
            chiefEditorRegistration("james.potter@warwick.ac.uk", "Dr", "James", "Potter",
                    "University of Warwick", "test_password", "Journal of Pottery", 65432345);
            
            // chief editor LOGIN test case true - all details correct
           login("james.potter@warwick.ac.uk", "test_password", 1);
            
            // test create journal
            JournalController.createJournal("kate.bush@edinburgh.ac.uk", "Foundations of CompSci", 85491254);

            // chief editor REGISTRATION test case false - journal with this ISSN already exists
            chiefEditorRegistration("harry.potter@hogwarts.ac.uk", "Professor", "Harry", "Potter",
                    "University of Hogwarts", "gryffindor", "Journal of Wizardry", 65432345);
            
            // add co-authors to the list
            addCoAuthor("luna.glovegood@hogwarts.ac.uk");
            addCoAuthor("cedric.diggory@hogwarts.ac.uk");
            
            // add them as users
            System.out.println(addCoAuthors("hufflepuff", 123));


        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }
}