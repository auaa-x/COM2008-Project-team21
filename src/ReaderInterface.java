/**
 * Class for Reader General interface
 * @author Ting Guo
 * @author Huiqiang Liu
 */


import java.awt.*;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;


public class ReaderInterface extends JFrame implements ActionListener {

    private JPanel infoPanel = new JPanel();
    private CardLayout cardLayout = new CardLayout();

    private JMenuBar menuBar;
    private JMenuItem login;

    private JTree tree;
    private JLabel selectedLabel;
    private JScrollPane treeScrolPane;
    private LinkedList<Journal> journals = new LinkedList<Journal>();
    private Desktop desktop = Desktop.getDesktop();
    private JButton open;


    private int selectedID;
    private Article selectedArt;


    public ReaderInterface() throws IOException, SQLException {
        this.setTitle("Reader Interface");
        this.setSize(1000, 600);
        this.setLocationRelativeTo(null);
        this.setResizable(false);

        //create the menu
        menuBar = new JMenuBar();
        login = new JMenuItem("Back to login");
        //file.addActionListener(this);
        login.addActionListener(this);
        menuBar.add(login);


        //set up the article information panel
        infoPanel.setPreferredSize(new Dimension(725, 600));


        //open button
        open = new JButton("Open");
        open.addActionListener(this);
        open.setFont(new Font("Tahoma", Font.PLAIN, 15));

        //create the tree panel
        JPanel treePanel = new JPanel();
        //create the root node
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Journal Publish System");
        journals = JournalController.getAllJournals();


        for (Journal journal : journals) {
            DefaultMutableTreeNode journal1 = new DefaultMutableTreeNode(journal);
            root.add(journal1);
            for (Volume volume : JournalController.getVolumes(journal.getIssn())) {
                DefaultMutableTreeNode volume1 = new DefaultMutableTreeNode(volume);
                journal1.add(volume1);
                for (Edition edition : JournalController.getEditions(volume.getIssn(), volume.getVolNum())) {
                    DefaultMutableTreeNode edition1 = new DefaultMutableTreeNode(edition);
                    volume1.add(edition1);
                    for (Article article : JournalController.getPublishedArticles(journal.getIssn(), edition.getVolNum(), edition.getNoNum())) {
                        DefaultMutableTreeNode article1 = new DefaultMutableTreeNode(article);
                        edition1.add(article1);
                    }
                }
            }
        }
        //create the tree by passing in the root node
        tree = new JTree(root);

        tree.setShowsRootHandles(true);
        tree.setRootVisible(false);
        treeScrolPane = new JScrollPane(tree);
        treeScrolPane.setPreferredSize(new Dimension(250, 527));
        treePanel.add(treeScrolPane);

        //display selection bottom bar
        selectedLabel = new JLabel();
        add(selectedLabel, BorderLayout.SOUTH);

        infoPanel = new JPanel();
        infoPanel.setLayout(cardLayout);
        tree.getSelectionModel().addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            selectedLabel.setText(selectedNode.getUserObject().toString());
            if (selectedNode.isLeaf()){
                selectedLabel.setText(selectedNode.getUserObject().toString());
                selectedArt = (Article)selectedNode.getUserObject();
                selectedID = selectedArt.getSubmissionID();
                System.out.println(selectedID);
                try {
                    infoPanel.add(panel(selectedID), selectedNode.getUserObject().toString());
                    cardLayout.show(infoPanel, selectedNode.getUserObject().toString());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });



        //add panels functions
        this.add(treePanel, BorderLayout.WEST);
        this.add(infoPanel, BorderLayout.EAST);
        this.setJMenuBar(menuBar);


        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public JScrollPane panel(int submissionId) throws SQLException {
        //set up the article information panel
        JPanel infoPanel = new JPanel();
        infoPanel.setPreferredSize(new Dimension(730, 527));

        //List of Qs
        //questions panel settings
        JPanel titleGroup = new JPanel(new BorderLayout());
        //qsGroup.setPreferredSize(new Dimension(1000,230));
        titleGroup.setBorder(BorderFactory.createEmptyBorder(10, 50, 0, 0));
        JLabel lblTitle = new JLabel("Title of Article");
        lblTitle.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        JLabel title = new JLabel(selectedArt.getTitle());
        title.setFont(new Font("Arial", Font.PLAIN, 15));
        titleGroup.add(lblTitle, BorderLayout.PAGE_START);
        titleGroup.add(title, BorderLayout.WEST);

        JPanel absGroup = new JPanel(new BorderLayout(10, 10));
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
        absPane.setPreferredSize(new Dimension(630,280));
        absPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        absGroup.add(lblAbstract, BorderLayout.PAGE_START);
        absGroup.add(absPane, BorderLayout.WEST);

        JPanel maGroup = new JPanel(new BorderLayout());
        //qsGroup.setPreferredSize(new Dimension(1000,230));
        maGroup.setBorder(BorderFactory.createEmptyBorder(0, 50, 10, 0));
        JLabel lblMa = new JLabel("Main Author");
        lblMa.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        JLabel mainAuthor = new JLabel(selectedArt.getMAuthorEmail());
        mainAuthor.setFont(new Font("Arial", Font.PLAIN, 15));
        maGroup.add(lblMa, BorderLayout.PAGE_START);
        maGroup.add(mainAuthor, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new BorderLayout());
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



        JScrollPane scrollPane = new JScrollPane(infoPanel);
        scrollPane.setPreferredSize(new Dimension(730,527));
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        return scrollPane;
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == login) {
            this.dispose();
            System.out.println(1);
            new LoginInterface();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new ReaderInterface();

            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        });
    }


}