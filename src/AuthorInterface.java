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
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.LinkedList;

public class AuthorInterface extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;
    private JPanel authorPanel;
    private CardLayout cardLayout = new CardLayout();
    private JPanel reviewsPanel;
    private JPanel reviewPanel;
    private JMenuBar menubar;

    // JMenu
    private JMenu selectSub;
    private JRadioButtonMenuItem subItem;
    private ButtonGroup group;
    private JMenu  settings;
    private JMenuItem changePw,updatePf,logOut;
    private String username;
    private LinkedList<Submission> submissions;
    private Status subStatus;

    //components in reviews received panel
    private JTextArea addedPDF;
    private JButton btnAddPdf;
    private Path path = null;
    private File pdf;

    //components in submitted panel
    private JPanel buttonPanel;
    private JButton open;
    private Desktop desktop = Desktop.getDesktop();

    //components in completed panel
    private JPanel buttonPanel1;
    private JButton open1;



    public AuthorInterface(String username) throws SQLException {
        this.setTitle("Submission");
        this.setSize(1000, 600);
        this.setLocationRelativeTo(null);
        this.setResizable(false);

        this.username = username;
        group = new ButtonGroup();
        submissions = new LinkedList<>(ArticleController.getSubmissions(username));

        //menu bar
        menubar = new JMenuBar();
        selectSub = new JMenu("Select Submission");
        //action listener for radio button in selecting submission menu
        ActionListener listener = actionEvent -> {
            AbstractButton aButton = (AbstractButton) actionEvent.getSource();
            String s = aButton.getText();
            Integer id = Integer.parseInt(aButton.getText());
            Status status = getStatusByID(id);
            try {
                if (status.equals(Status.SUBMITTED)) {
                    System.out.println("submitted condition detected");
                    authorPanel.add(submittedPanel(id), s);
                    cardLayout.show(authorPanel, s);
                } else if (status.equals(Status.REVIEWS_RECEIVED)) {
                        authorPanel.add(reviewsReceivedPanel(id), s);
                    cardLayout.show(authorPanel, s);
                } else if (status.equals(Status.RESPONSES_RECEIVED)) {
                    authorPanel.add(responsesReceivedPanel(id), s);
                    cardLayout.show(authorPanel, s);
                } else if (status.equals(Status.COMPLETED)) {
                    authorPanel.add(completedPanel(id), s);
                    cardLayout.show(authorPanel, s);
                } else if (status.equals(Status.FINAL_VERDICTS_RECEIVED)) {
                    authorPanel.add(finalPanel(id), s);
                    cardLayout.show(authorPanel, s);
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }

        };
        //add radio button menu item
        for (int s=0; s < submissions.size(); s++){
            Submission sub = submissions.get(s);
            subItem = new JRadioButtonMenuItem(String.valueOf(sub.getSubmissionID()));
            subItem.addActionListener(listener);
            group.add(subItem);
            selectSub.add(subItem);
            if (s==0){
                subItem.setSelected(true);
            }
        }
        menubar.add(selectSub);

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

        //get selected id of submissions list
        String selected = getSelectedButtonText(group);
        Integer selectedSubId = Integer.parseInt(selected);
        Status status = getStatusByID(selectedSubId);
        System.out.println(status);

        //setup author panel
        authorPanel = new JPanel();
        authorPanel.setLayout(cardLayout);
        //switch panel by status
        if (status.equals(Status.SUBMITTED)){
            authorPanel.add(submittedPanel(selectedSubId), selected);
            cardLayout.show(authorPanel, selected);
        } else if (status.equals(Status.REVIEWS_RECEIVED)){
            authorPanel.add(reviewsReceivedPanel(selectedSubId), selected);
            cardLayout.show(authorPanel, selected);
        } else if (status.equals(Status.RESPONSES_RECEIVED)) {
            authorPanel.add(responsesReceivedPanel(selectedSubId), selected);
            cardLayout.show(authorPanel, selected);
        } else if (status.equals(Status.COMPLETED)) {
            authorPanel.add(completedPanel(selectedSubId), selected);
            cardLayout.show(authorPanel, selected);
        } else if ( status.equals(Status.FINAL_VERDICTS_RECEIVED)) {
            authorPanel.add(finalPanel(selectedSubId), selected);
            cardLayout.show(authorPanel, selected);
        }
        this.add(authorPanel);
    //extra settings
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

    }

    public JPanel submittedPanel(Integer id){
        JPanel subPanel = new JPanel();
        JLabel subTitle = new JLabel("Submission " + id + " is waiting for reviews.");
        subTitle.setBorder(BorderFactory.createEmptyBorder(190, 200, 50, 200));
        subTitle.setFont(new Font("Lucida Grande", Font.PLAIN, 30));
        subTitle.setHorizontalAlignment(JLabel.CENTER);

        buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 100, 20, 100));
        open = new JButton("View Submission PDF");
        open.addActionListener((new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openSubPdf(id);
            }
        }));
        open.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        buttonPanel.add(open, BorderLayout.CENTER);
        subPanel.add(subTitle);
        subPanel.add(buttonPanel);
        return subPanel;
    }

    public JScrollPane reviewsReceivedPanel(Integer id) throws SQLException {
        reviewsPanel = new JPanel();

        //notice panel for add answers
        JPanel noticePanel = new JPanel();
        JLabel banner = new JLabel("Add the revised version of the article after responding to all reviews!");
        banner.setFont(new Font("Lucida Grande", Font.PLAIN, 24));
        banner.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        noticePanel.add(banner);

        //pdf panel
        JPanel pdfPanel = new JPanel();
        //add PDF button and display
        addedPDF = new JTextArea(1,8);
        addedPDF.setText("Revised PDF");
        addedPDF.setAlignmentY(Component.CENTER_ALIGNMENT);
        addedPDF.setFont(new Font("Arial", Font.PLAIN, 15));
        addedPDF.setEditable (false); //set textArea non-editable

        JScrollPane pdfPane = new JScrollPane(addedPDF);
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
        //only main author can submit responds
        if (!ArticleController.getArticle(id).getMAuthorEmail().equals(username)) {submit.setEnabled(false);}
            submit.addActionListener(e -> {
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

        return scrollPane;
    }

    //review panel for review + no.
    private JPanel review(int submissionID, int no) throws SQLException {
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
        //get values from db
        boolean isResponded = ReviewController.isSubmittedResponse(submissionID, anonID);
        Review review = ReviewController.getReview(submissionID, anonID);
        LinkedList<Question> questions = new LinkedList<>(ReviewController.getQuestions(submissionID, anonID));
        String initialVd = ReviewController.getVerdict(submissionID, anonID).toString();

        //notice panel
        JPanel noticePanel = new JPanel();
        JLabel banner = new JLabel("Review "+ no);
        banner.setFont(new Font("Lucida Grande", Font.PLAIN, 30));
        banner.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        //summary group
        JPanel summaryGroup = new JPanel(new BorderLayout(10, 10));
        summaryGroup.setPreferredSize(new Dimension(1000,300));
        summaryGroup.setBorder(BorderFactory.createEmptyBorder(10, 100, 20, 100));
        //label
        JLabel summary = new JLabel("Summary");
        summary.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        //text area
        JTextArea sumField = new JTextArea(2,50);
        sumField.setFont(new Font("Arial", Font.PLAIN, 15));
        sumField.setText(review.getSummary());
        sumField.setEditable(false);
        sumField.setLineWrap(true);
        sumField.setWrapStyleWord(true);
        //scroll pane
        JScrollPane sumFieldPane = new JScrollPane(sumField);
        sumFieldPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sumFieldPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        summaryGroup.add(summary, BorderLayout.PAGE_START);
        summaryGroup.add(sumFieldPane, BorderLayout.CENTER);

        //errors
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
        //only main author can submit responds
        if (!ArticleController.getArticle(submissionID).getMAuthorEmail().equals(username)) {
            response.setEnabled(false); }
        //if already responded then disable
        if (isResponded){
            response.setEnabled(false); }
        response.addActionListener(e -> {
            System.out.println("Respond to " + no);
            SwingUtilities.getWindowAncestor(response).dispose();
            new RespondInterface(username, submissionID, no, questions);
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


    private JPanel responsesReceivedPanel(Integer id){
        JPanel responseReceivedPanel = new JPanel();

        JLabel title = new JLabel("Waiting for final verdict.");
        title.setBorder(BorderFactory.createEmptyBorder(190, 200, 50, 200));
        title.setFont(new Font("Lucida Grande", Font.PLAIN, 30));
        title.setHorizontalAlignment(JLabel.CENTER);


        buttonPanel1 = new JPanel();
        buttonPanel1.setBorder(BorderFactory.createEmptyBorder(0, 100, 20, 100));
        //button to open submission pdf
        open = new JButton("View Submission PDF");
        open.addActionListener((e -> {
            openSubPdf(id);
        }));
        open.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        //button to open final pdf
        open1 = new JButton("View Final PDF");
        open1.addActionListener((e -> {
            openFinalPdf(id);
        }));
        open1.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        buttonPanel1.setLayout(new BoxLayout(buttonPanel1,BoxLayout.X_AXIS));
        buttonPanel1.add(open);
        buttonPanel1.add(open1);

        responseReceivedPanel.add(title);
        responseReceivedPanel.add(buttonPanel1);
        return responseReceivedPanel;
    }


    public JPanel completedPanel(Integer id){
        JPanel completedPanel = new JPanel();

        JLabel title = new JLabel("Your article has completed the review process.");
        title.setBorder(BorderFactory.createEmptyBorder(190, 200, 30, 200));
        title.setFont(new Font("Lucida Grande", Font.PLAIN, 30));
        title.setHorizontalAlignment(JLabel.CENTER);

        //Initial verdict
        JPanel vdGroup = new JPanel(new BorderLayout(10, 10));
        //vdGroup.setPreferredSize(new Dimension(1000,300));
        vdGroup.setBorder(BorderFactory.createEmptyBorder(10, 300, 20, 300));
        JLabel verdict = new JLabel("  Final Verdict");
        verdict.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        JTextArea vdField = new JTextArea(1,12);
        vdField.setFont(new Font("Arial", Font.PLAIN, 15));
        //vdField.setText(finalVd);
        vdField.setEditable(false);
        vdGroup.add(verdict, BorderLayout.PAGE_START);
        vdGroup.add(vdField, BorderLayout.WEST);

        buttonPanel1 = new JPanel();
        buttonPanel1.setBorder(BorderFactory.createEmptyBorder(0, 100, 20, 100));
        //button to open submission pdf
        open = new JButton("View Submission PDF");
        open.addActionListener((e -> {
            openSubPdf(id);
        }));
        open.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        //button to open final pdf
        open1 = new JButton("View Final PDF");
        open1.addActionListener((e -> {
            openFinalPdf(id);
        }));
        open1.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        buttonPanel1.setLayout(new BoxLayout(buttonPanel1,BoxLayout.X_AXIS));
        buttonPanel1.add(open);
        buttonPanel1.add(open1);

        completedPanel.add(title);
        completedPanel.add(vdGroup);
        completedPanel.add(buttonPanel1);

        return completedPanel;
    }

    private JPanel finalPanel(Integer id) throws SQLException {
        JPanel finalPanel = new JPanel();

        JLabel title = new JLabel("Your article has got final verdicts: ");
        title.setBorder(BorderFactory.createEmptyBorder(190, 200, 30, 200));
        title.setFont(new Font("Lucida Grande", Font.PLAIN, 30));
        title.setHorizontalAlignment(JLabel.CENTER);

        //Initial verdict
        JPanel vdGroup = new JPanel();
        vdGroup.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        LinkedList<Verdict> finalVerdicts = JournalController.getFinalVerdicts(id);
        vdGroup.setLayout(new BoxLayout(vdGroup, BoxLayout.X_AXIS));
        for (Verdict vd : finalVerdicts) {
            JLabel vd1 = new JLabel(vd.toString()+ "    ");
            vdGroup.add(vd1);
            vd1.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        }

        buttonPanel1 = new JPanel();
        buttonPanel1.setBorder(BorderFactory.createEmptyBorder(0, 100, 20, 100));
        //button to open submission pdf
        open = new JButton("View Submission PDF");
        open.addActionListener((e ->
                openSubPdf(id)));
        open.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        //button to open final pdf
        open1 = new JButton("View Final PDF");
        open1.addActionListener((e ->
                openFinalPdf(id)));
        open1.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        buttonPanel1.setLayout(new BoxLayout(buttonPanel1,BoxLayout.X_AXIS));
        buttonPanel1.add(open);
        buttonPanel1.add(open1);

        finalPanel.add(title);
        finalPanel.add(vdGroup);
        finalPanel.add(buttonPanel1);

        return finalPanel;
    }
    public void openSubPdf(int submissionId) {
        try {
            ArticleController.getSubmissionPDF(submissionId);
            File article = new File("article.pdf");
            if (!Desktop.isDesktopSupported()) {
                JOptionPane.showMessageDialog(null, "Desktop does not support this function");
            } else if (article.exists()) {
                desktop.open(article);
            }
        } catch (IOException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void openFinalPdf(int submissionId) {
        try {
            ArticleController.getArticlePDF(submissionId);
            File article = new File("article.pdf");
            if (!Desktop.isDesktopSupported()) {
                JOptionPane.showMessageDialog(null, "Desktop does not support this function");
            } else if (article.exists()) {
                desktop.open(article);
            }
        } catch (IOException | SQLException ex) {
            ex.printStackTrace();
        }
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
        if (e.getSource() == changePw) {
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
}
