/**
 * Class for Author Registration Interface
 * @author Ting Guo
 * @author Huiqiang Liu
 */

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.LinkedList;


@SuppressWarnings("serial")
public class AuthorRegisterInterface extends JFrame implements ActionListener, ItemListener {
    private static final long serialVersionUID = 1L;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField cfPasswordField;
    private JComboBox<String> comboTitleTypes;
    private JTextField fnField;
    private JTextField snField;
    private JTextField uniField;
    private JComboBox<Journal> comboJnTypes;
    private ButtonGroup group;
    private LinkedList<Journal> journals;
    private Journal journalSelected;
    private JTextField articleTitleField;
    private JTextArea atAbstractField;
    private JPasswordField sharedPasswordField;
    private JTextArea addedPDF;
    private JTextField coEmailField;
    private JTextArea addedCoAuthorArea;
    private String userTitle = "Mrs";
    private Path path = null;
    private File pdf;
    private JProgressBar progressBar;
    private int issn;

    private JButton resetCoAuthor;
    private JButton btnAddPdf;
    private JButton addCoAuthor;
    private JButton back;
    private JButton register;



    public AuthorRegisterInterface() throws SQLException, FontFormatException {
        this.setTitle("Author Registration");
        this.setSize(1200, 700);
        this.setLocationRelativeTo(null);
        this.setResizable(false);

        JPanel noticePanel = new JPanel();
        JPanel userPanel = new JPanel();
        JPanel articlePanel = new JPanel();
        JPanel coAuthorsPanel = new JPanel();
        JPanel addedCoAuthorsPanel = new JPanel();
        JPanel buttonPane = new JPanel();


        //notice banner
        JLabel banner = new JLabel("<html>Only complete this form if you are the author of the journal</html>",
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

        //password
        JLabel cfPassword = new JLabel("Confirm Password");
        cfPassword.setFont(new Font("Arial", Font.PLAIN, 20));
        cfPasswordField = new JPasswordField(15);
        cfPasswordField.setFont(new Font("Arial", Font.PLAIN, 15));


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






        //article form title
        JLabel atFormTitle = new JLabel("Article details",
                SwingConstants.CENTER);
        atFormTitle.setFont(new Font("Arial", Font.PLAIN, 25));

        //journal categories combobox
        JLabel lblJournal = new JLabel("Journal");
        lblJournal.setFont(new Font("Arial", Font.PLAIN, 20));
        journals = new LinkedList<Journal>(JournalController.getAllJournals());
        comboJnTypes = new JComboBox<Journal>();
        for (Journal journal : journals) {
            comboJnTypes.addItem(journal);
        }
        //comboJnTypes.setSelectedIndex(0);
        comboJnTypes.addItemListener(this);
        comboJnTypes.setFont(new Font("Arial", Font.PLAIN, 16));


        //article title
        JLabel articleTitle = new JLabel("Title");
        articleTitle.setFont(new Font("Arial", Font.PLAIN, 20));
        articleTitleField = new JTextField(20);
        articleTitleField.setFont(new Font("Arial", Font.PLAIN, 15));


        //article abstract
        JLabel atAbstract = new JLabel("Abstract");
        atAbstract.setFont(new Font("Arial", Font.PLAIN, 20));
        atAbstractField = new JTextArea(8,19);
        atAbstractField.setFont(new Font("Arial", Font.PLAIN, 15));
        JScrollPane abstractPane = new JScrollPane(atAbstractField);
        abstractPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        //add PDF button and display
        addedPDF = new JTextArea(1,20);
        addedPDF.setText("Added PDF");
        addedPDF.setFont(new Font("Arial", Font.PLAIN, 15));
        addedPDF.setEditable (false); //set textArea non-editable
        JScrollPane pdfPane = new JScrollPane(addedPDF);
        pdfPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        pdfPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        //add pdf button
        btnAddPdf = new JButton("Add PDF");
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
        resetCoAuthor = new JButton("Reset");
        resetCoAuthor.addActionListener(this);
        resetCoAuthor.setFont(new Font("Tahoma", Font.PLAIN, 15));

        //add co-author button
        addCoAuthor = new JButton("Add a co-author");
        addCoAuthor.addActionListener(this);
        addCoAuthor.setFont(new Font("Tahoma", Font.PLAIN, 15));

        //added-co-author title
        JLabel addedCoAuthor = new JLabel("Added co-authors", SwingConstants.CENTER);
        addedCoAuthor.setVerticalAlignment(SwingConstants.CENTER);
        addedCoAuthor.setFont(new Font("Arial", Font.PLAIN, 25));

        //display added author area
        addedCoAuthorArea = new JTextArea(5, 18);
        addedCoAuthorArea.setFont(new Font("Arial", Font.PLAIN, 15));
        addedCoAuthorArea.setEditable (false); //set textArea non-editable
        JScrollPane scrollPane = new JScrollPane(addedCoAuthorArea);
        scrollPane.setVerticalScrollBarPolicy (ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);



        //shared password
        JLabel sharedPassword = new JLabel("Shared password");
        sharedPassword.setFont(new Font("Arial", Font.PLAIN, 20));
        sharedPasswordField = new JPasswordField(15);
        sharedPasswordField.setFont(new Font("Arial", Font.PLAIN, 15));





        //register button
        register = new JButton("Register and submit the article");
        register.addActionListener(this);
        register.setFont(new Font("Tahoma", Font.PLAIN, 20));

        //back button
        back = new JButton("Back");
        back.addActionListener(this);


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
        articlePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 50));
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
        userPanel.add(cfPassword, left);
        userPanel.add(cfPasswordField, right);
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
        articlePanel.add(lblJournal, left);
        articlePanel.add(comboJnTypes, right);
        articlePanel.add(articleTitle, left);
        articlePanel.add(articleTitleField, right);
        articlePanel.add(atAbstract, left);
        articlePanel.add(abstractPane, right);
        articlePanel.add(btnAddPdf, left);
        articlePanel.add(pdfPane, right);

        //coAuthorsPanel
        coAuthorsPanel.add(space, left);
        coAuthorsPanel.add(coAuthorFormTitle, right);
        coAuthorsPanel.add(coEmail, left);
        coAuthorsPanel.add(coEmailField, right);
        coAuthorsPanel.add(resetCoAuthor, left);
        coAuthorsPanel.add(addCoAuthor, right);
        coAuthorsPanel.add(sharedPassword, left);
        coAuthorsPanel.add(sharedPasswordField, right);

        //addedCoAuthorsPanel
        addedCoAuthorsPanel.add(addedCoAuthor);
        addedCoAuthorsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        addedCoAuthorsPanel.add(scrollPane);

        //buttonPane
        buttonPane.add(back, buttonSpace);
        buttonPane.add(register);

        //frame
        this.add(noticePanel);
        this.add(userPanel);
        this.add(articlePanel);
        this.add(coAuthorsPanel);
        this.add(addedCoAuthorsPanel);
        this.add(buttonPane);
        this.setLayout(new FlowLayout());

        //extra settings
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }


    public void itemStateChanged(ItemEvent e) {
        //{"Prof", "Dr", "Mr","Mrs", "Ms", "Miss"};
        if (e.getSource() == comboTitleTypes) {
            String item = (String) e.getItem();
            switch (item) {
                case "Prof":
                    userTitle = "Prof";
                    break;
                case "Dr":
                    userTitle = "Dr";
                    break;
                case "Mr":
                    userTitle = "Mr";
                    break;
                case "Mrs":
                    userTitle = "Mrs";
                    break;
                case "Ms":
                    userTitle = "Ms";
                    break;
                case "Miss":
                    userTitle = "Miss";
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + item);
            }
        } else if (e.getSource() == comboJnTypes) {
            journalSelected = (Journal) comboJnTypes.getSelectedItem();
          //  System.out.println(journalSelected.getTitle());
        }
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == back) {
            new LoginInterface();
            this.dispose();
        }
        //btnAddPdf actionEvent : get file path from JFileChooser than convert it into file object
        else if (e.getSource() == btnAddPdf) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Please select your article file");
            fileChooser.setApproveButtonText("ok");
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Pdf Files", "pdf");
            fileChooser.setFileFilter(filter);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(this)) {
                path= Paths.get(fileChooser.getSelectedFile().getPath());
            }
            addedPDF.setText(fileChooser.getSelectedFile().getPath());
            pdf = path.toFile();

        } else if (e.getSource() == addCoAuthor) {
            //add co-author email to the emails list
            String emails = coEmailField.getText();
            if (!emails.trim().isEmpty()) {
                UserController.addCoAuthor(emails);
                addedCoAuthorArea.append(emails + "\n");
                coEmailField.setText("");
            }
        } else if (e.getSource() == resetCoAuthor) {
            UserController.coAuthorsList.clear();
            addedCoAuthorArea.setText("");
        } else if (e.getSource() == register) {
            String email = emailField.getText();
            String surname = snField.getText();
            String forename = fnField.getText();
            String university = uniField.getText();
            String cfPw = String.valueOf(cfPasswordField.getPassword());
            String password = String.valueOf(passwordField.getPassword());
            String sharedPassword = String.valueOf(sharedPasswordField.getPassword());
            String articleTitle = articleTitleField.getText();
            String atAbstract = atAbstractField.getText();
            int issn = journalSelected.getIssn();

            if (!email.trim().isEmpty() && !password.trim().isEmpty() && !forename.trim().isEmpty() &&
                    !surname.trim().isEmpty() && !university.trim().isEmpty()
                    && !sharedPassword.trim().isEmpty() && !articleTitle.trim().isEmpty() && UserController.isValidEmail(email)) {
                if (!cfPw.equals(password)) {
                    JOptionPane.showMessageDialog(null, "Please check your password again.");
                } else {
                    try {
                            if (UserController.mainAuthorRegistration(email, userTitle, forename,
                                    surname, university, password, sharedPassword, articleTitle, atAbstract,
                                    pdf, issn )) {
                                JOptionPane.showMessageDialog(null, "You have registered successfully!");
                                dispose();
                                new AuthorInterface(email);
                            }
                    } catch (SQLException | FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please fill in all fields!");
            }
        }
    }


    public static void main(String[] args) throws SQLException, FontFormatException {
        new AuthorRegisterInterface();
    }
}