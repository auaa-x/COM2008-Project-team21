/**
 * Class for Respond Interface
 * @author Ting Guo
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.LinkedList;

public class RespondInterface extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private JPanel respondPanel;
    private JPanel qsGroup, asGroup;
    private JPanel leftPanel, rightPanel;
    private JPanel buttonPane, submitButtonPane;
    private JTextArea qsArea, asArea, asField;
    private JButton addAnswer, reset;
    private String anonID;


    RespondInterface(String username, int submissionId, int no, LinkedList<Question> questions) {
        this.setTitle("Respond Review "+ no);
        this.setSize(1000, 600);
        this.setLocationRelativeTo(null);

        //List of Qs
        //questions panel settings
        qsGroup = new JPanel(new BorderLayout(10, 10));
        qsGroup.setPreferredSize(new Dimension(1000,230));
        qsGroup.setBorder(BorderFactory.createEmptyBorder(10, 100, 20, 100));
        JLabel question = new JLabel("List of Questions");
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


        //notice panel for add answers
        JPanel noticePanel = new JPanel();
        JLabel banner = new JLabel("Please add your answers in order so they correspond to the questions!");
        banner.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        banner.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        noticePanel.add(banner);

        //answers panel settings
        asGroup = new JPanel(new BorderLayout(10, 10));
        asGroup.setPreferredSize(new Dimension(1000,300));
        asGroup.setBorder(BorderFactory.createEmptyBorder(10, 100, 20, 100));

        leftPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setPreferredSize(new Dimension(350,300));
        rightPanel.setPreferredSize(new Dimension(350,300));
        buttonPane = new JPanel();
        submitButtonPane = new JPanel();

        //left panel
        //panel title
        JLabel answer = new JLabel("Add Answer");
        answer.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        asField = new JTextArea(2,10);
        asField.setFont(new Font("Arial", Font.PLAIN, 15));
        asField.setLineWrap(true);
        asField.setWrapStyleWord(true);
        JScrollPane asFieldPane = new JScrollPane(asField);
        asFieldPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        asFieldPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        //add answer button
        addAnswer = new JButton("Add Answer");
        addAnswer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String ans = asField.getText();
                if (!ans.trim().isEmpty()) {
                    ReviewController.addAnswer(ans);
                    asArea.append(ans + "\n");
                    asField.setText("");
                    System.out.println(ReviewController.getAnswerList());
                } else {
                    JOptionPane.showMessageDialog(null,"Please enter your answer!");
                }
            }
        });
        addAnswer.setFont(new Font("Tahoma", Font.PLAIN, 20));
        //reset answer button
        reset = new JButton("Reset");
        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ReviewController.clearAnswerList();
                asArea.setText("");
            }
        });
        reset.setFont(new Font("Tahoma", Font.PLAIN, 20));

        //right panel
        //title
        JLabel answers = new JLabel("List of Answers");
        answers.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        //answers display area
        asArea= new JTextArea(2,10);
        asArea.setEditable (false);
        asArea.setFont(new Font("Arial", Font.PLAIN, 15));
        JScrollPane answersPane = new JScrollPane(asArea);
        answersPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        answersPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        JButton submit = new JButton("Submit");
        submit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
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

                try {
                    if (ReviewController.createResponse(submissionId, anonID)){
                        SwingUtilities.getWindowAncestor(submit).dispose();
                        JOptionPane.showMessageDialog(null,"You have respond to " + anonID +
                                " successfully!");
                        new AuthorInterface(username);
                    } else {
                        JOptionPane.showMessageDialog(null,"Sorry, please try again!");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                System.out.println("Respond to " + no + "has been submitted");
            }
        });
        submit.setFont(new Font("Lucida Grande", Font.PLAIN, 20));


        //layout
        //layout for buttonPane - addAnswer & reset
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
        buttonPane.add(addAnswer);
        buttonPane.add(reset);
        //layout for submitButtonPane
        submitButtonPane.setLayout(new BoxLayout(submitButtonPane, BoxLayout.X_AXIS));
        submitButtonPane.add(submit);
        //layout for leftPanel - answer title  & asFieldPane & buttonPane
        leftPanel.add(answer, BorderLayout.NORTH);
        leftPanel.add(asFieldPane, BorderLayout.CENTER);
        leftPanel.add(buttonPane, BorderLayout.SOUTH);
        //layout for right - answers title & answersPane & submitButtonPane
        rightPanel.add(answers, BorderLayout.NORTH);
        rightPanel.add(answersPane, BorderLayout.CENTER);
        rightPanel.add(submitButtonPane, BorderLayout.SOUTH);
        //layout for asGroup - leftPanel & rightPanel
        asGroup.add(noticePanel, BorderLayout.NORTH);
        asGroup.add(leftPanel, BorderLayout.WEST);
        asGroup.add(rightPanel, BorderLayout.EAST);



        respondPanel = new JPanel();
        respondPanel.setLayout(new BoxLayout(respondPanel,BoxLayout.Y_AXIS));
        respondPanel.add(qsGroup);
        respondPanel.add(asGroup);


        JScrollPane scrollPane = new JScrollPane(respondPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(scrollPane);

        //extra settings
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    public static void main(String[] args) throws SQLException {
        //new RespondInterface(1,);
    }
}