import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * Class for Final Verdict Interface
 * @author Ting Guo
 */

public class FinalVerdictInterface extends JFrame implements ItemListener{

    private String username;
    private JPanel displayPanel, qnaPanel;
    private JPanel qsGroup, asGroup, submitButtonPane;
    private JTextArea qsArea, asArea;
    private int submissionId;
    private LinkedList<Question> questions;
    private LinkedList<Answer> answers;
    private Verdict vd;



    public FinalVerdictInterface(String username, int submissionId, String anonId) throws SQLException {
        this.setTitle("Final Verdict");
        this.setSize(1000, 600);
        this.setLocationRelativeTo(null);
        //this.setResizable(false);

        this.username = username;
        this.submissionId = submissionId;

        questions = new LinkedList<>(ReviewController.getQuestions(submissionId, anonId));
        answers = new LinkedList<>(ReviewController.getAnswers(submissionId, anonId));

        displayPanel = new JPanel();
        qnaPanel = new JPanel(new BorderLayout(10, 10));
        submitButtonPane = new JPanel();


        //List of Qs
        //questions panel settings
        qsGroup = new JPanel(new BorderLayout(10, 10));
        qsGroup.setPreferredSize(new Dimension(430,260));
        qsGroup.setBorder(BorderFactory.createEmptyBorder(60, 100, 20, 0));
        JLabel question = new JLabel("Questions");
        question.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        qsArea = new JTextArea(2,50);
        qsArea.setFont(new Font("Arial", Font.PLAIN, 15));
        JScrollPane qsPane = new JScrollPane(qsArea);
        qsPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        qsPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        for (Question qs : questions) {
            qsArea.append(qs.toString()+"\n");
        }
        qsArea.setEditable(false);
        JScrollPane qsAreaPane = new JScrollPane(qsArea);
        qsAreaPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        qsAreaPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        qsGroup.add(question, BorderLayout.PAGE_START);
        qsGroup.add(qsAreaPane, BorderLayout.CENTER);

        //answer group
        asGroup = new JPanel(new BorderLayout(10, 10));
        asGroup.setPreferredSize(new Dimension(430,260));
        asGroup.setBorder(BorderFactory.createEmptyBorder(60, 0, 20, 100));
        JLabel answer = new JLabel("Answers");
        answer.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        asArea = new JTextArea(2,50);
        asArea.setFont(new Font("Arial", Font.PLAIN, 15));
        JScrollPane asPane = new JScrollPane(asArea);
        asPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        asPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        for (Answer as : answers) {
            asArea.append(as.toString()+"\n");
        }
        asArea.setEditable(false);
        JScrollPane asAreaPane = new JScrollPane(asArea);
        asAreaPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        asAreaPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        asGroup.add(answer, BorderLayout.PAGE_START);
        asGroup.add(asAreaPane, BorderLayout.CENTER);

        qnaPanel.add(qsGroup, BorderLayout.WEST);
        qnaPanel.add(asGroup, BorderLayout.EAST);


        //Final verdict
        JPanel vdGroup = new JPanel(new BorderLayout(10, 10));
        //vdGroup.setPreferredSize(new Dimension(1000,300));
        vdGroup.setBorder(BorderFactory.createEmptyBorder(0, 340, 0, 340));
        JLabel verdict = new JLabel("Final Verdict");
        verdict.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        String[] verdicts = {"Final Verdict", "Strong Accept", "Weak Accept", "Weak Reject", "Strong Reject"};
        JComboBox<String> comboVerdicts = new JComboBox<>(verdicts);
        comboVerdicts.addItemListener(this);
        comboVerdicts.setFont(new Font("Tahoma", Font.PLAIN, 20));
        vdGroup.add(comboVerdicts, BorderLayout.EAST);
        vdGroup.add(verdict, BorderLayout.WEST);

        JButton submit = new JButton("Submit");
        submit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (ReviewController.submitFinalVerdict(submissionId, vd, anonId)){
                        SwingUtilities.getWindowAncestor(submit).dispose();
                        JOptionPane.showMessageDialog(null,"You have submitted final verdict to " +
                                submissionId + " successfully!");
                        new ReviewerInterface(username);
                        System.out.println("Final verdict to " + submissionId + " has been submitted");
                    } else {
                        JOptionPane.showMessageDialog(null,"Sorry, please try again!");
                    }
                } catch (SQLException | IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        submit.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        submitButtonPane.setLayout(new BoxLayout(submitButtonPane, BoxLayout.X_AXIS));
        submitButtonPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 50, 0));;
        submitButtonPane.add(submit);

        displayPanel.setLayout(new BoxLayout(displayPanel,BoxLayout.Y_AXIS));
        displayPanel.add(qnaPanel);
        displayPanel.add(vdGroup);
        displayPanel.add(submitButtonPane);

        this.add(displayPanel);

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

    public static void main(String[] args) throws SQLException {
        new FinalVerdictInterface("chaddock@illinois.ac.uk",2,"reviewer1");
    }

}
