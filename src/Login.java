/**
 * Class for login window
 * @author Ting Guo
 * @author Huiqiang Liu
 */
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.sql.SQLException;


public class Login extends JFrame implements ActionListener, ItemListener {

    public static void main (String[] args) {
        //launching code goes in here
        new Login();
    }


    // Needed for serialisation
    private static final long serialVersionUID = 1L;
    private JTextField textField;
    private JPasswordField passwordField;
    private JComboBox<String> comboUserTypes;
    private int userType;



    // Constructor with frame title
    public Login() {
        //construction code goes in here
        super("Login");  //pass the title name

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
        /*contentPane.setLayout(new FlowLayout());
        contentPane.add(new JButton("Login"));*/

        //Welcome banner
        JLabel lblNewLabel = new JLabel("Welcome");
        lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 46));
        lblNewLabel.setBounds(425, 15, 270, 90);
        contentPane.add(lblNewLabel);


        //user types combobox
        String[] userTypes = {"User Type", "Editor", "Author", "Reviewer"};
        JComboBox<String> comboUserTypes = new JComboBox<>(userTypes);
        comboUserTypes.addItemListener(this);

        comboUserTypes.setFont(new Font("Tahoma", Font.PLAIN, 20));
        comboUserTypes.setBounds(550, 140, 190, 50);
        contentPane.add(comboUserTypes);


        //username
        JLabel lblEmail = new JLabel("Email address");
        lblEmail.setFont(new Font("Tahoma", Font.PLAIN, 28));
        lblEmail.setBounds(290, 205, 170, 50);
        contentPane.add(lblEmail);

        textField = new JTextField();
        textField.setFont(new Font("Tahoma", Font.PLAIN, 28));
        textField.setBounds(480, 210, 260, 50);
        contentPane.add(textField);
        textField.setColumns(10);


        //password
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 28));
        lblPassword.setBounds(290, 285, 170, 50);
        contentPane.add(lblPassword);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Tahoma", Font.PLAIN, 28));
        passwordField.setBounds(480, 290, 260, 50);
        contentPane.add(passwordField);


        //login button
        //JButton btnLogin;
        JButton btnLogin = new JButton("Log in");
        btnLogin.addActionListener(this);

        btnLogin.setFont(new Font("Tahoma", Font.PLAIN, 25));
        btnLogin.setBounds(410, 390, 200, 45);
        contentPane.add(btnLogin);
        setVisible(true);



        setDefaultCloseOperation(EXIT_ON_CLOSE); //ensure that Java terminates on close
        setVisible(true);
    }

    public void itemStateChanged(ItemEvent e) {

        //System.out.println(e.getItem());
        String item = (String)e.getItem();
        if(item == "Editor"){
            userType = 1;
        } else if (item == "Author"){
            userType = 2;
        } else if (item == "Reviewer"){
            userType = 3;
        };
    }

    public void actionPerformed(ActionEvent e) {
            String userName = textField.getText();
            @SuppressWarnings("deprecation")
            String password = passwordField.getText();

            try {
                if(UserController.login(userName, password, userType)){
                    JOptionPane.showMessageDialog(null, "Logged in");
                } else {
                    JOptionPane.showMessageDialog(null, "Wrong Username & Password");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            };


            //System.out.println("Username is: " + userName);
            //System.out.println("Password is: " + password);
            //System.out.println("Usertype is: " + userType);
    }


}
