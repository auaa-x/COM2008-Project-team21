/**
 * Class for Author Interface
 * @author Ting Guo
 * @author Huiqiang Liu
 */



import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class AuthorInterface extends JFrame implements ActionListener{

    public static boolean authorPwChanged = false;
    private JMenuBar menubar;
    // JMenu
    private JMenu file;
    private JMenu create;
    private JTable articlesTable;


    // Menu items
    private JMenuItem articles, submissions, createSub,changePw;

    //Articles display panel


    public AuthorInterface(){
        this.setTitle("Author");
        this.setSize(1000, 600);
        this.setLocationRelativeTo(null);
        this.setResizable(false);

        //menu bar
        menubar = new JMenuBar();

        file = new JMenu("File");
        articles = new JMenuItem("Articles");
        submissions = new JMenuItem("Submissions");
        changePw = new JMenuItem("Change Password");
        if (authorPwChanged){ changePw.setEnabled(false);} else {changePw.setEnabled(true);}


        articles.addActionListener(this);
        submissions.addActionListener(this);
        changePw.addActionListener(this);


        file.add(articles);
        file.add(submissions);
        file.add(changePw);


        create = new JMenu("Create");
        createSub = new JMenuItem("Submission");
        create.add(createSub);

        menubar.add(file);
        menubar.add(create);
        this.setJMenuBar(menubar);

        //Articles information display table
        String[][] data = {
                { "Journal of Computer Science", "xxxxxxxx", "link here",""},
                { "Journal of Software Engineering", "xxxxxxxx", "link here",""},
                { "Journal of Artificial Intelligence", "xxxxxxxx", "link here",""}
        };

        // Column Names
        String[] columnNames = { "Journal", "ISSN", "Linked PDF", "Date"};

        articlesTable = new JTable(data, columnNames);
        //articlesTable.setBounds(30, 40, 200, 300);

        // adding it to JScrollPane
        JScrollPane articlesScrollPane = new JScrollPane(articlesTable);
        this.add(articlesScrollPane);

    //extra settings
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==articles){
            System.out.println("Menu A clicked"); }
        else if(e.getSource()==submissions){
            System.out.println("Menu B clicked"); }
        else if(e.getSource()==changePw){
            this.setVisible(false);
            new ChangePw();

        }
    }

    public static void main(String args[]) {
        new AuthorInterface();
    }


}
