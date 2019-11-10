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

        JLabel lblNewLabel = new JLabel("Welcome");
        lblNewLabel.setForeground(Color.BLACK);
        lblNewLabel.setFont(new Font("Times New Roman", Font.PLAIN, 46));
        lblNewLabel.setBounds(423, 13, 273, 93);
        contentPane.add(lblNewLabel);

        textField = new JTextField();
        textField.setFont(new Font("Tahoma", Font.PLAIN, 32));
        textField.setBounds(481, 170, 281, 68);
        contentPane.add(textField);
        textField.setColumns(10);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Tahoma", Font.PLAIN, 32));
        passwordField.setBounds(481, 286, 281, 68);
        contentPane.add(passwordField);

        JLabel lblUsername = new JLabel("Username");
        lblUsername.setBackground(Color.BLACK);
        lblUsername.setForeground(Color.BLACK);
        lblUsername.setFont(new Font("Tahoma", Font.PLAIN, 31));
        lblUsername.setBounds(250, 166, 193, 52);
        contentPane.add(lblUsername);

        JLabel lblPassword = new JLabel("Password");
        lblPassword.setForeground(Color.BLACK);
        lblPassword.setBackground(Color.CYAN);
        lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 31));
        lblPassword.setBounds(250, 286, 193, 52);
        contentPane.add(lblPassword);

        JButton lb1Login = new JButton("Log in");
        lb1Login.setForeground(Color.BLACK);
        lb1Login.setBackground(Color.CYAN);
        lb1Login.setFont(new Font("Tahoma", Font.PLAIN, 31));
        lb1Login.setBounds(350, 386, 250, 52);
        contentPane.add(lb1Login);



        setDefaultCloseOperation(EXIT_ON_CLOSE); //ensure that Java terminates on close
        setVisible(true);

    }

    public static void main (String[] args) {
        //launching code goes in here
        new Login("Login");
    }
}
