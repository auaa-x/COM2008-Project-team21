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
	private JPanel reviewsPanel;
    private JPanel reviewPanel;
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
    //Articles display panel


    public AuthorInterface(String username) throws SQLException {
        this.setTitle("Submission");
        this.setSize(1000, 600);
        reviewsPanel = new JPanel();
        reviewsPanel.setSize(1000,600);
        this.setLocationRelativeTo(null);
        //this.setResizable(false);

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
        //Status status = getStatusByID(selectedSubId);
        int id = 1;
        Status status = Status.REVIEWS_RECEIVED;
        if (status.equals(Status.SUBMITTED)){
            System.out.println("condition detected");
            submittedPanel(selectedSubId);
        } else if (status.equals(Status.REVIEWS_RECEIVED)){
            reviewsReceivedPanel(id);
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
        JPanel subPanel = new JPanel();
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

    public void reviewsReceivedPanel(Integer id) throws SQLException {
        System.out.println("reviewsReceivedPanel");
        reviewsPanel.add(review(id,1));
        //reviewsPanel.add(review(id,2));
        //reviewsPanel.add(review(id,3));
        JScrollPane scrollPane = new JScrollPane(reviewsPanel);
        scrollPane.setPreferredSize(new Dimension(1000,600));
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(scrollPane);
    }

    public JPanel review(int submissionID, int no) throws SQLException {
        System.out.println("review " + no);
        reviewPanel = new JPanel();
        String anonID = null;
        switch (no) {
            case 1:
                anonID = "reviewer1";
                break;
            case 2:
                anonID = "reviewer2";
                break;
            case 3:
                anonID = "reviewer3";
                break;
        }
        Review review = ReviewController.getReview(submissionID, anonID);
        LinkedList<Question> questions = new LinkedList<>(ReviewController.getQuestions(submissionID, anonID));
        String initialVd = ReviewController.getVerdict(submissionID,anonID).toString();
        JPanel noticePanel = new JPanel();
        JLabel banner = new JLabel("Review 1");
        banner.setFont(new Font("Lucida Grande", Font.PLAIN, 30));
        banner.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        //Personal details
        JPanel summaryGroup = new JPanel(new BorderLayout(10, 10));
        summaryGroup.setPreferredSize(new Dimension(1000,300));
        summaryGroup.setBorder(BorderFactory.createEmptyBorder(10, 100, 20, 100));
        JLabel summary = new JLabel("Summary");
        summary.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        JTextArea sumField = new JTextArea(2,50);
        sumField.setFont(new Font("Arial", Font.PLAIN, 15));
        sumField.setText(review.getSummary());
        sumField.setEditable(false);
        summaryGroup.add(summary, BorderLayout.PAGE_START);
        summaryGroup.add(sumField, BorderLayout.CENTER);

        //Errors
        JPanel errorsGroup = new JPanel(new BorderLayout(10, 10));
        errorsGroup.setPreferredSize(new Dimension(1000,300));
        errorsGroup.setBorder(BorderFactory.createEmptyBorder(10, 100, 20, 100));
        JLabel error = new JLabel("Errors");
        error.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        JTextArea errorField = new JTextArea(2,50);
        errorField.setFont(new Font("Arial", Font.PLAIN, 15));
        errorField.setText(review.getTypoErrors());
        errorField.setEditable(false);
        errorsGroup.add(error, BorderLayout.PAGE_START);
        errorsGroup.add(errorField, BorderLayout.CENTER);

        //List of Qs
        JPanel qsGroup = new JPanel(new BorderLayout(10, 10));
        qsGroup.setPreferredSize(new Dimension(1000,300));
        qsGroup.setBorder(BorderFactory.createEmptyBorder(10, 100, 20, 100));
        JLabel question = new JLabel("List of Questions");
        question.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        JTextArea qsField = new JTextArea(2,50);
        qsField.setFont(new Font("Arial", Font.PLAIN, 15));
        for (Question qs : questions) {
            qsField.append(qs.toString()+"\n");
        }
        qsField.setEditable(false);
        qsGroup.add(question, BorderLayout.PAGE_START);
        qsGroup.add(qsField, BorderLayout.CENTER);

        //Initial verdict
        JPanel vdGroup = new JPanel(new BorderLayout(10, 10));
        //vdGroup.setPreferredSize(new Dimension(1000,300));
        vdGroup.setBorder(BorderFactory.createEmptyBorder(10, 100, 20, 100));
        JLabel verdict = new JLabel("Initial Verdict");
        verdict.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        JTextArea vdField = new JTextArea(1,12);
        vdField.setFont(new Font("Arial", Font.PLAIN, 15));
        vdField.setText(initialVd);
        vdField.setEditable(false);
        vdGroup.add(verdict, BorderLayout.PAGE_START);
        vdGroup.add(vdField, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 100));
        JButton response = new JButton("Create a Response");
        response.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                System.out.println("Respond to " + no);
                new RespondInterface(no, questions);
            }
        });
        response.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        buttonPanel.add(response, BorderLayout.EAST);

        noticePanel.setLayout(new BoxLayout(noticePanel,BoxLayout.LINE_AXIS));
        noticePanel.add(banner);

        reviewPanel.setLayout(new BoxLayout(reviewPanel,BoxLayout.Y_AXIS));
        reviewPanel.add(noticePanel);
        reviewPanel.add(summaryGroup);
        reviewPanel.add(errorsGroup);
        reviewPanel.add(qsGroup);
        reviewPanel.add(vdGroup);
        reviewPanel.add(buttonPanel);

        return reviewPanel;
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
