/**
 * Class for Author Interface
 * @author Ting Guo
 */

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private String username;
    private LinkedList<Submission> submissions;
    private Status subStatus;

    //components in reviews received panel
    private JTextArea addedPDF;
    private JButton btnAddPdf;
    private Path path = null;
    private File pdf;
    private Object AuthorInterface;


    public AuthorInterface(String username) throws SQLException {
        this.setTitle("Submission");
        this.setSize(1000, 600);
        reviewsPanel = new JPanel();
        reviewsPanel.setSize(1000,600);
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

        /*
        create = new JMenu("Create");
        createSub = new JMenuItem("Submission");
        create.add(createSub);
        create.setEnabled(false);
        menubar.add(create);
        */

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

        this.setJMenuBar(menubar);


        Integer selectedSubId = Integer.parseInt(getSelectedButtonText(group));
        Status status = getStatusByID(selectedSubId);
        System.out.println(status);

        if (status.equals(Status.SUBMITTED)){
            System.out.println("submitted condition detected");
            submittedPanel(selectedSubId);
        } else if (status.equals(Status.REVIEWS_RECEIVED)){
            reviewsReceivedPanel(selectedSubId);
        } else if (status.equals(Status.RESPONSES_RECEIVED)) {
            responsesReceivedPanel(selectedSubId);
        } else if (status.equals(Status.COMPLETED)) {
            completedPanel(selectedSubId);
        }

    //extra settings
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

    }

    public void submittedPanel(Integer id){
        JPanel subPanel = new JPanel();
        JLabel subTitle = new JLabel("Submission " + id + " is waiting for reviews.");
        subTitle.setBorder(BorderFactory.createEmptyBorder(180, 200, 50, 200));
        subTitle.setFont(new Font("Lucida Grande", Font.PLAIN, 30));
        //System.out.println(subTitle1.getFont());
        subTitle.setHorizontalAlignment(JLabel.CENTER);
        subPanel.add(subTitle);
        this.add(subPanel);


    }

    public void reviewsReceivedPanel(Integer id) throws SQLException {
        System.out.println("reviewsReceivedPanel");
        reviewsPanel = new JPanel();


        //notice panel for add answers
        JPanel noticePanel = new JPanel();
        JLabel banner = new JLabel("Add the revised version of the article after responding to all reviews!");
        banner.setFont(new Font("Lucida Grande", Font.PLAIN, 24));
        banner.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        noticePanel.add(banner);


        //pdf panel
        JPanel pdfPanel = new JPanel();
        //pdfPanel.setPreferredSize(new Dimension(20,20));
        //add PDF button and display
        addedPDF = new JTextArea(1,8);
        addedPDF.setText("Revised PDF");
        addedPDF.setAlignmentY(Component.CENTER_ALIGNMENT);
        addedPDF.setFont(new Font("Arial", Font.PLAIN, 15));
        addedPDF.setEditable (false); //set textArea non-editable

        JScrollPane pdfPane = new JScrollPane(addedPDF);
        //pdfPane.setPreferredSize(new Dimension(20,20));
        pdfPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        pdfPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        //add pdf button
        btnAddPdf = new JButton("Add PDF");
        btnAddPdf.addActionListener(this);
        btnAddPdf.setFont(new Font("Arial", Font.PLAIN, 15));

        pdfPanel.setLayout(new BoxLayout(pdfPanel,BoxLayout.X_AXIS));
        pdfPanel.setBorder(new EmptyBorder(new Insets(20, 350, 20, 350)));
        pdfPanel.add(pdfPane);
        pdfPanel.add(btnAddPdf);


        //button panel
        JPanel submitButtonPane = new JPanel();
        JButton submit = new JButton("Submit");
        submit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (ReviewController.submitResponsesAndPdf(id, pdf)){
                        JOptionPane.showMessageDialog(null, "you have respond successfully!");
                        dispose();
                        new AuthorInterface(username);
                    } else {
                        JOptionPane.showMessageDialog(null, "Sorry, please try again!");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        submit.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        //layout for submitButtonPane
        submitButtonPane.setLayout(new BoxLayout(submitButtonPane, BoxLayout.X_AXIS));
        submitButtonPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 50, 0));
        submitButtonPane.add(submit);


        reviewsPanel.setLayout(new BoxLayout(reviewsPanel, BoxLayout.Y_AXIS));
        reviewsPanel.add(review(id,1));
        reviewsPanel.add(review(id,2));
        reviewsPanel.add(review(id,3));
        reviewsPanel.add(noticePanel);
        reviewsPanel.add(pdfPanel);
        reviewsPanel.add(submitButtonPane);



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
        boolean isResponded = ReviewController.isSubmittedResponse(submissionID, anonID);
        Review review = ReviewController.getReview(submissionID, anonID);
        LinkedList<Question> questions = new LinkedList<>(ReviewController.getQuestions(submissionID, anonID));
        System.out.println("getting initial verdict of " + anonID);
        String initialVd = ReviewController.getVerdict(submissionID, anonID).toString();

        JPanel noticePanel = new JPanel();
        JLabel banner = new JLabel("Review "+ no);
        banner.setFont(new Font("Lucida Grande", Font.PLAIN, 30));
        banner.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        //Summary group
        JPanel summaryGroup = new JPanel(new BorderLayout(10, 10));
        summaryGroup.setPreferredSize(new Dimension(1000,300));
        summaryGroup.setBorder(BorderFactory.createEmptyBorder(10, 100, 20, 100));
        JLabel summary = new JLabel("Summary");
        summary.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        JTextArea sumField = new JTextArea(2,50);
        sumField.setFont(new Font("Arial", Font.PLAIN, 15));
        sumField.setText(review.getSummary());
        sumField.setEditable(false);
        sumField.setLineWrap(true);
        sumField.setWrapStyleWord(true);
        JScrollPane sumFieldPane = new JScrollPane(sumField);
        sumFieldPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sumFieldPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        summaryGroup.add(summary, BorderLayout.PAGE_START);
        summaryGroup.add(sumFieldPane, BorderLayout.CENTER);

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
        errorField.setLineWrap(true);
        errorField.setWrapStyleWord(true);
        JScrollPane errorFieldPane = new JScrollPane(errorField);
        errorFieldPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        errorFieldPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        errorsGroup.add(error, BorderLayout.PAGE_START);
        errorsGroup.add(errorFieldPane, BorderLayout.CENTER);

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
        qsField.setLineWrap(true);
        qsField.setWrapStyleWord(true);
        JScrollPane qsFieldPane = new JScrollPane(qsField);
        qsFieldPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        qsFieldPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        qsGroup.add(question, BorderLayout.PAGE_START);
        qsGroup.add(qsFieldPane, BorderLayout.CENTER);

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
        if (isResponded){
            response.setEnabled(false);
        }
        response.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Respond to " + no);
                SwingUtilities.getWindowAncestor(response).dispose();
                new RespondInterface(username, submissionID, no, questions);
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
        JPanel responseReceivedPanel = new JPanel();
        JLabel title = new JLabel("Waiting for final verdict.");
        title.setBorder(BorderFactory.createEmptyBorder(220, 200, 50, 200));
        title.setFont(new Font("Lucida Grande", Font.PLAIN, 30));
        //System.out.println(subTitle1.getFont());
        title.setHorizontalAlignment(JLabel.CENTER);
        responseReceivedPanel.add(title);
        this.add(responseReceivedPanel);
        //System.out.println("Responses Received Panel");
    }


    public void completedPanel(Integer id){
        JPanel completedPanel = new JPanel();
        JLabel title = new JLabel("Your article has completed the review process.");
        title.setBorder(BorderFactory.createEmptyBorder(180, 200, 50, 200));
        title.setFont(new Font("Lucida Grande", Font.PLAIN, 30));
        //System.out.println(subTitle1.getFont());
        title.setHorizontalAlignment(JLabel.CENTER);

        //Initial verdict
        JPanel vdGroup = new JPanel(new BorderLayout(10, 10));
        //vdGroup.setPreferredSize(new Dimension(1000,300));
        vdGroup.setBorder(BorderFactory.createEmptyBorder(10, 100, 20, 100));
        JLabel verdict = new JLabel("  Final Verdict");
        verdict.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        JTextArea vdField = new JTextArea(1,12);
        vdField.setFont(new Font("Arial", Font.PLAIN, 15));
        //vdField.setText(finalVd);
        vdField.setEditable(false);
        vdGroup.add(verdict, BorderLayout.PAGE_START);
        vdGroup.add(vdField, BorderLayout.WEST);

        completedPanel.add(title);
        completedPanel.add(vdGroup);
        this.add(completedPanel);
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
        if (e.getSource() == submissions) {
            System.out.println("Menu B clicked");
        } else if (e.getSource() == changePw) {
            new ChangePw(username, 2);
            this.dispose();
        } else if (e.getSource() == updatePf) {
            new UpdateProfileInterface(username, 2,false);
            this.dispose();
        } else if (e.getSource() == logOut) {
            this.dispose();
            UserController.logout();
            JOptionPane.showMessageDialog(null, "You have logged out successfully!");
            new LoginInterface();
        } else if (e.getSource() == btnAddPdf) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Please select your revised file");
            fileChooser.setApproveButtonText("ok");
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Pdf Files", "pdf");
            fileChooser.setFileFilter(filter);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(this)) {
                path = Paths.get(fileChooser.getSelectedFile().getPath());
            }
            addedPDF.setText(fileChooser.getSelectedFile().getPath());
            pdf = path.toFile();
        }
    }

    public static void main(String[] args) throws SQLException {
        new AuthorInterface("larsen@copenhagen.ac.uk");
    }
}
