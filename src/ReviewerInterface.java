/**
 * Class for Reviewer Interface
 * @author Ting Guo
 **/

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;

public class ReviewerInterface extends JFrame implements ActionListener {

    public static void main(String[] args) throws SQLException, IOException {
        new ReviewerInterface("chaddock@illinois.ac.uk");
    }

    private static final long serialVersionUID = 1L;
    private JPanel reviewerPanel;
    private CardLayout cardLayout = new CardLayout();
    private String username;

    // menubar
    private JMenuBar menubar;
    private JMenu settings;
    private JMenuItem availableArticle, articleSelection, responses,changePw, updatePf, logOut;

    private String[] columnNames;

    //components in AvailableArticleTable()
    private File article;
    private Desktop desktop;
    private JPanel availableArticlePanel;
    private JTable availableArticleTable;
    private LinkedList<Submission> submissionsToReview;
    private int remaining;
    private JLabel remainingLabel;

    //components in ArticleSelectionTable()
    private JTable selectedArticlesTable;
    private LinkedList<Submission> selectedArticles;
    //components in ResponsesPanel()
    private LinkedList<Submission> responded;
    private JTable respondedTable;


    public ReviewerInterface(String username) throws SQLException, IOException {
        this.setTitle("Reviewer Interface");
        this.setSize(1000, 600);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.username = username;

        desktop = Desktop.getDesktop();
        columnNames = new String[]{"Journal", "Title", "Linked PDF", "Selection", "SubmissionId"};

        remaining = ReviewController.remainingCostToCover(username);
        responded = ReviewController.getSubmissionsResponded(username);


        //Add panels and set constraints.
        reviewerPanel = new JPanel();
        reviewerPanel.setLayout(cardLayout);
        reviewerPanel.add(availableArticleTable(username),"Articles Available");
        reviewerPanel.add(articleSelectionTable(username),"Selected Articles");
        reviewerPanel.add(responsesPanel(username),"Responses");



        //menu bar
        menubar = new JMenuBar();
        settings = new JMenu("Settings");
        //articles = new JMenuItem("Articles");
        availableArticle = new JMenuItem("Articles Available to Review");
        articleSelection = new JMenuItem("Selected Articles");
        responses = new JMenuItem("Responses");
        changePw = new JMenuItem("Change Password");
        updatePf = new JMenuItem("Update Profile");
        logOut = new JMenuItem("Log out");

        availableArticle.addActionListener(this);
        if (remaining == 0) { availableArticle.setEnabled(false);}
        articleSelection.addActionListener(this);
        responses.addActionListener(this);
        if (responded.size() == 0) { responses.setEnabled(false);}
        changePw.addActionListener(this);
        updatePf.addActionListener(this);
        logOut.addActionListener(this);

        settings.add(changePw);
        settings.add(updatePf);

        menubar.add(settings);
        menubar.add(availableArticle);
        menubar.add(articleSelection);
        menubar.add(responses);
        menubar.add(logOut);

        this.setJMenuBar(menubar);

        //check remaining cost
        //if remaining more than 3 than continue selecting
        if (remaining != 0){
            cardLayout.show(reviewerPanel, "Articles Available");
        }
        //if drops to 0, jump to selection panel to review
        else if (remaining == 0) {
            cardLayout.show(reviewerPanel, "Selected Articles");
        }

        this.add(reviewerPanel);
        //extra settings
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }


    public JScrollPane availableArticleTable(String username) throws SQLException, IOException {
        submissionsToReview = new LinkedList<>(ReviewController.getSubmissionsToReview(username));

        DefaultTableModel model = new DefaultTableModel(columnNames,0);
        for (Submission sub : submissionsToReview) {
            int submissionId = sub.getSubmissionID();
            System.out.println(submissionId);
            String journalTitle = JournalController.getJournalByArticle(submissionId).getTitle();
            String articleTitle = ArticleController.getArticle(submissionId).getTitle();
            String openPDF = "Open PDF";
            String select = "Select to Review";
            model.addRow(new Object[]{journalTitle, articleTitle, openPDF, select,submissionId});
        }

        availableArticleTable = new JTable(model);

        availableArticleTable.getColumnModel().getColumn(4).setMinWidth(0);
        availableArticleTable.getColumnModel().getColumn(4).setMaxWidth(0);
        availableArticleTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        availableArticleTable.getColumnModel().getColumn(0).setPreferredWidth(300);
        availableArticleTable.getColumnModel().getColumn(1).setPreferredWidth(450);
        availableArticleTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        availableArticleTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        availableArticleTable.getColumnModel().getColumn(4).setPreferredWidth(0);

        availableArticleTable.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent me) {
                int row = availableArticleTable.rowAtPoint(me.getPoint());
                int col = availableArticleTable.columnAtPoint(me.getPoint());
                int id = (int) availableArticleTable.getValueAt(row,4);
                System.out.println("You clicked at row " + row);
                System.out.println("You clicked at col " + col);
                if (col == 2) {
                    try {
                        System.out.println(id);
                        ArticleController.getSubmissionPDF(id);
                        File article = new File("article.pdf");
                        if (!Desktop.isDesktopSupported()) {
                            JOptionPane.showMessageDialog(null, "Desktop does not support this function");
                        } else if (article.exists()) {
                            desktop.open(article);
                        }
                    } catch (IOException | SQLException ex) {
                        ex.printStackTrace();
                    }
                } else if (col == 3) {
                    try {
                        ReviewController.selectToReview(username, id);
                        availableArticleTable.setValueAt("Selected",row,3);
                        JOptionPane.showMessageDialog(null,"You have selected to review "+
                                availableArticleTable.getValueAt(row,1) + " successfully!");
                        SwingUtilities.getWindowAncestor(availableArticleTable).dispose();
                        new ReviewerInterface(username);
                    } catch (SQLException | IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Select to review is clicked");
                }
            }
        });
        remainingLabel = new JLabel("Remaining articles: " + remaining);
        add(remainingLabel, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(availableArticleTable);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        return scrollPane;
    }


    public JScrollPane articleSelectionTable(String username) throws SQLException, IOException {
        selectedArticles = new LinkedList<>(ReviewController.getSubmissionsSelected(username));

        DefaultTableModel model = new DefaultTableModel(columnNames,0);
        for (Submission sub : selectedArticles) {
            int submissionId = sub.getSubmissionID();
            System.out.println(submissionId);
            String journalTitle = JournalController.getJournalByArticle(submissionId).getTitle();
            String articleTitle = ArticleController.getArticle(submissionId).getTitle();
            String openPDF = "Open PDF";
            String select = "Submit a Review";
            model.addRow(new Object[]{journalTitle, articleTitle, openPDF, select, submissionId});
        }

        selectedArticlesTable = new JTable(model);

        selectedArticlesTable.getColumnModel().getColumn(4).setMinWidth(0);
        selectedArticlesTable.getColumnModel().getColumn(4).setMaxWidth(0);
        selectedArticlesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        selectedArticlesTable.getColumnModel().getColumn(0).setPreferredWidth(300);
        selectedArticlesTable.getColumnModel().getColumn(1).setPreferredWidth(450);
        selectedArticlesTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        selectedArticlesTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        selectedArticlesTable.getColumnModel().getColumn(4).setPreferredWidth(0);

        selectedArticlesTable.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent me) {
                int row = selectedArticlesTable.rowAtPoint(me.getPoint());
                int col = selectedArticlesTable.columnAtPoint(me.getPoint());
                int id = (int) selectedArticlesTable.getValueAt(row,4);
                System.out.println("You clicked at row " + row);
                System.out.println("You clicked at col " + col);
                if (col == 2) {
                    try {
                        System.out.println(id);
                        ArticleController.getSubmissionPDF(id);
                        File article = new File("article.pdf");
                        if (!Desktop.isDesktopSupported()) {
                            JOptionPane.showMessageDialog(null, "Desktop does not support this function");
                        } else if (article.exists()) {
                            desktop.open(article);
                        }
                    } catch (IOException | SQLException ex) {
                        ex.printStackTrace();
                    }
                } else if (col == 3) {
                    try {
                        String anonId = ReviewController.getAnonID(username, id);//get anonId
                        selectedArticlesTable.setValueAt("Selected",row,3);
                        new SubmitReviewInterface(username, id, anonId);
                        SwingUtilities.getWindowAncestor(selectedArticlesTable).dispose();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Select to review is clicked");
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(selectedArticlesTable);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return scrollPane;
    }

    public JScrollPane responsesPanel(String username) throws SQLException, IOException {
        responded = new LinkedList<>(ReviewController.getSubmissionsResponded(username));

        DefaultTableModel model = new DefaultTableModel(columnNames,0);
        for (Submission sub : responded) {
            int submissionId = sub.getSubmissionID();
            System.out.println(submissionId);
            String journalTitle = JournalController.getJournalByArticle(submissionId).getTitle();
            String articleTitle = ArticleController.getArticle(submissionId).getTitle();
            String openPDF = "Open PDF";
            String select = "Submit Final Verdict";
            model.addRow(new Object[]{journalTitle, articleTitle, openPDF, select, submissionId});
        }

        respondedTable = new JTable(model);

        respondedTable.getColumnModel().getColumn(4).setMinWidth(0);
        respondedTable.getColumnModel().getColumn(4).setMaxWidth(0);
        respondedTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        respondedTable.getColumnModel().getColumn(0).setPreferredWidth(300);
        respondedTable.getColumnModel().getColumn(1).setPreferredWidth(450);
        respondedTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        respondedTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        respondedTable.getColumnModel().getColumn(4).setPreferredWidth(0);

        respondedTable.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent me) {
                int row = respondedTable.rowAtPoint(me.getPoint());
                int col = respondedTable.columnAtPoint(me.getPoint());
                int id = (int) respondedTable.getValueAt(row,4);
                System.out.println("You clicked at row " + row);
                System.out.println("You clicked at col " + col);
                if (col == 2) {
                    try {
                        System.out.println(id);
                        ArticleController.getSubmissionPDF(id);
                        File article = new File("article.pdf");
                        if (!Desktop.isDesktopSupported()) {
                            JOptionPane.showMessageDialog(null, "Desktop does not support this function");
                        } else if (article.exists()) {
                            desktop.open(article);
                        }
                    } catch (IOException | SQLException ex) {
                        ex.printStackTrace();
                    }
                } else if (col == 3) {
                    try {
                        String anonId = ReviewController.getAnonID(username, id);//get anonId
                        respondedTable.setValueAt("Selected",row,3);
                        new FinalVerdictInterface(username, id, anonId);
                        SwingUtilities.getWindowAncestor(respondedTable).dispose();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(respondedTable);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return scrollPane;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        //availableArticle, articleSelection, responses, logOut
        if (e.getSource() == responses) {
            cardLayout.show(reviewerPanel, "Responses");
            System.out.println("responses clicked");
        } else if (e.getSource() == articleSelection) {
            cardLayout.show(reviewerPanel, "Selected Articles");
        } else if (e.getSource() == changePw) {
            new ChangePw(username,3);
            this.dispose();
        } else if (e.getSource() == updatePf) {
            new UpdateProfileInterface(username, 3, false);
            this.dispose();
        }
    }


}