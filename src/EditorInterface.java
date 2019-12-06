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
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.LinkedList;


public class EditorInterface extends JFrame implements ActionListener {
    private JMenuBar menuBar;
    private JRadioButtonMenuItem journalItem, journalItem1;
    private JMenu selectJournal;
    private ButtonGroup group;
    private JMenu settings;
    private JMenuItem retire, changePw,updatePf, toChiefEditor, logOut;
    private Desktop desktop = Desktop.getDesktop();

    private JTree tree;
    private JLabel selectedLabel;
    private JScrollPane treeScrollPane;
    private JPanel treePanel;

    private JPanel infoPanel, verdictPanel;
    private JButton open;
    private String username;
    private LinkedList<Integer> journalsISSN;
    private LinkedList<Journal> journals;
    private LinkedList<Article> considerList;
    private int issn;
    private int selectedID;
    private Article selectedArt;

    private JPanel titleGroup, absGroup, maGroup;
    private JPanel vdGroup, saGroup, buttonPanel, buttonPanel1;
    private JPanel panel = null;


    EditorInterface(String username) throws SQLException {
        this.setTitle("Editor Interface");
        this.setSize(1000, 600);
        this.setLocationRelativeTo(null);
        this.setResizable(false);

        //set up panels
        infoPanel = new JPanel();
        treePanel = new JPanel();
        verdictPanel = new JPanel();
        panel = new JPanel();


        this.username = username;
        journalsISSN = new LinkedList<Integer>();
        journalsISSN = JournalController.getEditorJournals(username);
        journals = new LinkedList<Journal>();

        //create the menu
        menuBar = new JMenuBar();

        //journal selection
        //menu.addSeparator();
        selectJournal = new JMenu("Select Journal" );
        group = new ButtonGroup();
        for (int j=0; j<journalsISSN.size(); ++j){
            issn = journalsISSN.get(j);
            Journal journal = JournalController.getJournal(issn);
            journalItem = new JRadioButtonMenuItem(journal.getTitle());
            journals.add(journal);
            journalItem.addActionListener(this);
            group.add(journalItem);
            selectJournal.add(journalItem);
            if (j==0){
                journalItem.setSelected(true);
            }
        }

        menuBar.add(selectJournal);

        //settings:
        settings = new JMenu("Settings");
        updatePf = new JMenuItem("Update Profile");
        changePw = new JMenuItem("Change Password");
        toChiefEditor = new JMenuItem("Chief Editor Options");
        retire = new JMenuItem("Retire");

        changePw.addActionListener(this);
        toChiefEditor.addActionListener(this);
        updatePf.addActionListener(this);
        //if ( !UserController.isChiefEditor(username, issn )){ toChiefEditor.setEnabled(false);} else {toChiefEditor.setEnabled(true);}
        retire.addActionListener(this);
        settings.add(changePw);
        settings.add(updatePf);
        settings.add(retire);
        menuBar.add(settings);
        menuBar.add(toChiefEditor);

        logOut = new JMenuItem("Log out");
        logOut.addActionListener(this);
        menuBar.add(logOut);

        this.setJMenuBar(menuBar);



        //create the tree panel
        JPanel treePanel = new JPanel();
        //create the root node
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Journal Publish System");
        //create tree
        considerList = new LinkedList<>();
        for (Journal journal : journals) {
            DefaultMutableTreeNode journal1 = new DefaultMutableTreeNode(journal);
            root.add(journal1);
            DefaultMutableTreeNode underCs = new DefaultMutableTreeNode("Under Consideration");
            journal1.add(underCs);
            journal.getIssn();
            considerList = JournalController.getArtByStatusAndJournal(Status.FINAL_VERDICTS_RECEIVED,
                   journal.getIssn());
            for (Article a : considerList){
                DefaultMutableTreeNode a1 = new DefaultMutableTreeNode(a);
                underCs.add(a1);
            }
        }

        //create the tree by passing in the root node
        tree = new JTree(root);

        tree.setShowsRootHandles(true);
        tree.setRootVisible(false);
        treeScrollPane = new JScrollPane(tree);
        treeScrollPane.setPreferredSize(new Dimension(250, 527));
        treePanel.add(treeScrollPane);

        //display selection bottom bar
        selectedLabel = new JLabel();

        add(selectedLabel, BorderLayout.SOUTH);
        tree.getSelectionModel().addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (selectedNode.isLeaf() && considerList != null){
                selectedLabel.setText(selectedNode.getUserObject().toString());
                selectedArt = (Article)selectedNode.getUserObject();
                selectedID = selectedArt.getSubmissionID();
                System.out.println(selectedID);
                try {
                    this.removeAll();
                    this.add(treePanel, BorderLayout.WEST);
                    this.add(panel(selectedID),BorderLayout.WEST);
                    this.invalidate();
                    this.repaint();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

        });


        //add panels functions
        this.add(treePanel, BorderLayout.WEST);


        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public JPanel panel(int submissionId) throws SQLException {
        JPanel panel = new JPanel();
        //set up the article information panel
        infoPanel.setPreferredSize(new Dimension(730, 300));

        //List of Qs
        //questions panel settings
        titleGroup = new JPanel(new BorderLayout());
        //qsGroup.setPreferredSize(new Dimension(1000,230));
        titleGroup.setBorder(BorderFactory.createEmptyBorder(10, 50, 0, 0));
        JLabel lblTitle = new JLabel("Title of Article");
        lblTitle.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        JLabel title = new JLabel(selectedArt.getTitle());
        title.setFont(new Font("Arial", Font.PLAIN, 15));
        titleGroup.add(lblTitle, BorderLayout.PAGE_START);
        titleGroup.add(title, BorderLayout.WEST);

        absGroup = new JPanel(new BorderLayout(10,10));
        absGroup.setBorder(BorderFactory.createEmptyBorder(0, 50, 10, 0));
        JLabel lblAbstract = new JLabel("Abstract of Article");
        lblAbstract.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        JTextArea artAbstract = new JTextArea();
        artAbstract.setText(selectedArt.getAbstract());
        artAbstract.setEditable(false);
        artAbstract.setLineWrap(true);
        artAbstract.setWrapStyleWord(true);
        artAbstract.setFont(new Font("Arial", Font.PLAIN, 15));
        JScrollPane absPane = new JScrollPane(artAbstract);
        absPane.setPreferredSize(new Dimension(630,100));
        absPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        absPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        absGroup.add(lblAbstract, BorderLayout.PAGE_START);
        absGroup.add(absPane, BorderLayout.WEST);

        maGroup = new JPanel(new BorderLayout());
        //qsGroup.setPreferredSize(new Dimension(1000,230));
        maGroup.setBorder(BorderFactory.createEmptyBorder(0, 50, 10, 0));
        JLabel lblMa = new JLabel("Main Author");
        lblMa.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        JLabel mainAuthor = new JLabel(selectedArt.getMAuthorEmail());
        mainAuthor.setFont(new Font("Arial", Font.PLAIN, 15));
        maGroup.add(lblMa, BorderLayout.PAGE_START);
        maGroup.add(mainAuthor, BorderLayout.WEST);

        buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 100));
        open = new JButton("Open");
        open.addActionListener((new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    System.out.println("open " + selectedID);
                    ArticleController.getSubmissionPDF(selectedID);
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
        }));
        open.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
        buttonPanel.add(open, BorderLayout.EAST);

        //open button
        infoPanel.setLayout(new BoxLayout(infoPanel,BoxLayout.Y_AXIS));
        infoPanel.add(titleGroup);
        infoPanel.add(absGroup);
        infoPanel.add(maGroup);
        infoPanel.add(buttonPanel);




        //set up the verdict panel
        verdictPanel.setPreferredSize(new Dimension(730, 100));
        verdictPanel.setLayout(new BorderLayout());

        LinkedList<Verdict> finalVerdicts = JournalController.getFinalVerdicts(submissionId);
        //List of final verdicts
        //final verdicts panel settings
        vdGroup = new JPanel();
        vdGroup.setLayout(new BoxLayout(vdGroup, BoxLayout.Y_AXIS));
        vdGroup.setPreferredSize(new Dimension(190,100));
        vdGroup.setBorder(BorderFactory.createEmptyBorder(40, 50, 0, 0));
        for (Verdict vd : finalVerdicts) {
            JLabel vd1 = new JLabel(vd.toString());
            vdGroup.add(vd1);
            vd1.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        }



        saGroup = new JPanel();
        saGroup.setLayout(new BoxLayout(saGroup, BoxLayout.Y_AXIS));
        saGroup.setPreferredSize(new Dimension(600,100));
        saGroup.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));

        JLabel actTitle = new JLabel("Suggested Action:");
        actTitle.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        JLabel suggestion = new JLabel(JournalController.getSuggestion(submissionId));
        suggestion.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        saGroup.add(actTitle);
        saGroup.add(suggestion);


        JButton accept = new JButton("Accept");
        accept.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
        accept.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (JournalController.checkEditorConflict(username, submissionId)) {
                        JOptionPane.showMessageDialog(null, "Sorry, there is an interest conflict \n" +
                                "with this submission!");
                    } else {
                        if (JournalController.acceptAnArticle(submissionId)) {
                            JOptionPane.showMessageDialog(null, "You have accept " + submissionId +
                                    "successfully!");
                        } else {
                            JOptionPane.showMessageDialog(null, "Sorry, please try again!");
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        JButton reject = new JButton("Reject");
        reject.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
        reject.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (JournalController.checkEditorConflict(username, submissionId)) {
                        JOptionPane.showMessageDialog(null, "Sorry, there is an interest conflict \n" +
                                "with this submission!");
                    } else {
                        JournalController.rejectAnArticle(submissionId);
                        if (JournalController.rejectAnArticle(submissionId)) {
                            JOptionPane.showMessageDialog(null, "You have accept " + submissionId +
                                    "successfully!");
                        } else {
                            JOptionPane.showMessageDialog(null, "Sorry, please try again!");
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        //layout for buttonPane - accept and reject
        buttonPanel1 = new JPanel();
        buttonPanel1.setLayout(new BoxLayout(buttonPanel1, BoxLayout.X_AXIS));
        buttonPanel1.setBorder(BorderFactory.createEmptyBorder(0, 150, 0, 0));
        buttonPanel1.add(accept);
        buttonPanel1.add(reject);


        JPanel rightPane = new JPanel();
        rightPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 100));
        rightPane.setPreferredSize(new Dimension(600,100));
        rightPane.setLayout(new BoxLayout(rightPane, BoxLayout.Y_AXIS));
        rightPane.add(saGroup);
        rightPane.add(buttonPanel1);


        verdictPanel.add(vdGroup, BorderLayout.WEST);
        verdictPanel.add(rightPane, BorderLayout.CENTER);

        JScrollPane northPane = new JScrollPane(infoPanel);
        northPane.setPreferredSize(new Dimension(730,300));
        northPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JScrollPane southPane = new JScrollPane(verdictPanel);
        southPane.setPreferredSize(new Dimension(730,100));
        southPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(730,600));
        panel.add(northPane);
        panel.add(southPane);

        return panel;
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
            UserController.logout();
            JOptionPane.showMessageDialog(null, "You have logged out successfully!");
            new LoginInterface();
        }
        else if (e.getSource() == toChiefEditor) {
            try {
                new ChiefEditorInterface(username);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            this.dispose();
        }
        else if(e.getSource()==updatePf){
            new UpdateProfileInterface(username, 1,false);
            this.dispose();
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
        }
        else if (e.getSource() == changePw) {
            new ChangePw(username, 1);
            this.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                //new EditorInterface("hermiona.granger@hogwarts.ac.uk");
                new EditorInterface("harry.potter@warwick.ac.uk");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Could not connect to the server, please try again.");
                e.printStackTrace();
            }
        });
    }

}
