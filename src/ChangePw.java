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

    public static void main(String[] args) {
        //launching code goes in here
        new ChangePw("james.potter@warwick.ac.uk", 1 );
    }


    // Needed for serialisation
    private static final long serialVersionUID = 1L;
    private String username;
    private JPasswordField oldPwField;
    private JPasswordField newPwField;
    private JPasswordField cfPwField;
    private JButton change;
    private JButton back;
    int userType;


    // Constructor with frame title
    public ChangePw(String username, int usertype) {
        this.username = username;
        this.userType = usertype;

        this.setTitle("Change Password");
        this.setSize(1000, 600);
        this.setLocationRelativeTo(null);
        this.setResizable(false);


        //button panel
        JPanel noticePanel = new JPanel();
        JPanel buttonPane = new JPanel();
        JPanel fieldsPanel = new JPanel();


        //Welcome banner
        JLabel banner = new JLabel("Change Password");
        banner.setFont(new Font("Tahoma", Font.PLAIN, 50));
        banner.setBorder(BorderFactory.createEmptyBorder(70, 200, 50, 200));


        //Old password Field
        JLabel lblOldPw = new JLabel("Old Password");
        lblOldPw.setFont(new Font("Tahoma", Font.PLAIN, 26));
        oldPwField = new JPasswordField(10);
        oldPwField.setFont(new Font("Tahoma", Font.PLAIN, 26));

        //password
        JLabel lblNewPw = new JLabel("New Password");
        lblNewPw.setFont(new Font("Tahoma", Font.PLAIN, 26));
        newPwField = new JPasswordField(10);
        newPwField.setFont(new Font("Tahoma", Font.PLAIN, 26));

        //password
        JLabel lblCfPw = new JLabel("Confirm Password");
        lblCfPw.setFont(new Font("Tahoma", Font.PLAIN, 26));
        cfPwField = new JPasswordField(10);
        cfPwField.setFont(new Font("Tahoma", Font.PLAIN, 26));


        //change button
        change = new JButton("Change");
        change.addActionListener(this);
        change.setFont(new Font("Tahoma", Font.PLAIN, 24));

        //back button
        back = new JButton("Back");
        back.addActionListener(this);
        back.setFont(new Font("Tahoma", Font.PLAIN, 24));


        //layouts
        this.setLayout(new FlowLayout());
        //notice panel
        noticePanel.setLayout(new FlowLayout());
        //fields panel
        fieldsPanel.setLayout(new GridBagLayout());
        GridBagConstraints left = new GridBagConstraints();
        left.anchor = GridBagConstraints.EAST;
        left.weighty = 10.0;
        left.insets = new Insets(5, 2, 5, 5);
        left.ipadx = 2;
        GridBagConstraints right = new GridBagConstraints();
        right.anchor = GridBagConstraints.WEST;
        right.ipady = 5;
        right.insets = new Insets(0, 0, 0, 20);
        right.gridwidth = GridBagConstraints.REMAINDER;
        right.weightx = 0.5;
        right.weighty = 0.5;
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(0, 130, 50, 80));
        //button panel
        buttonPane.setLayout(new GridBagLayout());
        GridBagConstraints buttonSpace = new GridBagConstraints();
        buttonSpace.insets = new Insets(0, 50, 0, 5);


        //add functions
        //noticePanel
        noticePanel.add(banner);
        //fieldsPanel
        fieldsPanel.add(lblOldPw, left);
        fieldsPanel.add(oldPwField, right);
        fieldsPanel.add(lblNewPw, left);
        fieldsPanel.add(newPwField, right);
        fieldsPanel.add(lblCfPw, left);
        fieldsPanel.add(cfPwField, right);
        //button panel
        buttonPane.add(change, buttonSpace);
        buttonPane.add(back, buttonSpace);

        this.add(noticePanel);
        this.add(fieldsPanel);
        this.add(buttonPane);

        //extra settings

        this.setSize(1000, 600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }


    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == back) {
            dispose();
            switch (userType) {
                case 1:
                    try {
                        new EditorInterface(username);
                        System.out.println("Editor back");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    break;
                case 2:
                    new AuthorInterface(username);
                    break;
                case 3:
                    // new ReviewerInterface(username);
                    break;
            }
        } else {
            String oldPassword = String.valueOf(oldPwField.getPassword());
            String newPassword = String.valueOf(newPwField.getPassword());
            String cfPassword = String.valueOf(cfPwField.getPassword());
            try {
                if (UserController.checkPasswordStrength(newPassword)) {
                    if (UserController.changePassword(username, oldPassword, newPassword, cfPassword)) {
                        JOptionPane.showMessageDialog(null, "Password changed successfully!");
                        //AuthorInterface.authorPwChanged = true;
                        dispose();
                        switch (userType) {
                            case 1:
                                new EditorInterface(username);
                                break;
                            case 2:
                                new AuthorInterface(username);
                                break;
                            case 3:
                                //new ReviewerInterface(username);
                                break;
                        }
                        new AuthorInterface(username);
                    } else {
                        JOptionPane.showMessageDialog(null, "Details incorrect!");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Password is too weak! \nMust include lower and upper case, 8 characters at least. Space is not allowed.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }


}
