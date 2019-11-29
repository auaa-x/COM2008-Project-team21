/**
 * Class for Reader General interface
 * @author Ting Guo
 * @author Huiqiang Liu
 */


import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;


public class ReaderInterface extends JFrame implements ActionListener {
    private JTree tree;
    private JLabel selectedLabel;
    private JScrollPane treeScrolPane;
    private LinkedList<Journal> journals = new LinkedList<Journal>();
    private LinkedList<Volume> volumes = new LinkedList<Volume>();
    private JMenuBar menuBar;
    private JMenu view;
    private JMenuItem login;
    private File article = new File("./article.pdf");
    private Desktop desktop = Desktop.getDesktop();
    private JPanel infoPanel = new JPanel();
    private JLabel infoTitle;
    private JButton open;


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
        infoPanel.setPreferredSize(new Dimension(725, 525));


        //title of the information panel
        infoTitle = new JLabel("Article Information Display Here");
        infoTitle.setFont(new Font("Tahoma", Font.PLAIN, 20));
        //open button
        open = new JButton("Open");
        open.addActionListener(this);
        open.setFont(new Font("Tahoma", Font.PLAIN, 15));
        /*
        infoPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        JButton btnOpen = new JButton("Open");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 0.2;
        gbc.anchor = GridBagConstraints.SOUTH;
        artInfo.add(btnOpen, gbc);

        JLabel jInfo = new JLabel("Information:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.gridheight = 2;
        gbc.weightx = 0.2;
        gbc.weighty = 0.8;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        artInfo.add(jInfo, gbc);*/


        //create the tree panel
        JPanel treePanel = new JPanel();
        //create the root node
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Journal Publish System");
        journals = JournalController.getAllJournals();
        //getVolumes(int issn);

        for (Journal journal : journals) {
            DefaultMutableTreeNode journal1 = new DefaultMutableTreeNode(journal.toString());
            root.add(journal1);
            for (Volume volume : JournalController.getVolumes(journal.getIssn())) {
                DefaultMutableTreeNode volume1 = new DefaultMutableTreeNode(volume.toString());
                journal1.add(volume1);
                for (Edition edition : JournalController.getEditions(volume.getIssn(), volume.getVolNum())) {
                    DefaultMutableTreeNode edition1 = new DefaultMutableTreeNode(edition.toString());
                    volume1.add(edition1);
                    for (Article article : JournalController.getPublishedArticles(journal.getIssn(), edition.getVolNum(), edition.getNoNum())) {
                        DefaultMutableTreeNode article1 = new DefaultMutableTreeNode(article.toString());
                        edition1.add(article1);
                    }
                }
            }
        }
        DefaultMutableTreeNode test = new DefaultMutableTreeNode("test");
        DefaultMutableTreeNode testFile = new DefaultMutableTreeNode("test file");
        test.add(testFile);
        testFile.add(new DefaultMutableTreeNode("Article"));
        root.add(test);

        //create the tree by passing in the root node
        tree = new JTree(root);

        tree.setShowsRootHandles(true);
        tree.setRootVisible(false);
        treeScrolPane = new JScrollPane(tree);
        treeScrolPane.setPreferredSize(new Dimension(250, 527));
        treePanel.add(treeScrolPane);

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


        infoPanel.add(infoTitle);



        //add panels functions
        this.add(treePanel, BorderLayout.WEST);
        this.add(infoPanel, BorderLayout.EAST);
        this.setJMenuBar(menuBar);



     /*   tree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                            tree.getLastSelectedPathComponent();
                    if (node == null) return;
                    Object nodeInfo = node.getUserObject();
                    // Cast nodeInfo to your object and do whatever you want
                }
            }
        });*/

         /*
    @Override
            public void valueChanged(TreeSelectionEvent e) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();

            if (node == null)
                return;

            Object object = node.getUserObject();
            if (node.isLeaf()) {
                Article title = (Article) object;
                System.out.println("you choosed:"+ title.toString());
            }
     */



        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
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