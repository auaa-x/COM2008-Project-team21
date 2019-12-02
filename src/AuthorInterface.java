/**
 * Class for Author Interface
 * @author Ting Guo
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.LinkedList;

public class AuthorInterface extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;
    private JMenuBar menubar;
    // JMenu
    private JMenu selectSub;
    private JRadioButtonMenuItem subItem;
    private ButtonGroup group;
    private JMenu create, settings;
    private JMenuItem createSub,changePw,updatePf,logOut;
    private JTable articlesTable;
    private String username;
    private LinkedList<Submission> submissions;
    private Status subStatus;


    private JTextField titleField;
    private JTextField fnField;
    private JTextField snField;
    private JTextField uniField;
    private JButton update, update1;
    private JButton back, back1;

    private JPanel subPanel;
    // Menu items


    //Articles display panel


    public AuthorInterface(String username) throws SQLException {
        this.setTitle("Submission");
        this.setSize(1000, 600);
        this.setLocationRelativeTo(null);
        this.setResizable(false);

        this.username = username;
        group = new ButtonGroup();
        submissions = new LinkedList<>(ArticleController.getSubmissions(username));
        System.out.println(submissions);

        //menu bar
        menubar = new JMenuBar();

        //articles = new JMenuItem("Articles");
        selectSub = new JMenu("Select Submission");
        for (int s=0; s < submissions.size(); s++){
            Submission sub = submissions.get(s);
            subItem = new JRadioButtonMenuItem(String.valueOf(sub.getSubmissionID()));
            subItem.addActionListener(this);
            group.add(subItem);
            selectSub.add(subItem);
            if (s==0){
                subItem.setSelected(true);
            }
        }
        menubar.add(selectSub);

        create = new JMenu("Create");
        createSub = new JMenuItem("Submission");
        create.add(createSub);
        create.setEnabled(false);
        menubar.add(create);

        settings = new JMenu("Settings");
        changePw = new JMenuItem("Change Password");
        updatePf = new JMenuItem("Update Profile");
        changePw.addActionListener(this);
        updatePf.addActionListener(this);
        settings.add(changePw);
        settings.add(updatePf);
        menubar.add(settings);

        logOut = new JMenuItem("Log out");
        logOut.addActionListener(this);
        menubar.add(logOut);
        //if (authorPwChanged){ changePw.setEnabled(false);} else {changePw.setEnabled(true);}

        this.setJMenuBar(menubar);

/*
        //Articles information display table
        String[][] data = {
                { "Journal of Computer Science", "xxxxxxxx", "link here",""},
                { "Journal of Software Engineering", "xxxxxxxx", "link here",""},
                { "Journal of Artificial Intelligence", "xxxxxxxx", "link here",""}
        };

        // Column Names
        String[] columnNames = { "Journal", "ISSN", "Linked PDF", "Date"};

        articlesTable = new JTable(data, columnNames);
        //articlesTable.setBounds(30, 40, 200, 300);*/

        Integer selectedSubId = Integer.parseInt(getSelectedButtonText(group));
        Status status = getStatusByID(selectedSubId);
        if (status.equals(Status.SUBMITTED)){
            System.out.println("condition detected");
            submittedPanel(selectedSubId);
        } else if (status.equals(Status.REVIEWS_RECEIVED)){
            reviewsReceivedPanel(selectedSubId);
        } else if (status.equals(Status.RESPONSES_RECEIVED)) {
            responsesReceivedPanel(selectedSubId);
        } else if (status.equals(Status.COMPLETED)) {
            completedPanel(selectedSubId);
        }

/*        // adding it to JScrollPane

        ;*/


/*        JScrollPane scrollPane = new JScrollPane();
        this.add(scrollPane);*/

    //extra settings
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

    }

    public void submittedPanel(Integer id){
        subPanel = new JPanel();
        JLabel subTitle = new JLabel("Submission: " + id + " have been received.");
        subTitle.setBorder(BorderFactory.createEmptyBorder(180, 200, 50, 200));
        JLabel subTitle1 = new JLabel("Waiting for reviews....");
        subTitle.setFont(new Font("Lucida Grande", Font.PLAIN, 30));
        subTitle1.setFont(new Font("Lucida Grande", Font.PLAIN, 30));
        //System.out.println(subTitle1.getFont());
        subTitle.setHorizontalAlignment(JLabel.CENTER);
        subPanel.add(subTitle);
        subPanel.add(subTitle1);
        this.add(subPanel);


    }

    public void reviewsReceivedPanel(Integer id){
        //TO-DO method
        //System.out.println("Reviews Received Panel");
    }


    public void responsesReceivedPanel(Integer id){
        //TO-DO method
        //System.out.println("Responses Received Panel");
    }


    public void completedPanel(Integer id){
        //TO-DO method
        //System.out.println("Completed Panel");
    }


    //get selected radio box text
    public String getSelectedButtonText(ButtonGroup buttonGroup) {
        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements(); ) {
            AbstractButton button = buttons.nextElement();

            if (button.isSelected()) {
                return button.getText();
            }
        }
        return null;
    }

    public Status getStatusByID(Integer id){
        for (Submission sub : submissions){
            if (sub.getSubmissionID()== id){
                subStatus = sub.getStatus();
            }
        }
        return subStatus;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==submissions){
            System.out.println("Menu B clicked"); }

        else if(e.getSource()==changePw){
            new ChangePw(username, 2);
            this.dispose();
        }
        else if(e.getSource()==updatePf){
            new UpdateProfileInterface(username, 2);
            this.dispose();
        }
        else if (e.getSource()==logOut) {
        	this.dispose();
        	JOptionPane.showMessageDialog(null, "You have logged out successfully!");
            new LoginInterface();
            }
        }

    public static void main(String[] args) throws SQLException {

        new AuthorInterface("chaddock@illinois.ac.uk");
    }
}
