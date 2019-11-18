import java.awt.BorderLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class readerInterfaceTree extends JFrame {
    private JTree tree;
    private JLabel selectedLabel;

    public readerInterfaceTree()
    {
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
        add(new JScrollPane(tree));

        selectedLabel = new JLabel();
        add(selectedLabel, BorderLayout.SOUTH);
        tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                selectedLabel.setText(selectedNode.getUserObject().toString());
            }
        });

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("JTree Example");
        this.setSize(200, 200);
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
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new readerInterfaceTree();
            }
        });
    }
}