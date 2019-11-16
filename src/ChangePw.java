/**
 * Class for Change password window
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
        new ChangePw();
    }


    // Needed for serialisation
    private static final long serialVersionUID = 1L;
    private JPasswordField oldPwField;
    private JPasswordField newPwField;
    private JPasswordField cfPwField;
    //private int userType;



    // Constructor with frame title
    public ChangePw() {
        //construction code goes in here
        super("Change Password");  //pass the title name

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

        setDefaultCloseOperation(EXIT_ON_CLOSE); //ensure that Java terminates on close
        setVisible(true);
    }



    public void actionPerformed(ActionEvent e) {
        String oldPassword = String.valueOf(oldPwField.getPassword());
        String newPassword = String.valueOf(newPwField.getPassword());
        String cfPassword = String.valueOf(cfPwField.getPassword());

        //try {
            //if( method to check password correctness){
                if (newPassword.equals(cfPassword)) {
                    JOptionPane.showMessageDialog(null, "Password changed successfully!");
                } else {
                    JOptionPane.showMessageDialog(null, "Please confirm your new password!");
                }
            //}
        //} catch (SQLException ex) {
        //            ex.printStackTrace();
        //        };
        //         */

        System.out.println("Old Password is: " + oldPassword);
        System.out.println("New Password is: " + newPassword);
        System.out.println("Confirm Password is: " + cfPassword);
    }


}