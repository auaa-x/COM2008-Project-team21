/**
 * Class for author to update profile information
 * @author Ting Guo
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;

public class UpdateProfileInterface extends JFrame implements ActionListener, ItemListener {
    public static void main(String[] args) {
        //launching code goes in here
        new UpdateProfileInterface("chaddock@illinois.ac.uk", 2, false);
    }

    // Needed for serialisation
    private static final long serialVersionUID = 1L;
    private JPanel updatePanel, displayPanel;
    private String username;
    private String currentTitle;
    private String currentFn;
    private String currentSn;
    private String currentUni;
    private JComboBox<String> comboTitleTypes;
    private String userTitle;
    private JTextField titleField;
    private JTextField fnField;
    private JTextField snField;
    private JTextField uniField;
    private JButton update, update1;
    private JButton back, back1;
    private int userType;
    private boolean validation;


    // Constructor with frame title
    public UpdateProfileInterface(String username, int userType, boolean validation) {
        this.username = username;
        this.userType = userType;
        this.setTitle("Update Profile");
        this.setSize(1000, 600);
        this.setLocationRelativeTo(null);
        this.setResizable(false);

        try {
            currentTitle = UserController.getUsersTitle(username);
            currentFn = UserController.getUsersForename(username);
            currentSn = UserController.getUsersSurname(username);
            currentUni = UserController.getUsersUniversity(username);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        if (!validation) {
            System.out.println("not validation");
            displayInfoPanel();
        } else if (validation) {
            System.out.println("validation");
            UpdateInfoPanel();
        }
        //extra settings
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public void UpdateInfoPanel() {
        updatePanel = new JPanel();
        JPanel noticePanel = new JPanel();
        JPanel buttonPane = new JPanel();
        JPanel fieldsPanel = new JPanel();


        //Welcome banner
        JLabel banner = new JLabel("Update personal details");
        banner.setFont(new Font("Tahoma", Font.PLAIN, 50));
        banner.setBorder(BorderFactory.createEmptyBorder(70, 200, 50, 200));


        //user title
        JLabel title = new JLabel("Title");
        title.setFont(new Font("Arial", Font.PLAIN, 20));
        //title combobox
        String[] titleTypes = {"Prof", "Dr", "Mr", "Mrs", "Ms", "Miss"};
        comboTitleTypes = new JComboBox<>(titleTypes);
        comboTitleTypes.addItemListener(this);
        comboTitleTypes.setFont(new Font("Arial", Font.PLAIN, 16));

        //password
        JLabel forename = new JLabel("Forename");
        forename.setFont(new Font("Tahoma", Font.PLAIN, 26));
        fnField = new JTextField(10);
        fnField.setText(currentFn);
        fnField.setEnabled(true);
        fnField.setFont(new Font("Tahoma", Font.PLAIN, 26));

        //password
        JLabel surname = new JLabel("Surname");
        surname.setFont(new Font("Tahoma", Font.PLAIN, 26));
        snField = new JTextField(10);
        snField.setText(currentSn);
        snField.setEnabled(true);
        snField.setFont(new Font("Tahoma", Font.PLAIN, 26));

        //university
        JLabel university = new JLabel("University");
        university.setFont(new Font("Tahoma", Font.PLAIN, 26));
        uniField = new JTextField(10);
        uniField.setText(currentUni);
        uniField.setEnabled(true);
        uniField.setFont(new Font("Tahoma", Font.PLAIN, 26));


        //change button
        update1 = new JButton("Update");
        update1.addActionListener(this);
        update1.setFont(new Font("Tahoma", Font.PLAIN, 24));

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
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(0, 130, 50, 180));
        //button panel
        buttonPane.setLayout(new GridBagLayout());
        GridBagConstraints buttonSpace = new GridBagConstraints();
        buttonSpace.insets = new Insets(0, 50, 0, 5);


        //add functions
        //noticePanel
        noticePanel.add(banner);
        //fieldsPanel
        fieldsPanel.add(title, left);
        fieldsPanel.add(comboTitleTypes, right);
        fieldsPanel.add(forename, left);
        fieldsPanel.add(fnField, right);
        fieldsPanel.add(surname, left);
        fieldsPanel.add(snField, right);
        fieldsPanel.add(university, left);
        fieldsPanel.add(uniField, right);
        //button panel
        buttonPane.add(update1, buttonSpace);
        buttonPane.add(back, buttonSpace);

        this.add(noticePanel);
        this.add(fieldsPanel);
        this.add(buttonPane);
    }


    public void displayInfoPanel() {
        System.out.println("not validation");
        displayPanel = new JPanel();
        JPanel noticePanel = new JPanel();
        JPanel buttonPane = new JPanel();
        JPanel fieldsPanel = new JPanel();


        //Welcome banner
        JLabel banner = new JLabel("Update personal details");
        banner.setFont(new Font("Tahoma", Font.PLAIN, 50));
        banner.setBorder(BorderFactory.createEmptyBorder(70, 200, 50, 200));


        //title
        JLabel title = new JLabel("Title");
        title.setFont(new Font("Tahoma", Font.PLAIN, 26));
        titleField = new JTextField(10);
        titleField.setText(currentTitle);
        titleField.setEnabled(false);
        titleField.setFont(new Font("Tahoma", Font.PLAIN, 26));

        //password
        JLabel forename = new JLabel("Forename");
        forename.setFont(new Font("Tahoma", Font.PLAIN, 26));
        fnField = new JTextField(10);
        fnField.setText(currentFn);
        fnField.setEnabled(false);
        fnField.setFont(new Font("Tahoma", Font.PLAIN, 26));

        //password
        JLabel surname = new JLabel("Surname");
        surname.setFont(new Font("Tahoma", Font.PLAIN, 26));
        snField = new JTextField(10);
        snField.setText(currentSn);
        snField.setEnabled(false);
        snField.setFont(new Font("Tahoma", Font.PLAIN, 26));

        //university
        JLabel university = new JLabel("University");
        university.setFont(new Font("Tahoma", Font.PLAIN, 26));
        uniField = new JTextField(10);
        uniField.setText(currentUni);
        uniField.setEnabled(false);
        uniField.setFont(new Font("Tahoma", Font.PLAIN, 26));


        //change button
        update = new JButton("Update");
        update.addActionListener(this);
        update.setFont(new Font("Tahoma", Font.PLAIN, 24));

        //back button
        back1 = new JButton("Back");
        back1.addActionListener(this);
        back1.setFont(new Font("Tahoma", Font.PLAIN, 24));


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
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(0, 130, 50, 180));
        //button panel
        buttonPane.setLayout(new GridBagLayout());
        GridBagConstraints buttonSpace = new GridBagConstraints();
        buttonSpace.insets = new Insets(0, 50, 0, 5);


        //add functions
        //noticePanel
        noticePanel.add(banner);
        //fieldsPanel
        fieldsPanel.add(title, left);
        fieldsPanel.add(titleField, right);
        fieldsPanel.add(forename, left);
        fieldsPanel.add(fnField, right);
        fieldsPanel.add(surname, left);
        fieldsPanel.add(snField, right);
        fieldsPanel.add(university, left);
        fieldsPanel.add(uniField, right);
        //button panel
        buttonPane.add(update, buttonSpace);
        buttonPane.add(back1, buttonSpace);

        this.add(noticePanel);
        this.add(fieldsPanel);
        this.add(buttonPane);

        // this.add(displayPanel);
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
        }
    }


    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == back | e.getSource() == back1) {
            switch (userType) {
                case 1:
                    try {
                        new EditorInterface(username);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    this.dispose();
                    break;
                case 2:
                    System.out.println("Author detected");
                    try {
                        new AuthorInterface(username);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }

                    this.dispose();
                    break;
                case 3:
                    //new ReviewerInterface(username);
                    this.dispose();
                    break;
            }
        } else if (e.getSource() == update) {
            JPanel panel = new JPanel();
            JLabel label = new JLabel("Please enter your password:");
            JPasswordField passwordField = new JPasswordField(10);
            panel.add(label);
            panel.add(passwordField);
            String[] options = new String[]{"OK", "Cancel"};
            int option = JOptionPane.showOptionDialog(null, panel, "The title",
                    JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, options, options[1]);

            if (option == 0) {
                if (passwordField != null) {
                    try {
                        String password = String.valueOf(passwordField.getPassword());
                        if (UserController.checkPassword(username, password)) {
                            this.dispose();
                            validation = true;
                            new UpdateProfileInterface(username, userType, true);
                        } else {
                            JOptionPane.showMessageDialog(null, "Sorry, your password is wrong,\n" +
                                    "please try again!");
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } else if (e.getSource() == update1) {
            System.out.println("final update button detected");
            String surname = snField.getText();
            String forename = fnField.getText();
            String university = uniField.getText();
            try {
                if (UserController.updateTitle(username, userTitle) && UserController.updateSurname(username, surname) &&
                        UserController.updateForename(username, forename) && UserController.updateUniversity(username, university)) {
                    JOptionPane.showMessageDialog(null, "You have updated profile successfully!");
                    switch (userType) {
                        case 1:
                            try {
                                new EditorInterface(username);
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                            break;
                        case 2:
                            new AuthorInterface(username);
                            break;
                        case 3:
                            //new ReviewerInterface(username);
                            break;
                    }
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Sorry, please try again!");

                }
            } catch (SQLException ex) {
                ex.printStackTrace();

            }
        }
    }
}