/**
 * Class for Submit Interface
 * @author Ting Guo
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.sql.SQLException;

public class SubmitReviewInterface extends JFrame implements ActionListener, ItemListener {

    private static final long serialVersionUID = 1L;
    private JPanel reviewPanel;
    private JPanel qsGroup;
    private JPanel leftPanel, rightPanel;
    private JPanel buttonPane, submitButtonPane;
    private JTextArea sumField, errorField, qsArea, asArea, qsField;
    private JButton addQs, reset;
    private String anonID;
    private Verdict vd;


    SubmitReviewInterface(String username, int submissionId, String anonID) {
        this.setTitle("Submit a Review");
        this.setSize(1000, 600);
        this.setLocationRelativeTo(null);
        this.setResizable(false);


        this.anonID = anonID;
        //notice panel for add answers
        JPanel noticePanel = new JPanel();
        JLabel banner = new JLabel("Review");
        banner.setFont(new Font("Lucida Grande", Font.PLAIN, 30));
        banner.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));
        noticePanel.add(banner);

        //Summary group
        //set up group panel
        JPanel summaryGroup = new JPanel(new BorderLayout(10, 10));
        summaryGroup.setPreferredSize(new Dimension(1000,300));
        summaryGroup.setBorder(BorderFactory.createEmptyBorder(10, 100, 20, 100));
        //summary label
        JLabel summary = new JLabel("Summary");
        summary.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        //summary input field
        sumField = new JTextArea(2,50);
        sumField.setFont(new Font("Arial", Font.PLAIN, 15));
        //new line automatically
        sumField.setLineWrap(true);
        sumField.setWrapStyleWord(true);
        //set up scroll pane for summary input field
        JScrollPane sumFieldPane = new JScrollPane(sumField);
        sumFieldPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sumFieldPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        summaryGroup.add(summary, BorderLayout.PAGE_START);
        summaryGroup.add(sumFieldPane, BorderLayout.CENTER);

        //Errors
        //set up group panel
        JPanel errorsGroup = new JPanel(new BorderLayout(10, 10));
        errorsGroup.setPreferredSize(new Dimension(1000,300));
        errorsGroup.setBorder(BorderFactory.createEmptyBorder(10, 100, 20, 100));
        //error label
        JLabel error = new JLabel("Typographical Errors");
        error.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        //errors input field
        errorField = new JTextArea(2,50);
        errorField.setFont(new Font("Arial", Font.PLAIN, 15));
        errorField.setLineWrap(true);
        errorField.setWrapStyleWord(true);
        //scroll pane for errors input field
        JScrollPane errorFieldPane = new JScrollPane(errorField);
        errorFieldPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        errorFieldPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        errorsGroup.add(error, BorderLayout.PAGE_START);
        errorsGroup.add(errorFieldPane, BorderLayout.CENTER);

        //Initial verdict
        JPanel vdGroup = new JPanel(new BorderLayout(10, 10));
        //vdGroup.setPreferredSize(new Dimension(1000,300));
        vdGroup.setBorder(BorderFactory.createEmptyBorder(10, 340, 20, 340));
        JLabel verdict = new JLabel("Initial Verdict");
        verdict.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        String[] verdicts = {"Initial Verdict", "Strong Accept", "Weak Accept", "Weak Reject", "Strong Reject"};
        JComboBox<String> comboVerdicts = new JComboBox<>(verdicts);
        comboVerdicts.addItemListener(this);
        comboVerdicts.setFont(new Font("Tahoma", Font.PLAIN, 20));
        vdGroup.add(comboVerdicts, BorderLayout.EAST);
        vdGroup.add(verdict, BorderLayout.WEST);

        //questions panel settings
        qsGroup = new JPanel(new BorderLayout(10, 10));
        qsGroup.setPreferredSize(new Dimension(1000,300));
        qsGroup.setBorder(BorderFactory.createEmptyBorder(10, 100, 20, 100));

        leftPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setPreferredSize(new Dimension(350,300));
        rightPanel.setPreferredSize(new Dimension(350,300));
        buttonPane = new JPanel();
        submitButtonPane = new JPanel();

        //left panel
        //panel title
        JLabel question = new JLabel("Add Question");
        question.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        qsField = new JTextArea(2,10);
        qsField.setFont(new Font("Arial", Font.PLAIN, 15));
        qsField.setLineWrap(true);
        qsField.setWrapStyleWord(true);
        JScrollPane qsFieldPane = new JScrollPane(qsField);
        qsFieldPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        qsFieldPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        //add question button
        addQs = new JButton("Add Question");
        addQs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String qs = qsField.getText();
                if (!qs.trim().isEmpty()) {
                    ReviewController.addQuestion(qs);
                    qsArea.append(qs + "\n");
                    qsField.setText("");
                    System.out.println(ReviewController.getQuestionList());
                } else {
                    JOptionPane.showMessageDialog(null,"Please enter your question!");
                }
            }
        });
        addQs.setFont(new Font("Tahoma", Font.PLAIN, 20));
        //reset answer button
        reset = new JButton("Reset");
        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ReviewController.clearQuestionList();
                qsField.setText("");
                qsArea.setText("");
            }
        });
        reset.setFont(new Font("Tahoma", Font.PLAIN, 20));

        //right panel
        //title
        JLabel questions = new JLabel("List of Questions");
        questions.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        //answers display area
        qsArea= new JTextArea(2,10);
        qsArea.setEditable (false);
        qsArea.setFont(new Font("Arial", Font.PLAIN, 15));
        JScrollPane qsPane = new JScrollPane(qsArea);
        qsPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        qsPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        JButton submit = new JButton("Submit");
        submit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String sum = sumField.getText();
                    String typos = errorField.getText();
                    if (ReviewController.submitReview(submissionId, anonID, sum, typos, vd)){
                        SwingUtilities.getWindowAncestor(submit).dispose();
                        JOptionPane.showMessageDialog(null,"You have reviewed " + ArticleController.getArticle(submissionId).getTitle() +
                                " successfully!");
                        new ReviewerInterface(username);
                        System.out.println("Review to " + submissionId + " has been submitted");
                    } else {
                        JOptionPane.showMessageDialog(null,"Sorry, please try again!");
                    }
                } catch (SQLException | IOException ex) {
                    ex.printStackTrace();
                }

            }
        });
        submit.setFont(new Font("Lucida Grande", Font.PLAIN, 20));

        //layout
        //layout for buttonPane - addAnswer & reset
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
        buttonPane.add(addQs);
        buttonPane.add(reset);
        //layout for submitButtonPane
        submitButtonPane.setLayout(new BoxLayout(submitButtonPane, BoxLayout.X_AXIS));
        submitButtonPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 50, 0));
        submitButtonPane.add(submit);
        //layout for leftPanel - answer title  & asFieldPane & buttonPane
        leftPanel.add(question, BorderLayout.NORTH);
        leftPanel.add(qsFieldPane, BorderLayout.CENTER);
        leftPanel.add(buttonPane, BorderLayout.SOUTH);
        //layout for right - answers title & answersPane & submitButtonPane
        rightPanel.add(questions, BorderLayout.NORTH);
        rightPanel.add(qsPane, BorderLayout.CENTER);
        //layout for asGroup - leftPanel & rightPanel
        qsGroup.add(leftPanel, BorderLayout.WEST);
        qsGroup.add(rightPanel, BorderLayout.EAST);

        reviewPanel = new JPanel();
        reviewPanel.setLayout(new BoxLayout(reviewPanel,BoxLayout.Y_AXIS));
        reviewPanel.add(noticePanel);
        reviewPanel.add(summaryGroup);
        reviewPanel.add(errorsGroup);
        reviewPanel.add(qsGroup);
        reviewPanel.add(vdGroup);
        reviewPanel.add(submitButtonPane);

        JScrollPane scrollPane = new JScrollPane(reviewPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(scrollPane);

        //extra settings
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        String v = (String) e.getItem();
        switch (v) {
            case "Strong Accept":
                vd = Verdict.STRONG_ACCEPT;
                break;
            case "Weak Accept":
                vd = Verdict.WEAK_ACCEPT;
                break;
            case "Weak Reject":
                vd = Verdict.WEAK_REJECT;
                break;
            case "Strong Reject":
                vd = Verdict.STRONG_REJECT;
                break;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    public static void main(String[] args) throws SQLException {
        new SubmitReviewInterface("chaddock@illinois.ac.uk",2, "reviewer1");
    }
}