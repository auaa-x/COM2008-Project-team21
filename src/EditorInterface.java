
/**
 * Class for Editor Interface
 * @author Ting Guo
 */

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.LinkedList;


public class EditorInterface extends JFrame implements ActionListener {
    private JMenuBar menuBar;
    private JRadioButtonMenuItem journalItem, journalItem1;
    private JMenu journalSelection;
    private ButtonGroup group;
    private JMenu settings;
    private JMenuItem retire, changePw, toChiefEditor, logOut;
    private File article = new File("./article.pdf");
    private Desktop desktop = Desktop.getDesktop();

    private JTree tree;
    private JLabel selectedLabel;
    private Journal selectedJournal;
    private JScrollPane treeScrollPane;
    private JPanel treePanel;

    private JPanel infoPanel, verdictPanel;
    private JLabel infoTitle;
    private JButton open;
    private String username;
    private LinkedList<Integer> journalsISSN;
    private LinkedList<Journal> journals;
    private int issn;


    EditorInterface(String username) throws SQLException {
        this.setTitle("Editor Interface");
        this.setSize(1000, 600);
        this.setLocationRelativeTo(null);
        this.setResizable(false);

        //set up panels
        infoPanel = new JPanel();
        treePanel = new JPanel();
        verdictPanel = new JPanel();


        this.username = username;
        journalsISSN = new LinkedList<Integer>();
        journalsISSN = JournalController.getEditorJournals(username);
        journals = new LinkedList<Journal>();

        //create the menu
        menuBar = new JMenuBar();

        //journal selection
        //menu.addSeparator();
        journalSelection = new JMenu("Select Journal" );
        group = new ButtonGroup();
        for (int j=0; j<journalsISSN.size(); ++j){
            issn = journalsISSN.get(j);
            Journal journal = JournalController.getJournal(issn);
            journalItem = new JRadioButtonMenuItem(journal.getTitle());
            journals.add(journal);
            journalItem.addActionListener(this);
            group.add(journalItem);
            if (j==0){
                journalItem.setSelected(true);
            }
            journalSelection.add(journalItem);
        }

        menuBar.add(journalSelection);

        //settings:
        settings = new JMenu("Settings");
        changePw = new JMenuItem("Change Password");
        toChiefEditor = new JMenuItem("Chief Editor Option");
        retire = new JMenuItem("Retire");

        changePw.addActionListener(this);
        toChiefEditor.addActionListener(this);
        //if ( !UserController.isChiefEditor(username, issn )){ toChiefEditor.setEnabled(false);} else {toChiefEditor.setEnabled(true);}
        retire.addActionListener(this);
        settings.add(changePw);
        settings.add(toChiefEditor);
        settings.add(retire);
        menuBar.add(settings);

        logOut = new JMenuItem("Log out");
        logOut.addActionListener(this);
        menuBar.add(logOut);

        this.setJMenuBar(menuBar);



        //create the tree panel
        JPanel treePanel = new JPanel();
        //create the root node
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Journal Publish System");

        for (Journal journal : journals) {
            DefaultMutableTreeNode journal1 = new DefaultMutableTreeNode(journal.getTitle());
            root.add(journal1);

            for (Volume volume : JournalController.getVolumes(journal.getIssn())) {
                DefaultMutableTreeNode volume1 = new DefaultMutableTreeNode("vol. " + volume.getVolNum());
                journal1.add(volume1);
                /*for (int e=0; e<department.getSections().size(); ++k)
                {
                    Section section = department.getSections().get(k);
                    DefaultMutableTreeNode section1 = new DefaultMutableTreeNode(section.getName());
                    department1.add(section1);
                    for (int m=0; m<section.getProducts().size(); ++m)
                    {
                        Product product = section.getProducts().get(m);
                        DefaultMutableTreeNode product1 = new DefaultMutableTreeNode(product.getId());
                        section1.add(product1);
                        DefaultMutableTreeNode product1_amount = new DefaultMutableTreeNode(product.getAmount());
                        product1.add(product1_amount);
                    }
                }*/
            }
        }

        //create the tree by passing in the root node
        tree = new JTree(root);

        tree.setShowsRootHandles(true);
        tree.setRootVisible(false);
        treeScrollPane = new JScrollPane(tree);
        treeScrollPane.setPreferredSize(new Dimension(250, 527));

        selectedLabel = new JLabel();
        add(selectedLabel, BorderLayout.SOUTH);
        tree.getSelectionModel().addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            selectedLabel.setText(selectedNode.getUserObject().toString());
            if (selectedNode.getUserObject().toString().equals("Article")){
                //first check if Desktop is supported by Platform or not
                if(!Desktop.isDesktopSupported()){
                    JOptionPane.showMessageDialog(null, "Desktop does not support this function");
                    return;
                } else if (article.exists()){
                    try {
                        desktop.open(article);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        //set up the article information panel
        infoPanel.setPreferredSize(new Dimension(725, 525));


        //title of the information panel
        infoTitle = new JLabel("Article Information Display Here");
        infoTitle.setFont(new Font("Tahoma", Font.PLAIN, 20));
        //open button
        open = new JButton("Open");
        open.addActionListener(this);
        open.setFont(new Font("Tahoma", Font.PLAIN, 15));

        treePanel.add(treeScrollPane);
        infoPanel.add(infoTitle);



        //add panels functions
        this.add(treePanel, BorderLayout.WEST);
        this.add(infoPanel, BorderLayout.EAST);
        this.setJMenuBar(menuBar);




        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
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

    @Override
    public void actionPerformed(ActionEvent e) {

        //register, appoint, passChiefEditor, retire, publish, delay;
        //log out
        if (e.getSource() == logOut) {
            this.dispose();
            JOptionPane.showMessageDialog(null, "You have logged out successfully!");
            new LoginInterface();
        }
        else if (e.getSource() == toChiefEditor) {
            dispose();
            try {
                new ChiefEditorInterface(username);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        //retire
        else if (e.getSource() == retire){
            String[] options = {"Yes", "No"};
            String issn1 = String.valueOf(getSelectedButtonText(group));
            int x = JOptionPane.showOptionDialog(null, "Are you sure you want to retire from "
                            + issn1, "Retire", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[0]);
            if (x == 0){
                try {
                    JournalController.editorRetire(username, Integer.parseInt(issn1));
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,"Cannot connect to the server, please try later.");
                    ex.printStackTrace();
                }
                try {
                    new EditorInterface(username);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            //select which journal editor will retire from
           /* JComboBox journalSelection = new JComboBox(journals.toArray());
            JOptionPane.showMessageDialog( null, journalSelection, "please select a journal", JOptionPane.QUESTION_MESSAGE);
            Integer selection = (Integer) journalSelection.getSelectedItem();
            try {
                if (JournalController.editorRetire(username, selection)){
                    JOptionPane.showMessageDialog(null,"You have retired from "+ selection +" successfully!");
                } else {
                    JOptionPane.showMessageDialog(null,"Please try again!");
                }
            } catch (SQLException ex) {

                ex.printStackTrace();
            }*/
        }
        else if (e.getSource() == changePw) {
            this.dispose();
            new ChangePw(username, 1);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new EditorInterface("Editor");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Can not connect to the  server, please try again.");
                e.printStackTrace();
            }
        });
    }

}
