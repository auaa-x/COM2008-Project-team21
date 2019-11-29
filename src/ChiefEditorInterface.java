/**
 * Class for Chief Editor Interface
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
import java.util.LinkedList;


public class ChiefEditorInterface extends JFrame implements ActionListener {
	private JMenuBar menuBar;
	private JMenu staff, journal;
	private JMenuItem register, appoint, passChiefEditor, retire, publish, delay, logOut;
	private File article = new File("./article.pdf");
	private Desktop desktop = Desktop.getDesktop();

	private JTree tree;
	private JLabel selectedLabel;
	private JScrollPane treeScrollPane;
	private JPanel treePanel;

	private JPanel infoPanel;
	private JLabel infoTitle;
	private JButton open;
	private String username;
	private LinkedList<Integer> journals;


	ChiefEditorInterface(String username) throws SQLException {

		this.setTitle("Chief Editor Interface");
		this.setSize(1000, 600);
		this.setLocationRelativeTo(null);
		this.setResizable(false);


		this.username = username;
		journals = new LinkedList<Integer>();
		journals = JournalController.getEditorJournals(username);



		//set up panels
		infoPanel = new JPanel();
		treePanel = new JPanel();

		//create the menu
		menuBar = new JMenuBar();
		staff = new JMenu("Staff Management" );
		journal = new JMenu("Journal Management");
		logOut = new JMenuItem("Log out");
		logOut.addActionListener(this);
		menuBar.add(staff);
		menuBar.add(journal);
		menuBar.add(logOut);


		register = new JMenuItem("Register an editor");
		appoint = new JMenuItem("Appoint an editor");
		passChiefEditor = new JMenuItem("Pass the role");
		retire = new JMenuItem("Retire");
		publish = new JMenuItem("Publish");
		delay = new JMenuItem("Delay article");
		register.addActionListener(this);
		appoint.addActionListener(this);
		passChiefEditor.addActionListener(this);
		retire.addActionListener(this);
		publish.addActionListener(this);
		delay.addActionListener(this);

		staff.add(register);
		staff.add(appoint);
		staff.add(passChiefEditor);
		staff.add(retire);
		journal.add(publish);
		journal.add(delay);

		this.setJMenuBar(menuBar);


		//set up the article information panel
		infoPanel.setPreferredSize(new Dimension(725, 525));


		//title of the information panel
		infoTitle = new JLabel("Article Information Display Here");
		infoTitle.setFont(new Font("Tahoma", Font.PLAIN, 20));
		//open button
		open = new JButton("Open");
		open.addActionListener(this);
		open.setFont(new Font("Tahoma", Font.PLAIN, 15));

		//create the tree panel
		JPanel treePanel = new JPanel();
		//create the root node
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Journal Publish System");
		LinkedList<Journal> journals = JournalController.getAllJournals();

		for (Journal journal : journals) {
			DefaultMutableTreeNode journal1 = new DefaultMutableTreeNode(journal.getTitle());
			root.add(journal1);
			for (Volume volume : JournalController.getVolumes(journal.getIssn())) {
				DefaultMutableTreeNode volume1 = new DefaultMutableTreeNode("vol. " + volume.getVolNum());
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
		//create the tree by passing in the root node
		tree = new JTree(root);

		tree.setShowsRootHandles(true);
		tree.setRootVisible(false);
		treeScrollPane = new JScrollPane(tree);
		treeScrollPane.setPreferredSize(new Dimension(250, 527));
		treePanel.add(treeScrollPane);

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




		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
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

		//register an editor
		else if (e.getSource() == register) {
			/*UserController.createTempUser();
			UserController.createEditor();*/
		}

		//appoint an editor (add user type 1(editor) to an existed user )
		else if (e.getSource() == appoint) {
			String[] options = {"Yes", "Back"};
			JComboBox journalSelection = new JComboBox(journals.toArray());
			JOptionPane.showMessageDialog(null, journalSelection, "please select a journal", JOptionPane.QUESTION_MESSAGE);

			String appointed = JOptionPane.showInputDialog("Please enter other editor's email address");
			try {
				UserController.addRole(appointed, 1);
				UserController.createEditor(appointed, (Integer) journalSelection.getSelectedItem());
				if (UserController.checkUsertype(appointed, 1)) {
					JOptionPane.showMessageDialog(null, "Editor added successfully!");
				} else {
					JOptionPane.showMessageDialog(null, "Please try again!");
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			//System.out.printf("The user's name is '%s'.\n", name);
		}

		//pass the chief editor to other editor
		else if (e.getSource() == passChiefEditor) {

		}

		//retire
		else if (e.getSource() == retire) {
				/*String[] options = {"Yes", "No"};
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
			}*/
			JComboBox journalSelection = new JComboBox(journals.toArray());
			JOptionPane.showMessageDialog(null, journalSelection, "please select a journal", JOptionPane.QUESTION_MESSAGE);
			try {
				int issn = (Integer) journalSelection.getSelectedItem();
				if (JournalController.chiefEditorRetire(username, issn)) {
					JOptionPane.showMessageDialog(null, "You have retire from " + issn + " successfully");
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		} else if (e.getSource() == publish) {

		}
	}


	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				new ChiefEditorInterface("harry.potter@warwick.ac.uk");
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "Can not connect to the  server, please try again.");
				e.printStackTrace();
			}
		});
	}


}