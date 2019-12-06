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

	//JMenu
	private JMenuBar menuBar;
	private JMenu staff, selectJn, journal;
	private ButtonGroup group;
	private JRadioButtonMenuItem jnItem;
	private JMenuItem register, appoint, passChiefEditor, retire, publish, delay, toEditor, logOut;
	private Desktop desktop = Desktop.getDesktop();

	private JTree tree;
	private JLabel selectedLabel;
	private JScrollPane treeScrollPane;
	private JPanel treePanel;

	private JPanel infoPanel, optionPanel;
	private JButton open;
	private String username;
	private LinkedList<Integer> journalsISSN;
	private LinkedList<Integer> chiefJournalsISSN;
	private LinkedList<Journal> journals;
	private LinkedList<Article> accptedList;
	private LinkedList<Article> rejectedList;

	private int selectedID;
	private Article selectedArt;

	private JPanel titleGroup, absGroup, maGroup;
	private JPanel buttonPanel, buttonPanel1;
	private JPanel panel;


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
		infoPanel = new JPanel();
		treePanel = new JPanel();
		optionPanel = new JPanel();

		//create the menu
		menuBar = new JMenuBar();
		group = new ButtonGroup();
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
		rejectedList = new LinkedList<>();
		for (Journal journal : journals) {
			DefaultMutableTreeNode journal1 = new DefaultMutableTreeNode(journal);
			root.add(journal1);
			DefaultMutableTreeNode accepted = new DefaultMutableTreeNode("Accepted");
			DefaultMutableTreeNode rejected = new DefaultMutableTreeNode("Rejected");
			journal1.add(accepted);
			journal1.add(rejected);
			journal.getIssn();
			accptedList = JournalController.getArticlesToPublish(journal.getIssn());
			for (Article a : accptedList) {
				DefaultMutableTreeNode a1 = new DefaultMutableTreeNode(a);
				accepted.add(a1);
			}

			for (Article a : rejectedList) {
				DefaultMutableTreeNode a1 = new DefaultMutableTreeNode(a);
				rejected.add(a1);
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
			if (selectedNode.isLeaf() && accptedList != null) {
				selectedLabel.setText(selectedNode.getUserObject().toString());
				selectedArt = (Article) selectedNode.getUserObject();
				selectedID = selectedArt.getSubmissionID();
				System.out.println(selectedID);
				try {
					this.add(panel(selectedID), BorderLayout.EAST);
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

		absGroup = new JPanel(new BorderLayout(10, 10));
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
		absPane.setPreferredSize(new Dimension(630, 100));
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
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		infoPanel.add(titleGroup);
		infoPanel.add(absGroup);
		infoPanel.add(maGroup);
		infoPanel.add(buttonPanel);


		//set up the optionPanel panel
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
		buttonPanel1 = new JPanel();
		buttonPanel1.setLayout(new BoxLayout(buttonPanel1, BoxLayout.X_AXIS));
		buttonPanel1.setBorder(BorderFactory.createEmptyBorder(0, 250, 0, 0));
		buttonPanel1.add(accept);
		buttonPanel1.add(delay);



		JScrollPane northPane = new JScrollPane(infoPanel);
		northPane.setPreferredSize(new Dimension(730, 300));
		northPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		JScrollPane southPane = new JScrollPane(buttonPanel1);
		southPane.setPreferredSize(new Dimension(730, 100));
		southPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setPreferredSize(new Dimension(730, 600));
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
					//System.out.println(title);
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
							//System.out.println(username + "retired from" + issn);
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