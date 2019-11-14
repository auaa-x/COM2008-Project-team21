/**
 * Class for data manipulation in MySQL database
 * @author Urszula Talalaj
 * @author Julia Derebecka
 */
import java.sql.*;

public class SqlController {
	
	 protected static Connection con = null;
	    
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

}
