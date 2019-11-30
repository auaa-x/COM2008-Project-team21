/**
 * Class for Change Password Interface
 * @author Ting Guo
 */


import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.sql.SQLException;


public class EditorRegister extends JFrame implements ActionListener {

    public static void main(String[] args) {
        //launching code goes in here
        //new EditorRegister("james.potter@warwick.ac.uk");
    }


    // Needed for serialisation
    private static final long serialVersionUID = 1L;
    private String username;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField cfPwField;
    private int issn;
    private JButton register;
    private JButton back;

    // Constructor with frame title
    public EditorRegister(String username, int issn) {
        this.username = username;
        this.issn = issn;
        this.setTitle("Register a new editor");
        this.setSize(1000, 600);
        this.setLocationRelativeTo(null);
        this.setResizable(false);


        //button panel
        JPanel noticePanel = new JPanel();
        JPanel buttonPane = new JPanel();
        JPanel fieldsPanel = new JPanel();


        //Welcome banner
        JLabel banner = new JLabel("Register a new editor");
        banner.setFont(new Font("Tahoma", Font.PLAIN, 50));
        banner.setBorder(BorderFactory.createEmptyBorder(70, 200, 50, 200));


        //emailField
        JLabel email = new JLabel("Email Address");
        email.setFont(new Font("Tahoma", Font.PLAIN, 26));
        emailField = new JTextField(10);
        emailField.setFont(new Font("Tahoma", Font.PLAIN, 26));

        //password
        JLabel password = new JLabel("Password");
        password.setFont(new Font("Tahoma", Font.PLAIN, 26));
        passwordField = new JPasswordField(10);
        passwordField.setFont(new Font("Tahoma", Font.PLAIN, 26));

        //password
        JLabel lblCfPw = new JLabel("Confirm Password");
        lblCfPw.setFont(new Font("Tahoma", Font.PLAIN, 26));
        cfPwField = new JPasswordField(10);
        cfPwField.setFont(new Font("Tahoma", Font.PLAIN, 26));


        //change button
        register = new JButton("Register");
        register.addActionListener(this);
        register.setFont(new Font("Tahoma", Font.PLAIN, 24));

        //back buttons
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
        fieldsPanel.add(email, left);
        fieldsPanel.add(emailField, right);
        fieldsPanel.add(password, left);
        fieldsPanel.add(passwordField, right);
        fieldsPanel.add(lblCfPw, left);
        fieldsPanel.add(cfPwField, right);
        //button panel
        buttonPane.add(register, buttonSpace);
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
            this.dispose();
            try {
                new ChiefEditorInterface(username);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            String email = emailField.getText();
            String password = String.valueOf(passwordField.getPassword());
            String cfPw = String.valueOf(cfPwField.getPassword());
            //check if fields all have been filled
            if (!email.trim().isEmpty() && !password.trim().isEmpty() && !cfPw.trim().isEmpty()) {
                //email validation
                if (!UserController.isValidEmail(email)) {
                    JOptionPane.showMessageDialog(null, "Please input a valid email.");
                } else {
                    try {
                        //check if user already exists
                        if (UserController.checkEmail(email)) {
                            JOptionPane.showMessageDialog(null, "This email address has been registered,\n" +
                                    " please go to 'appoint an editor'");}
                        else {
                        //double check password
                        if (!cfPw.equals(password)) {
                            JOptionPane.showMessageDialog(null, "Please enter the same password\n" +
                                    " in the confirm password field.");
                        } else {
                            //check password strength
                            if (!UserController.checkPasswordStrength(password)) {
                                JOptionPane.showMessageDialog(null,
                                        "Password is too weak! \nMust include lower and upper case, 8 characters at least.\nSpace is not allowed.");
                            } else {
                                //all conditions verified
                                try {
                                    if (UserController.createTempUser(email, password, 1) &&
                                    UserController.createEditor(email, issn)){
                                        JOptionPane.showMessageDialog(null, "You have registered: \n"
                                                + email + " \nas an editor of \n"  + JournalController.getJournal(issn).getTitle());
                                        this.dispose();
                                        new ChiefEditorInterface(username);
                                    }
                                    else {
                                        JOptionPane.showMessageDialog(null,"Can not connect to database now, \n" +
                                                "please try again.");
                                    }
                                } catch (SQLException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please fill in all the fields!");
            }
        }
    }


}
