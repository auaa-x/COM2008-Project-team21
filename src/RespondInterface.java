/**
 * Class for Author Interface
 * @author Ting Guo
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

public class RespondInterface extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private JPanel respondPanel;



    RespondInterface(int no, LinkedList<Question> questions){
        this.setTitle("Respond Review "+ no);
        this.setSize(1000, 600);
        this.setLocationRelativeTo(null);

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

        respondPanel = new JPanel();
        respondPanel.setLayout(new BoxLayout(respondPanel,BoxLayout.Y_AXIS));
        respondPanel.add(qsGroup);

        this.add(respondPanel);

        //extra settings
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}