/**
 * Class for user data manipulation in MySQL database
 * @author Urszula Talalaj
 * @author Julia Derebecka
 */
import java.sql.*;

public class UserController extends SqlController {

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
     * Check if login details are correct
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
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
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
        openConnection();
        boolean result = false;
        PreparedStatement pstmt = null;
        PreparedStatement hash = null;
        try {
            String hashed_password = hashPassword(password);
            pstmt = con.prepareStatement("INSERT INTO `team021`.`user` (`email`, `title`, `forename`,"
                    + " `surname`, `uniAffiliation`, `password`, `isTemporary`, `isEditor`, `isAuthor`, `isReviewer`)"
                    + " VALUES (?, ?, ?, ?, ?, ?, '0', ?, ?, 0)");
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
            System.out.println("Rows in user updated: " + count);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            if (hash != null) pstmt.close();
            closeConnection();
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
    public static boolean addRole(String email, int usertype) throws SQLException{
        openConnection();
        boolean result = false;
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
            System.out.println("Rows in user updated: " + count);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            closeConnection();
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
     * @param ISSN
     * @return result true if registration is successful
     * @throws SQLException
     */
    public static boolean chiefEditorRegistration(String email, String title, String forename,
            String surname, String university, String password, String journal, int ISSN) throws SQLException {
        boolean result = false;
        if (!checkEmail(email)) {
            if (createUser(email, title, forename, surname, university, password, 1) && createJournal(email, journal, ISSN)) result = true;
        }
        return result;
    }


    /**
     * Create a new journal with all parameters
     * @param email
     * @param journal
     * @param ISSN
     * @return result true if journal is created successfully
     * @throws SQLException
     */
    public static boolean createJournal(String email, String journal, int ISSN) throws SQLException {
        openConnection();
        boolean result = false;
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement(" INSERT INTO `team021`.`journal` (`ISSN`, `title`, `chiefEditorEmail`)"
            		+ " VALUES (?, ?, ?)");
            pstmt.setInt(1, ISSN);
            pstmt.setString(2, journal);
            pstmt.setString(3, email);

            int count = pstmt.executeUpdate();
            if (count != 0) result = true;
            System.out.println("Rows in journal updated: " + count);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) pstmt.close();
            closeConnection();
        }
        return result;
    }
    
    
    /**
     * Register as a new author and add an article
     * @param email
     * @param title
     * @param forename
     * @param surname
     * @param university
     * @param password
     * @return result true if registration is successful
     * @throws SQLException
     */
    public static boolean mainAuthorRegistration(String email, String title, String forename,
            String surname, String university, String password) throws SQLException {
        boolean result = false;
        if (!checkEmail(email)) {
            if (createUser(email, title, forename, surname, university, password, 2) && createArticle() && addRole(3)) result = true;
        }
        return result;
    }




    public static void main (String[] args) {

        try {

            //chief editor registration test case 1 true - all details correct
            System.out.println(chiefEditorRegistration("james.potter@warwick.ac.uk", "Dr", "James", "Potter",
                    "University of Warwick", "test_password", "Journal of Pottery", 65432345));
            
            //test create journal
            //System.out.println(createJournal("kate.bush@edinburgh.ac.uk", "Foundations of COmpSci", 2344));

            // chief editor login test case true - all details correct
            System.out.println(login("james.potter@warwick.ac.uk", "test_password", 1));
            
            System.out.println(chiefEditorRegistration("harry.potter@hogwarts.ac.uk", "Professor", "Harry", "Potter",
                    "University of Hogwarts", "gryffindor", "Journal of Wizardry", 56214784));
            
            // chief editor login test case true - all details correct
            System.out.println(login("harry.potter@hogwarts.ac.uk", "gryffindor", 1));
            
            // add reviewer role for user test case true
            System.out.println(addRole("harry.potter@hogwarts.ac.uk", 3));

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }
}
