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
            pstmt = con.prepareStatement("SELECT * FROM user WHERE email=?;");
            pstmt.setString(1, email);
            ResultSet res = pstmt.executeQuery();
            
            result = res.next();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            pstmt.close();
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
            pstmt = con.prepareStatement("SELECT * FROM user WHERE email=?;");
            pstmt.setString(1, email);
            ResultSet res = pstmt.executeQuery();
            
            String truePassword = "";
            if(res.next()) {
                truePassword = res.getString("password");
                //System.out.println(truePassword);
            }
            
            if (truePassword.equals(password)) result = true;
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            pstmt.close();
            closeConnection();         
        }
     return result;
    }
    
    /**
     * Check if login details are correct
     * @param email
     * @param password
     * @return result true if login details are correct
     */
    public static boolean login(String email, String password) throws SQLException {
        if (checkEmail(email)) {
            //System.out.println("Email exists");
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
    
    public static void main (String[] args) {
        
        try {
            // test case 1 true - correct email and password
            System.out.println(login("john.smith@manchester.ac.uk", "12345"));
            
            // test case 2 false - incorrect email and password
            System.out.println(login("john.smithdsfdg@manchester.ac.uk", "12345"));
            
            // test case 1 false - correct email and password
            System.out.println(login("kate.bush@edinburgh.ac.uk", "1234567"));
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
    }
}