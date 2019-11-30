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
import java.util.Enumeration;
import java.util.LinkedList;


public class ChiefEditorInterface extends JFrame implements ActionListener {
	private JMenuBar menuBar;
	private JMenu staff, journal;
	private ButtonGroup group;
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
	private LinkedList<Integer> journalsISSN;
	private LinkedList<Integer> chiefJournalsISSN;
	private LinkedList<Journal> journals;


	ChiefEditorInterface(String username) throws SQLException {

		this.setTitle("Chief Editor Interface");
		this.setSize(1000, 600);
		this.setLocationRelativeTo(null);
		this.setResizable(false);


		this.username = username;
		journalsISSN = JournalController.getEditorJournals(username);
		chiefJournalsISSN = new LinkedList<Integer>();
		for (int issn : journalsISSN) {
			if (UserController.isChiefEditor(username, issn)){
				chiefJournalsISSN.add(issn);
			}
		}
		journals = new LinkedList<Journal>();
		for (int issn : chiefJournalsISSN) {
			Journal journal = JournalController.getJournal(issn);
			journals.add(journal);
/*			journalItem = new JRadioButtonMenuItem(journal.getTitle());
			journals.add(journal);
			journalItem.addActionListener(this);
			group.add(journalItem);
			if (j == 0) {
				journalItem.setSelected(true);
			}
			journalSelection.add(journalItem);*/
		}


		//set up panels
		infoPanel = new JPanel();
		treePanel = new JPanel();

		//create the menu
		menuBar = new JMenuBar();
		group = new ButtonGroup();
/*		//journal selection
		//menu.addSeparator();
		journalSelection = new JMenu("Select Journal");
		for (int j = 0; j < journalsISSN.size(); ++j) {
			issn = journalsISSN.get(j);
			Journal journal = JournalController.getJournal(issn);
			journalItem = new JRadioButtonMenuItem(journal.getTitle());
			journals.add(journal);
			journalItem.addActionListener(this);
			group.add(journalItem);
			if (j == 0) {
				journalItem.setSelected(true);
			}
			journalSelection.add(journalItem);
		}
		menuBar.add(journalSelection);*/
		staff = new JMenu("Staff Management");
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
			if (selectedNode.getUserObject().toString().equals("Article")) {
				//first check if Desktop is supported by Platform or not
				if (!Desktop.isDesktopSupported()) {
					JOptionPane.showMessageDialog(null, "Desktop does not support this function");
					return;
				} else if (article.exists()) {
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
		//register an editor
		else if (e.getSource() == register) {

		}
		//appoint an editor (add user type 1(editor) to an existed user )
		else if (e.getSource() == appoint) {
			String[] options = {"Yes", "Back"};
			JComboBox<Object> journalSelection = new JComboBox<>(journals.toArray());
			JOptionPane.showMessageDialog(null, journalSelection, "please select a journal", JOptionPane.QUESTION_MESSAGE);
			Journal selectedJournal = (Journal) journalSelection.getItemAt(journalSelection.getSelectedIndex());
			String appointed = JOptionPane.showInputDialog("Please enter other editor's email address");
			try {
				if (!UserController.checkEmail(appointed)) {
					JOptionPane.showMessageDialog(null, "Sorry, this email address has not been registered yet,\n" +
							"please go to 'register an editor'. ");
				} else {
						UserController.addRole(appointed, 1);
						UserController.createEditor(appointed,selectedJournal.getIssn());
						if (UserController.checkUsertype(appointed, 1)) {
							JOptionPane.showMessageDialog(null, "Editor added successfully!");
						} else {
							JOptionPane.showMessageDialog(null, "Please try again!");
						}
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
			JComboBox<Object> journalSelection = new JComboBox<>(journals.toArray());
			JOptionPane.showMessageDialog(null, journalSelection, "please select a journal", JOptionPane.QUESTION_MESSAGE);
			try {
				Journal selectedJournal = (Journal) journalSelection.getItemAt(journalSelection.getSelectedIndex());
				//System.out.println(title);
				String title = selectedJournal.getTitle();
				int issn = (Integer)selectedJournal.getIssn();
				System.out.println(issn);
				LinkedList<String> editors = JournalController.getEditors(issn);
				System.out.println(editors);
				if (editors.size() < 2) {
					JOptionPane.showMessageDialog(null, "You can not retire from journal " + title + ".\n Because there is only 1 editor.");
				} else {
					if (JournalController.chiefEditorRetire(username, issn)) {
						JOptionPane.showMessageDialog(null, "You have retire from journal " + title + " successfully");
						this.dispose();
						new EditorInterface(username);
					}
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		    else if (e.getSource() == publish) {

			}
		}



			public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				new ChiefEditorInterface("hermiona.granger@hogwarts.ac.uk");
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "Can not connect to the  server, please try again.");
				e.printStackTrace();
			}
		});
	}


}