/**
 * Class for Change Password Interface
 * @author Ting Guo
 * @author Huiqiang Liu
 */
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.sql.SQLException;


public class ChangePw extends JFrame implements ActionListener {

    public static void main (String[] args) {
        //launching code goes in here
        new ChangePw("james.potter@warwick.ac.uk");
    }



    // Needed for serialisation
	private String username;
    private static final long serialVersionUID = 1L;
    private JPasswordField oldPwField;
    private JPasswordField newPwField;
    private JPasswordField cfPwField;


    // Constructor with frame title
    public ChangePw(String username) {
    	//pass the title name
        super("Change Password");  
        //pass the username 
        this.username = username;
        this.setResizable(false);

        //access useful window system constants
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();

        //centre window
        setSize(screenSize.width/2, screenSize.height/2);
        setLocation(screenSize.width/4, screenSize.height/4);

        //container pane
        Container contentPane = getContentPane();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(450, 190, 1014, 597);
        setResizable(false);
        contentPane = new JPanel();
        contentPane.setBounds(5, 5, 5, 5);
        setContentPane(contentPane);
        contentPane.setLayout(null);


        //Welcome banner
        JLabel lblNewLabel = new JLabel("Change Password");
        lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 46));
        lblNewLabel.setBounds(325, 55, 540, 90);
        contentPane.add(lblNewLabel);


        //Old password Field
        JLabel lblOldPw = new JLabel("Old Password");
        lblOldPw.setFont(new Font("Tahoma", Font.PLAIN, 26));
        lblOldPw.setBounds(270, 165, 170, 50);
        contentPane.add(lblOldPw);

        oldPwField = new JPasswordField();
        oldPwField.setFont(new Font("Tahoma", Font.PLAIN, 26));
        oldPwField.setBounds(480, 170, 260, 45);
        contentPane.add(oldPwField);

        //password
        JLabel lblNewPw = new JLabel("New Password");
        lblNewPw.setFont(new Font("Tahoma", Font.PLAIN, 26));
        lblNewPw.setBounds(260, 240, 340, 45);
        contentPane.add(lblNewPw);

        newPwField = new JPasswordField();
        newPwField.setFont(new Font("Tahoma", Font.PLAIN, 26));
        newPwField.setBounds(480, 245, 260, 45);
        contentPane.add(newPwField);


        //password
        JLabel lblCfPw = new JLabel("Confirm Password");
        lblCfPw.setFont(new Font("Tahoma", Font.PLAIN, 26));
        lblCfPw.setBounds(235, 315, 340, 45);
        contentPane.add(lblCfPw);

        cfPwField = new JPasswordField();
        cfPwField.setFont(new Font("Tahoma", Font.PLAIN, 26));
        cfPwField.setBounds(480, 320, 260, 45);
        contentPane.add(cfPwField);


        //login button
        //JButton btnLogin;
        JButton btnChange = new JButton("Change");
        btnChange.addActionListener(this);

        btnChange.setFont(new Font("Tahoma", Font.PLAIN, 24));
        btnChange.setBounds(410, 410, 200, 45);
        contentPane.add(btnChange);

        //extra settings
        this.setSize(1000, 600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }



    public void actionPerformed(ActionEvent e) {
        String oldPassword = String.valueOf(oldPwField.getPassword());
        String newPassword = String.valueOf(newPwField.getPassword());
        String cfPassword = String.valueOf(cfPwField.getPassword());
        //JOptionPane.showMessageDialog(null, "Password changed successfully!");
       //dispose();
        //new AuthorInterface(username);

        try {
            if (UserController.changePassword(username, oldPassword, newPassword, cfPassword)) {
                JOptionPane.showMessageDialog(null, "Password changed successfully!");
                //AuthorInterface.authorPwChanged = true;
                dispose();
                new AuthorInterface(username);
            } else {
                JOptionPane.showMessageDialog(null, "Details incorrect!");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }


    }
}
