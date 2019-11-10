/**
 * Class for login window
 * @author Ting Guo
 * @author Huiqiang Liu
 */
import java.awt.*;
import javax.swing.*;


public class Login extends JFrame{
    // Needed for serialisation
    private static final long serialVersionUID = 1L;
    private JTextField textField;
    private JPasswordField passwordField;

    // Constructor with frame title
    public Login(String title) throws HeadlessException {
        //construction code goes in here
        super(title);  //pass the title name

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
        lblNewLabel.setFont(new Font("Times New Roman", Font.PLAIN, 46));
        lblNewLabel.setBounds(425, 15, 270, 90);
        contentPane.add(lblNewLabel);


        //user types combobox
        String[] userTypes = {"User Type", "Editor", "Author", "Reviewer"};
        JComboBox<String> comboUserTypes = new JComboBox<>(userTypes);
        comboUserTypes.setFont(new Font("Tahoma", Font.PLAIN, 20));
        comboUserTypes.setBounds(550, 140, 190, 50);
        contentPane.add(comboUserTypes);


        //username
        JLabel lblUsername = new JLabel("Username");
        lblUsername.setFont(new Font("Tahoma", Font.PLAIN, 28));
        lblUsername.setBounds(290, 205, 170, 50);
        contentPane.add(lblUsername);

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

        JButton lb1Login = new JButton("Log in");
        lb1Login.setFont(new Font("Tahoma", Font.PLAIN, 25));
        lb1Login.setBounds(410, 390, 200, 45);
        contentPane.add(lb1Login);


        setDefaultCloseOperation(EXIT_ON_CLOSE); //ensure that Java terminates on close
        setVisible(true);

    }

    public static void main (String[] args) {
        //launching code goes in here
        new Login("Login");
    }
}
