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
import java.sql.SQLException;


public class readerInterface extends JFrame implements ActionListener {
    private JTree tree;
    private JLabel selectedLabel;
    private JScrollPane treeScrolPane;
    private JMenuBar menuBar;
    private JMenu view;
    private JMenuItem login;

    readerInterface() {
        this.setTitle("Reader Interface");
        this.setSize(1000, 600);
        this.setLocationRelativeTo(null);
        this.setResizable(false);

        //create the menu
        menuBar = new JMenuBar();
        view = new JMenu("View");
        login = new JMenuItem("Login");
        //file.addActionListener(this);
        login.addActionListener(this);
        view.add(login);
        menuBar.add(view);
        this.setJMenuBar(menuBar);

        //create "open" button
        JPanel artInfo = new JPanel();
        artInfo.setPreferredSize(new Dimension(200, 0));
        this.add(artInfo, BorderLayout.EAST);

        //create article information
        artInfo.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        JButton btnOpen = new JButton("Open");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 0.2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
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
        artInfo.add(jInfo, gbc);


        //create the tree panel
        JPanel treePanel = new JPanel();
        //create the root node
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Journal Publish System");
        //create the child nodes as root name
        DefaultMutableTreeNode csJournal = new DefaultMutableTreeNode("Journal of Computer Science");
        DefaultMutableTreeNode seJournal = new DefaultMutableTreeNode("Journal of Software Engineering");
        DefaultMutableTreeNode aiJournal = new DefaultMutableTreeNode("Journal of Artificial Intelligence");

        //create other tree items as Journals
        DefaultMutableTreeNode volumeNode = new DefaultMutableTreeNode("Volume");
        DefaultMutableTreeNode volumeNode1 = new DefaultMutableTreeNode("Volume1");
        DefaultMutableTreeNode volumeNode2 = new DefaultMutableTreeNode("Volume");

        //add volumes to journal node
        csJournal.add(volumeNode);
        seJournal.add(volumeNode1);
        aiJournal.add(volumeNode2);

        //create other tree items as Volumes
        DefaultMutableTreeNode editionNode = new DefaultMutableTreeNode("Edition");
        DefaultMutableTreeNode editionNode1 = new DefaultMutableTreeNode("Edition1");
        //add Editions to Volume node
        volumeNode.add(editionNode);
        volumeNode.add(editionNode1);
        //create other tree items as Editions
        editionNode.add(new DefaultMutableTreeNode("Capsicum"));
        editionNode.add(new DefaultMutableTreeNode("Carrot"));
        editionNode.add(new DefaultMutableTreeNode("Tomato"));
        editionNode.add(new DefaultMutableTreeNode("Potato"));

        editionNode1.add(new DefaultMutableTreeNode("Banana"));
        editionNode1.add(new DefaultMutableTreeNode("Mango"));
        editionNode1.add(new DefaultMutableTreeNode("Apple"));
        editionNode1.add(new DefaultMutableTreeNode("Grapes"));
        editionNode1.add(new DefaultMutableTreeNode("Orange"));

        //add the child nodes to the root node
        root.add(csJournal);
        root.add(seJournal);
        root.add(aiJournal);

        //create the tree by passing in the root node
        tree = new JTree(root);

        tree.setShowsRootHandles(true);
        tree.setRootVisible(false);
        treeScrolPane = new JScrollPane(tree);
        treeScrolPane.setPreferredSize(new Dimension(250, 512));
        treePanel.add(treeScrolPane);


        this.add(treePanel, BorderLayout.WEST);

        selectedLabel = new JLabel();
        add(selectedLabel, BorderLayout.SOUTH);
        tree.getSelectionModel().addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            selectedLabel.setText(selectedNode.getUserObject().toString());
        });

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == login) {
            this.setVisible(false);
            System.out.println(1);
            new LoginInterface();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new readerInterface());
    }


}