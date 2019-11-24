/**
 * Class for login window
 * @author Ting Guo
 * @author Huiqiang Liu
 */
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.sql.SQLException;


public class LoginInterface extends JFrame implements ActionListener, ItemListener {

    // Needed for serialisation
    private static final long serialVersionUID = 1L;
    private JTextField emailField;
    private JPasswordField passwordField;
    private int userType;
    private JButton btnLogin;
    private JButton btnNoLogin;
    private JButton btnRegister;



    // Constructor with frame title
    LoginInterface() {
        this.setTitle("Journal Publishing System");
        this.setSize(1000, 600);
        this.setLocationRelativeTo(null);



        //button panel
        JPanel noticePanel = new JPanel();
        JPanel buttonPane = new JPanel();
        JPanel fieldsPanel = new JPanel();


        //Welcome banner
        JLabel banner = new JLabel("Welcome");
        banner.setFont(new Font("Tahoma", Font.PLAIN, 50));
        banner.setBorder(BorderFactory.createEmptyBorder(70, 200, 50, 200));



        //user types combobox
        JLabel role = new JLabel("Role: ");
        role.setFont(new Font("Tahoma", Font.PLAIN, 26));
        String[] userTypes = {"User Type", "Editor", "Author", "Reviewer"};
        JComboBox<String> comboUserTypes = new JComboBox<>(userTypes);
        comboUserTypes.addItemListener(this);
        comboUserTypes.setFont(new Font("Tahoma", Font.PLAIN, 20));



        //username
        JLabel lblEmail = new JLabel("Email address");
        lblEmail.setFont(new Font("Tahoma", Font.PLAIN, 26));
        emailField = new JTextField();
        emailField.setFont(new Font("Tahoma", Font.PLAIN, 26));
        emailField.setColumns(10);


        //password
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 26));
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Tahoma", Font.PLAIN, 26));
        passwordField.setColumns(10);



        //login button
        btnLogin = new JButton("Log in");
        btnLogin.addActionListener(this);
        btnLogin.setFont(new Font("Tahoma", Font.PLAIN, 22));


        //enter as a reader
        btnNoLogin = new JButton("Enter as a reader");
        btnNoLogin.addActionListener(this);
        btnNoLogin.setFont(new Font("Tahoma", Font.PLAIN, 22));


        //register
        btnRegister = new JButton("Register");
        btnRegister.addActionListener(this);
        btnRegister.setFont(new Font("Tahoma", Font.PLAIN, 22));



        //layouts
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
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(0, 80, 50, 25));
        //button panel
        buttonPane.setLayout(new GridBagLayout());
        GridBagConstraints buttonSpace = new GridBagConstraints();
        buttonSpace.insets = new Insets(0, 50, 0, 5);



        //add functions
        //noticePanel
        noticePanel.add(banner);
        //fieldsPanel
        fieldsPanel.add(role, left);
        fieldsPanel.add(comboUserTypes, right);
        fieldsPanel.add(lblEmail, left);
        fieldsPanel.add(emailField, right);
        fieldsPanel.add(lblPassword, left);
        fieldsPanel.add(passwordField, right);


        buttonPane.add(btnLogin,buttonSpace);
        buttonPane.add(btnNoLogin,buttonSpace);
        buttonPane.add(btnRegister,buttonSpace);

        this.add(noticePanel);
        this.add(fieldsPanel);
        this.add(buttonPane);

        this.setLayout(new FlowLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE); //ensure that Java terminates on close
        setVisible(true);
    }

    public void itemStateChanged(ItemEvent e) {

        String item = (String)e.getItem();
        switch (item) {
            case "Editor":
                userType = 1;
                break;
            case "Author":
                userType = 2;
                break;
            case "Reviewer":
                userType = 3;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + item);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnLogin) {
            String userName = emailField.getText();
            String password = String.valueOf(passwordField.getPassword());

            try {
                if (!userName.trim().isEmpty() && !password.trim().isEmpty()) {
                    if(UserController.login(userName, password, userType)){
                        this.dispose();
                        JOptionPane.showMessageDialog(null, "Logged in");
                        switch (userType) {
                            case 1 : //Editor
                                new CfEditorRegInterface();
                                break;
                            case 2 : //Author
                                new AuthorInterface();
                                break;
                            case 3 : //Reviewer
                                //new ReviewerInterface();
                                break;
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Wrong Username & Password");
                    }
                }
                else{
                    if (userName.trim().isEmpty()){
                        JOptionPane.showMessageDialog(null, "Please enter your email address!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Please enter your password!");
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        else if (e.getSource() == btnNoLogin){
            this.dispose();
            new readerInterface();
        }
        else if (e.getSource() == btnRegister){
            String[] options = {"Author", "Chief Editor","back"};
            int x = JOptionPane.showOptionDialog(null, "Would you like register as:",
                    "Select your role",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            if (x == 0){
                this.setVisible(false);
                new AuthorRegisterInterface();
            } else if (x == 1){
                this.setVisible(false);
                new CfEditorRegInterface();
            }
        }
    }

    public static void main (String[] args) {
        //launching code goes in here
        new LoginInterface();
    }

}
