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
	private JPanel artPanel;
	private CardLayout cardLayout = new CardLayout();

	//JMenu
	private JMenuBar menuBar;
	private JMenu staff, journal;
	private JMenuItem register, appoint, passChiefEditor, retire, publish, toEditor, logOut;
	private Desktop desktop = Desktop.getDesktop();

	private JTree tree;
	private JLabel selectedLabel;
	private JPanel treePanel;

	private JButton open;
	private String username;
	private LinkedList<Integer> journalsISSN;
	private LinkedList<Integer> chiefJournalsISSN;
	private LinkedList<Journal> journals;
	private LinkedList<Article> accptedList;
	private LinkedList<Article> delayedList;

	private int selectedID;
	private Article selectedArt;


	ChiefEditorInterface(String username) throws SQLException {

		this.setTitle("Chief Editor Interface");
		this.setSize(1000, 600);
		this.setLocationRelativeTo(null);
		this.setResizable(false);


		this.username = username;
		journalsISSN = JournalController.getEditorJournals(username);
		chiefJournalsISSN = new LinkedList<Integer>();
		for (int issn : journalsISSN) {
			if (UserController.isChiefEditor(username, issn)) {
				chiefJournalsISSN.add(issn);
			}
		}
		journals = new LinkedList<Journal>();
		for (int issn : chiefJournalsISSN) {
			Journal journal = JournalController.getJournal(issn);
			journals.add(journal);
		}

		//set up panels
		treePanel = new JPanel();

		//create the menu
		menuBar = new JMenuBar();
		staff = new JMenu("Staff Management");
		journal = new JMenu("Journal Management");
		toEditor = new JMenuItem("To Editor Options");
		toEditor.setPreferredSize(new Dimension(20, 10));
		logOut = new JMenuItem("Log out");
		toEditor.addActionListener(this);
		logOut.addActionListener(this);

		menuBar.add(staff);
		menuBar.add(journal);
		menuBar.add(toEditor);
		menuBar.add(logOut);


		register = new JMenuItem("Register an editor");
		appoint = new JMenuItem("Appoint an editor");
		passChiefEditor = new JMenuItem("Pass the role");
		retire = new JMenuItem("Retire as chief editor");
		publish = new JMenuItem("Publish");
		register.addActionListener(this);
		appoint.addActionListener(this);
		passChiefEditor.addActionListener(this);
		retire.addActionListener(this);
		publish.addActionListener(this);

		staff.add(register);
		staff.add(appoint);
		staff.add(passChiefEditor);
		staff.add(retire);
		journal.add(publish);

		this.setJMenuBar(menuBar);

		//create the tree panel
		JPanel treePanel = new JPanel();
		//create the root node
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Journal Publish System");
		//create tree
		accptedList = new LinkedList<>();
		delayedList = new LinkedList<>();
		for (Journal journal : journals) {
			DefaultMutableTreeNode journal1 = new DefaultMutableTreeNode(journal);
			root.add(journal1);
			DefaultMutableTreeNode accepted = new DefaultMutableTreeNode("Accepted");
			DefaultMutableTreeNode delayed = new DefaultMutableTreeNode("Delayed");
			journal1.add(accepted);
			journal1.add(delayed);
			journal.getIssn();
			accptedList = JournalController.getArticlesToPublish(journal.getIssn());
			delayedList = JournalController.getDelayedArticles(journal.getIssn());
			for (Article a : accptedList) {
				DefaultMutableTreeNode a1 = new DefaultMutableTreeNode(a);
				accepted.add(a1);
			}

			for (Article a : delayedList) {
				DefaultMutableTreeNode a1 = new DefaultMutableTreeNode(a);
				delayed.add(a1);
			}
		}

		//create the tree by passing in the root node
		tree = new JTree(root);

		tree.setShowsRootHandles(true);
		tree.setRootVisible(false);
		JScrollPane treeScrollPane = new JScrollPane(tree);
		treeScrollPane.setPreferredSize(new Dimension(250, 527));
		treePanel.add(treeScrollPane);

		//display selection bottom bar
		selectedLabel = new JLabel();
		add(selectedLabel, BorderLayout.SOUTH);

		artPanel = new JPanel();
		artPanel.setLayout(cardLayout);
		//display panel depends on selected node
		tree.getSelectionModel().addTreeSelectionListener(e -> {
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			if (selectedNode.isLeaf() && accptedList != null) {
				selectedLabel.setText(selectedNode.getUserObject().toString());
				selectedArt = (Article) selectedNode.getUserObject();
				selectedID = selectedArt.getSubmissionID();
				artPanel.add(panel(selectedID), selectedNode.getUserObject().toString());
				cardLayout.show(artPanel, selectedNode.getUserObject().toString());
			}
		});
		//add panels functions
		this.add(treePanel, BorderLayout.WEST);
		this.add(artPanel, BorderLayout.EAST);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);

	}

	public JPanel panel(int submissionId) {
		//set up the article information panel
		JPanel infoPanel = new JPanel();
		infoPanel.setPreferredSize(new Dimension(730, 400));

		//List of Qs
		//questions panel settings
		JPanel titleGroup = new JPanel(new BorderLayout());
		titleGroup.setBorder(BorderFactory.createEmptyBorder(30, 50, 10, 0));
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
		absPane.setPreferredSize(new Dimension(630, 250));
		absPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		absGroup.add(lblAbstract, BorderLayout.PAGE_START);
		absGroup.add(absPane, BorderLayout.WEST);

		JPanel maGroup = new JPanel(new BorderLayout());
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
		open.addActionListener((e -> {
			openSubPdf(submissionId);
		}));
		open.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		buttonPanel.add(open, BorderLayout.EAST);

		//open button
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		infoPanel.add(titleGroup);
		infoPanel.add(absGroup);
		infoPanel.add(maGroup);
		infoPanel.add(buttonPanel);


		//set up the optionPanel panel
		JPanel optionPanel = new JPanel();
		optionPanel.setPreferredSize(new Dimension(730, 100));
		optionPanel.setLayout(new BorderLayout());


		JButton accept = new JButton("Accept");
		accept.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
		accept.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (ArticleController.setToAccepted(submissionId)) {
						JOptionPane.showMessageDialog(null, "You have accept " + submissionId +
								"successfully!");
					} else {
						JOptionPane.showMessageDialog(null, "Sorry, please try again!");
					}
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		});
		JButton delay = new JButton("Delay");
		delay.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
		delay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (ArticleController.setToDelayed(submissionId)) {
						JOptionPane.showMessageDialog(null, "You have accept " + submissionId +
								"successfully!");
					} else {
						JOptionPane.showMessageDialog(null, "Sorry, please try again!");
					}
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		});

		//layout for buttonPane - accept and reject
		JPanel buttonPanel1 = new JPanel();
		buttonPanel1.setLayout(new BoxLayout(buttonPanel1, BoxLayout.X_AXIS));
		buttonPanel1.setBorder(BorderFactory.createEmptyBorder(0, 250, 0, 0));
		buttonPanel1.add(accept);
		buttonPanel1.add(delay);

		JScrollPane northPane = new JScrollPane(infoPanel);
		northPane.setPreferredSize(new Dimension(730, 400));
		northPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		JScrollPane southPane = new JScrollPane(buttonPanel1);
		southPane.setPreferredSize(new Dimension(730, 100));
		southPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setPreferredSize(new Dimension(730, 600));
		panel.add(northPane);
		panel.add(southPane);

		return panel;
	}

	public void openSubPdf(int submissionId) {
		try {
			ArticleController.getSubmissionPDF(submissionId);
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


	@Override
	public void actionPerformed(ActionEvent e) {
		//register, appoint, passChiefEditor, retire, publish;
		//log out
		if (e.getSource() == logOut) {
			this.dispose();
			UserController.logout();
			JOptionPane.showMessageDialog(null, "You have logged out successfully!");
			new LoginInterface();
		}
		//to editor options
		if (e.getSource() == toEditor) {
			try {
				new EditorInterface(username);
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			this.dispose();
		}
		//register an editor
		else if (e.getSource() == register) {
			String[] options = {"Yes", "Back"};
			JComboBox<Object> journalSelection = new JComboBox<>(journals.toArray());
			String windowTitle = "Please select a journal";
			int x = JOptionPane.showOptionDialog(null, journalSelection, windowTitle,
					JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
			if (x == 0) {
				int issn = ((Journal) journalSelection.getItemAt(journalSelection.getSelectedIndex())).getIssn();
				new EditorRegister(username, issn);
				this.dispose();
			}
		}
		//appoint an editor (add user type 1(editor) to an existed user )
		else if (e.getSource() == appoint) {
			String[] options = {"Yes", "Back"};
			JComboBox<Object> journalSelection = new JComboBox<>(journals.toArray());
			String windowTitle = "Please select a journal";
			int x = JOptionPane.showOptionDialog(null, journalSelection, windowTitle,
					JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
			if (x == 0) {
				Journal selectedJournal = (Journal) journalSelection.getItemAt(journalSelection.getSelectedIndex());
				String appointed = JOptionPane.showInputDialog("Please enter other editor's email address");
				try {
					if (!UserController.checkEmail(appointed)) {
						JOptionPane.showMessageDialog(null, "Sorry, this email address has not been registered yet,\n" +
								"please go to 'register an editor'. ");
					} else {
						UserController.addRole(appointed, 1);
						UserController.createEditor(appointed, selectedJournal.getIssn());
						if (UserController.checkUsertype(appointed, 1)) {
							JOptionPane.showMessageDialog(null, "Editor added successfully!");
						} else {
							JOptionPane.showMessageDialog(null, "Please try again!");
						}
					}
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}

		//pass the chief editor to other editor
		else if (e.getSource() == passChiefEditor) {
			String[] options = {"Yes", "Back"};
			JComboBox<Object> journalSelection = new JComboBox<>(journals.toArray());
			String windowTitle = "Please select a journal";
			int x = JOptionPane.showOptionDialog(null, journalSelection, windowTitle,
					JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
			if (x == 0) {
				try {
					Journal selectedJournal = (Journal) journalSelection.getItemAt(journalSelection.getSelectedIndex());
					LinkedList<String> editors = JournalController.getEditors(selectedJournal.getIssn());
					System.out.println(editors);
					JComboBox<Object> editorsSelection = new JComboBox<>(editors.toArray());
					String title1 = "Please select an editor";
					int y = JOptionPane.showOptionDialog(null, editorsSelection, title1,
							JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
					if (y == 0) {
						String selectedEditor = (String) editorsSelection.getItemAt(editorsSelection.getSelectedIndex());
						if (JournalController.chiefEditorPassRole(username, selectedEditor, selectedJournal.getIssn())) {
							JOptionPane.showMessageDialog(null, "New chief editor appointed to " +
									selectedEditor + "successfully!");
						} else {
							JOptionPane.showMessageDialog(null, "Please try again!");
						}
					}
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}

		//retire
		else if (e.getSource() == retire) {
			String[] options = {"Yes", "Back"};
			JComboBox<Object> journalSelection = new JComboBox<>(journals.toArray());
			String windowTitle = "Please select a journal";
			int x = JOptionPane.showOptionDialog(null, journalSelection, windowTitle,
					JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
			//if editor click yes
			if (x == 0) {
				try {
					Journal selectedJournal = (Journal) journalSelection.getItemAt(journalSelection.getSelectedIndex());
					String title = selectedJournal.getTitle();
					int issn = selectedJournal.getIssn();
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

		} else if (e.getSource() == publish) {
			String[] options = {"Yes", "Back"};
			JComboBox<Object> journalSelection = new JComboBox<>(journals.toArray());
			String windowTitle = "Please select a journal";
			int x = JOptionPane.showOptionDialog(null, journalSelection, windowTitle,
					JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
			if (x == 0) {
				Journal selectedJournal = (Journal) journalSelection.getItemAt(journalSelection.getSelectedIndex());
				try {
					if (JournalController.publishNextEdition(selectedJournal.getIssn())) {
						JOptionPane.showMessageDialog(null, "You have published next edition of " +
								selectedJournal.getTitle() + " successfully!");
					} else {
						JOptionPane.showMessageDialog(null, "Please try again!");
					}
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
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