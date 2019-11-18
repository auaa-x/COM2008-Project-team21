/**
 * Class for Author Registration Interface
 * @author Ting Guo
 * @author Huiqiang Liu
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;


public class AuthorRegisterInterface extends JFrame implements ActionListener, ItemListener {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> comboTitleTypes;
    private JTextField fnField;
    private JTextField snField;
    private JTextField uniField;
    private JComboBox<String> comboCtTypes;
    private JTextField jnTitleField;
    private JTextField atAbstractField;
    private JPasswordField sharedPasswordField;
    private JTextField addedPDF;
    private JTextField coEmailField;
    private JTextArea addedCoAuthorArea;
    private String category;

    public AuthorRegisterInterface() {
        JFrame frame = new JFrame("Author Registration");
        frame.setSize(1200, 700);
        frame.setLocationRelativeTo(null);

        JPanel noticePanel = new JPanel();
        JPanel userPanel = new JPanel();
        JPanel articlePanel = new JPanel();
        JPanel coAuthorsPanel = new JPanel();
        JPanel addedCoAuthorsPanel = new JPanel();
        JPanel buttonPane = new JPanel();


        //notice banner
        JLabel banner = new JLabel("<html>Only complete this form if you are au author of the journal</html>",
                SwingConstants.CENTER);
        banner.setFont(new Font("Arial", Font.PLAIN, 30));
        banner.setHorizontalAlignment(JLabel.CENTER);
        banner.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 80));
        //space
        JLabel space = new JLabel("       ");






        //Personal details
        JLabel psFormTitle = new JLabel("Personal details",
                SwingConstants.CENTER);
        psFormTitle.setFont(new Font("Arial", Font.PLAIN, 25));
        //email
        JLabel email = new JLabel("Email address");
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
        String[] titleTypes = {"Prof", "Mr", "Ms"};
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






        //article form title
        JLabel atFormTitle = new JLabel("Article details",
                SwingConstants.CENTER);
        atFormTitle.setFont(new Font("Arial", Font.PLAIN, 25));

        //journal categories combobox
        JLabel lblcategory = new JLabel("Category");
        lblcategory.setFont(new Font("Arial", Font.PLAIN, 20));
        String[] categoryTypes = {"Computer Science", "Software Engineering", "Artificial Intelligence"};
        comboCtTypes = new JComboBox<>(categoryTypes);
        comboCtTypes.addItemListener(this);
        comboCtTypes.setFont(new Font("Arial", Font.PLAIN, 16));

        //journal title
        JLabel journalTitle = new JLabel("Title");
        journalTitle.setFont(new Font("Arial", Font.PLAIN, 20));
        jnTitleField = new JTextField(15);
        jnTitleField.setFont(new Font("Arial", Font.PLAIN, 15));

        //article abstract
        JLabel atAbstract = new JLabel("Abstract");
        atAbstract.setFont(new Font("Arial", Font.PLAIN, 20));
        atAbstractField = new JTextField(15);
        atAbstractField.setFont(new Font("Arial", Font.PLAIN, 15));

        //add PDF button and display
        addedPDF = new JTextField("Added PDF",15);
        addedPDF.setFont(new Font("Arial", Font.PLAIN, 15));
        //add pdf button
        JButton btnAddPdf = new JButton("Add PDF");
        btnAddPdf.addActionListener(this);
        btnAddPdf.setFont(new Font("Arial", Font.PLAIN, 15));







        //co-author title
        JLabel coAuthorFormTitle = new JLabel("Co-author details",
                SwingConstants.CENTER);
        coAuthorFormTitle.setFont(new Font("Arial", Font.PLAIN, 25));

        //co-authors'emails
        JLabel coEmail = new JLabel("Email address");
        coEmail.setFont(new Font("Arial", Font.PLAIN, 20));
        coEmailField = new JTextField(15);
        coEmailField.setFont(new Font("Arial", Font.PLAIN, 15));

        //add co-author button
        JButton addCoAuthor = new JButton("Add a co-author");
        //addCoAuthor.addActionListener(this);
        addCoAuthor.setFont(new Font("Tahoma", Font.PLAIN, 15));

        //added-co-author title
        JLabel addedCoAuthor = new JLabel("Added co-authors", SwingConstants.CENTER);
        addedCoAuthor.setVerticalAlignment(SwingConstants.CENTER);
        addedCoAuthor.setFont(new Font("Arial", Font.PLAIN, 25));

        //!!!!!!!!display added author area!!!!!!!!!!!!!!!!!!!
        addedCoAuthorArea = new JTextArea(5, 1);
        addedCoAuthorArea.setFont(new Font("Arial", Font.PLAIN, 15));
        addedCoAuthorArea.setColumns(18);


        //shared password
        JLabel sharedPassword = new JLabel("Shared password");
        sharedPassword.setFont(new Font("Arial", Font.PLAIN, 20));
        sharedPasswordField = new JPasswordField(15);
        sharedPasswordField.setFont(new Font("Arial", Font.PLAIN, 15));





        //register button
        JButton register = new JButton("Register and submit the article");
        //back.addActionListener(this);
        register.setFont(new Font("Tahoma", Font.PLAIN, 20));

        //back button
        JButton back = new JButton("Back");
        //back.addActionListener(this);
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
        userPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 25));
        //articlePanel layout
        articlePanel.setLayout(new GridBagLayout());
        articlePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 90, 50));
        //coAuthorsPanel layout
        coAuthorsPanel.setLayout(new GridBagLayout());
        coAuthorsPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 50));
        //addedCoAuthorsPanel layout
        addedCoAuthorsPanel.setLayout(new BoxLayout(addedCoAuthorsPanel, BoxLayout.PAGE_AXIS));
        addedCoAuthorsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 30, 80));
        //buttonPane layout
        buttonPane.setLayout(new GridBagLayout());
        GridBagConstraints buttonSpace = new GridBagConstraints();
        buttonSpace.insets = new Insets(0, 215, 0, 105);


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
        articlePanel.add(space, left);
        articlePanel.add(atFormTitle, right);
        articlePanel.add(lblcategory, left);
        articlePanel.add(comboCtTypes, right);
        articlePanel.add(journalTitle, left);
        articlePanel.add(jnTitleField, right);
        articlePanel.add(atAbstract, left);
        articlePanel.add(atAbstractField, right);
        articlePanel.add(btnAddPdf, left);
        articlePanel.add(addedPDF, right);

        //coAuthorsPanel
        coAuthorsPanel.add(new JLabel("         "), left);
        coAuthorsPanel.add(coAuthorFormTitle, right);
        coAuthorsPanel.add(coEmail, left);
        coAuthorsPanel.add(coEmailField, right);
        coAuthorsPanel.add(new JLabel("         "), left);
        coAuthorsPanel.add(addCoAuthor, right);
        coAuthorsPanel.add(sharedPassword, left);
        coAuthorsPanel.add(sharedPasswordField, right);

        //addedCoAuthorsPanel
        addedCoAuthorsPanel.add(addedCoAuthor);
        addedCoAuthorsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        addedCoAuthorsPanel.add(addedCoAuthorArea);

        //buttonPane
        buttonPane.add(back, buttonSpace);
        buttonPane.add(register);

        //frame
        frame.add(noticePanel);
        frame.add(userPanel);
        frame.add(articlePanel);
        frame.add(coAuthorsPanel);
        frame.add(addedCoAuthorsPanel);
        frame.add(buttonPane);
        frame.setLayout(new FlowLayout());

        //extra settings
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }


    public void itemStateChanged(ItemEvent e) {

        //System.out.println(e.getItem());
        String item = (String)e.getItem();
        if(item == "Computer Science"){
            category = "Journal of Computer Science";
        } else if (item == "Software Engineering"){
            category = "Journal of Software Engineering";
        } else if (item == "Artificial Intelligence"){
            category = "Journal of Artificial Intelligence";
        };

    }

    public void actionPerformed(ActionEvent e) {
        /*


         */
    }


    public static void main(String args[]) {
        new AuthorRegisterInterface();
    }
}