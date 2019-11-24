/**
 * Class for Chief Editor Registration Interface
 * @author Ting Guo
 * @author Huiqiang Liu
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;


@SuppressWarnings("serial")
public class CfEditorRegInterface extends JFrame implements ActionListener, ItemListener {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> comboTitleTypes;
    private JTextField fnField;
    private JTextField snField;
    private JTextField uniField;
    private JTextField jnTitleField;
    private JTextField issnField;
    private String userTitle;
    private JButton register;
    private JButton back;

    CfEditorRegInterface() {
        this.setTitle("Chief Editor Registration");
        this.setSize(1000, 600);
        this.setLocationRelativeTo(null);
        this.setResizable(false);

        JPanel noticePanel = new JPanel();
        JPanel userPanel = new JPanel();
        JPanel journalPanel = new JPanel();
        JPanel buttonPane = new JPanel();


        //notice banner
        JLabel banner = new JLabel("<html>Only complete this form if you are a chief editor of the journal</html>",
                SwingConstants.CENTER);
        banner.setFont(new Font("Arial", Font.PLAIN, 30));
        banner.setHorizontalAlignment(JLabel.CENTER);
        banner.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));
        //space
        JLabel space = new JLabel("     ");


        //Personal details
        JLabel psFormTitle = new JLabel("Personal details",
                SwingConstants.CENTER);
        psFormTitle.setFont(new Font("Arial", Font.PLAIN, 25));
        //email
        JLabel email = new JLabel("Email Address");
        email.setFont(new Font("Arial", Font.PLAIN, 20));
        emailField = new JTextField(15);
        emailField.setFont(new Font("Arial", Font.PLAIN, 15));


        //password
        JLabel password = new JLabel("Password");
        password.setFont(new Font("Arial", Font.PLAIN, 20));
        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 15));


        //user title
        JLabel title = new JLabel("Title");
        title.setFont(new Font("Arial", Font.PLAIN, 20));
        //title combobox
        String[] titleTypes = {"Prof", "Dr", "Mr","Mrs", "Ms", "Miss"};
        comboTitleTypes = new JComboBox<>(titleTypes);
        comboTitleTypes.addItemListener(this);
        comboTitleTypes.setFont(new Font("Arial", Font.PLAIN, 16));


        //forename
        JLabel forename = new JLabel("Forename");
        forename.setFont(new Font("Arial", Font.PLAIN, 20));
        fnField = new JTextField(15);
        fnField.setFont(new Font("Arial", Font.PLAIN, 15));


        //surname
        JLabel surname = new JLabel("Surname");
        surname.setFont(new Font("Arial", Font.PLAIN, 20));
        snField = new JTextField(15);
        snField.setFont(new Font("Arial", Font.PLAIN, 15));


        //university
        JLabel university = new JLabel("University");
        university.setFont(new Font("Arial", Font.PLAIN, 20));
        uniField = new JTextField(15);
        uniField.setFont(new Font("Arial", Font.PLAIN, 15));


        //journal form title
        JLabel jnFormTitle = new JLabel("Journal details",
                SwingConstants.CENTER);
        jnFormTitle.setFont(new Font("Arial", Font.PLAIN, 25));
        //journal title
        JLabel journalTitle = new JLabel("Title");
        journalTitle.setFont(new Font("Arial", Font.PLAIN, 20));
        jnTitleField = new JTextField(15);
        jnTitleField.setFont(new Font("Arial", Font.PLAIN, 15));


        //journal issn
        JLabel issn = new JLabel("ISSN");
        issn.setFont(new Font("Arial", Font.PLAIN, 20));
        issnField = new JTextField(15);
        issnField.setFont(new Font("Arial", Font.PLAIN, 15));


        //register button
        register = new JButton("Register");
        register.addActionListener(this);
        register.setFont(new Font("Tahoma", Font.PLAIN, 20));


        //back button
        back = new JButton("Back");
        back.addActionListener(this);
        back.setFont(new Font("Tahoma", Font.PLAIN, 20));


        //layouts
        //noticePanel layout
        noticePanel.setLayout(new FlowLayout());
        //userPanel layout
        userPanel.setLayout(new GridBagLayout());
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
        userPanel.setBorder(BorderFactory.createEmptyBorder(0, 80, 50, 25));
        //journalPanel layout
        journalPanel.setLayout(new GridBagLayout());
        journalPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 200, 50));
        //buttonPane layout
        buttonPane.setLayout(new GridBagLayout());
        GridBagConstraints buttonSpace = new GridBagConstraints();
        buttonSpace.insets = new Insets(0, 0, 0, 50);


        //add functions
        noticePanel.add(banner);
        //userPanel
        userPanel.add(space, left);
        userPanel.add(psFormTitle, right);
        userPanel.add(email, left);
        userPanel.add(emailField, right);
        userPanel.add(password, left);
        userPanel.add(passwordField, right);
        userPanel.add(title, left);
        userPanel.add(comboTitleTypes, right);
        userPanel.add(forename, left);
        userPanel.add(fnField, right);
        userPanel.add(surname, left);
        userPanel.add(snField, right);
        userPanel.add(university, left);
        userPanel.add(uniField, right);
        //journalPanel
        journalPanel.add(space, left);
        journalPanel.add(jnFormTitle, right);
        journalPanel.add(journalTitle, left);
        journalPanel.add(jnTitleField, right);
        journalPanel.add(issn, left);
        journalPanel.add(issnField, right);
        //buttonPane
        buttonPane.add(register, buttonSpace);
        buttonPane.add(back, buttonSpace);
        //frame
        this.add(noticePanel);
        this.add(userPanel);
        this.add(journalPanel);
        this.add(buttonPane);
        this.setLayout(new FlowLayout());

        //extra settings
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }


    public void itemStateChanged(ItemEvent e) {

        //System.out.println(e.getItem());
        String item = (String)e.getItem();
        switch (item) {
            case "Prof":
                userTitle = "Prof";
                break;
            case "Mr":
                userTitle = "Mr";
                break;
            case "Ms":
                userTitle = "Ms";
                break;
        }
    }

    public void actionPerformed(ActionEvent e) {
        String email = emailField.getText();
        String password = String.valueOf(passwordField.getPassword());
        String forename = fnField.getText();
        String surname = snField.getText();
        String university = uniField.getText();
        String journalTitle = jnTitleField.getText();
        String issn = String.valueOf(issnField.getText());


        if(e.getSource()== back){
            this.setVisible(false);
            new LoginInterface();
        }
        else {
            if( !email.trim().isEmpty() && !password.trim().isEmpty() && !forename.trim().isEmpty() &&
                    !surname.trim().isEmpty() && !university.trim().isEmpty()
                    && !journalTitle.trim().isEmpty() && !(issn).trim().isEmpty()){
                try {
                    if(UserController.chiefEditorRegistration(email, userTitle, forename,
                            surname,university,password,journalTitle, Integer.parseInt(issn))){
                        this.dispose();
                        JOptionPane.showMessageDialog(null, "You have registered successfully!");
                        new CfEditorRegInterface();
                    } else {
                        JOptionPane.showMessageDialog(null, "Please check that you have completed the form correctly!");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            else {
                JOptionPane.showMessageDialog(null, "Please fill in!");
            }

        }
    }


    public static void main(String[] args) {
        new CfEditorRegInterface();
    }
}