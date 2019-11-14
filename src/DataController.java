/**
 * Class for data manipulation in MySQL database
 * @author Urszula Talalaj
 * @author Julia Derebecka
 */
import java.sql.*;

public class DataController {
    
    private static Connection con = null;
    
    /**
     * Create a connection with MySQL database
     * @return created connection
     */
    public static void openConnection() throws SQLException {
        try {
            con = DriverManager.getConnection("jdbc:mysql://stusql.dcs.shef.ac.uk/team021", "team021", "ea561329");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Close a connection with MySQL database
     * @param con connection to close
     */
    public static void closeConnection() throws SQLException {
        try {
            if (con != null) con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }    
    } 
    
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
     * Register as a new Chief Editor and add a journal
     * @param email
     * @param password
     * @param title
     * @param forename
     * @param surname
     * @param university
     * @param journal
     * @param ISSN
     * @return result true if registration is successful
     * @throws SQLException 
     */
    public static boolean chiefEditoRegistration(String email, String title, String forename,
            String surname, String university, String password, String journal, int ISSN) throws SQLException {
        boolean result = false;
        if (!checkEmail(email)) {
            if (createUser(email, title, forename, surname, university, password, 1)) result = true;
        }
        return result;
    }
 
    /**
     * Create a new user with all parameters
     * @param email
     * @param password
     * @param title
     * @param forename
     * @param surname
     * @param university
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
            System.out.println("Rows updated: " + count);
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
    
    public static void main (String[] args) {
        
        try {
            // chief editor registration test case 1 true only once - all details correct
            System.out.println(chiefEditoRegistration("james.potter@warwick.ac.uk", "Dr", "James", "Potter",
                    "University of Warwick", "test_password", "Journal of Pottery", 65432345));
            
            // chief editor login test case true - all details correct
            System.out.println(login("james.potter@warwick.ac.uk", "test_password", 1));
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
    }
}