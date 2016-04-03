import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class mainMenu extends JFrame{
    private Connection con;
    private JFrame frame;

    private Integer loggedInUserId;
    private JPanel mainPanel;

    /* Toolbar */
    private JPanel toolbarPanel;
    private JButton userProfileButton;
    private JButton logoutButton;

    /* Search bar */
    private JPanel searchPanel;
    private JButton searchButton;
    private JTextField searchField;
    private String[] options = {"Game", "Platform", "Developer"};
    private JComboBox<String> searchOptions;

    /* Top 10 games */
    private JPanel topGamesPanel;
    private ArrayList<JButton> topGamesButtons;
    private ArrayList<JLabel> topGamesLabels;

    /* Top Games Played by All Users */
    private JPanel allGamesPanel;
    private ArrayList<JButton> allGamesButtons;
    private ArrayList<JLabel> allGamesLabels;

    /* Top 5 Users */
    private JPanel topUsersPanel;
    private ArrayList<JButton> topUsersButtons;
    private ArrayList<JLabel> topUsersLabels;

    public mainMenu(Connection sqlConnection, Integer userId) {
        con = sqlConnection;
        loggedInUserId = userId;

        // TODO: Layouts
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        initToolbar();
        initSearch();
        initTopUsers();
        initGamesByAll();
        initTopGames();
    }

    public void initToolbar() {
        toolbarPanel = new JPanel();
        toolbarPanel.setLayout(new FlowLayout());
        userProfileButton = new JButton("My Profile");
        logoutButton = new JButton("Logout");

        toolbarPanel.add(userProfileButton);
        toolbarPanel.add(logoutButton);

        userProfileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //go to user profile
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //go to login screen again, set logged out
            }
        });
    }

    public void initSearch() {

        searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout());
        searchOptions = new JComboBox<String>(options);
        searchButton = new JButton("Search");
        searchField = new JTextField(30);

        searchPanel.add(searchOptions);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String search = searchField.getText();
                String option = (String) searchOptions.getSelectedItem();
                try {
                    if(!search.isEmpty()) {
                        Statement s = con.createStatement();
                        String query;

                        if (option.contentEquals("Game")){
                            query = "SELECT gameName, gameGenre, pName"
                                + " FROM game g INNER JOIN available a ON g.gameId = a.gameId"
                                + " INNER JOIN platform p ON a.pId = p.pId"
                                + " WHERE UPPER(gameName) LIKE UPPER('%" + search + "%')";
                        }
                        else if (option.contentEquals("Platform")){
                            query = "SELECT pName, cost, releaseDate "
                                    + "FROM platform "
                                    + "WHERE UPPER(pName) LIKE UPPER('%" + search + "%')";
                        }
                        else {
                            query = "SELECT dName "
                                    + "FROM developer "
                                    + "WHERE UPPER(dName) LIKE UPPER('%" + search + "%')";
                        }
                        ResultSet rs = s.executeQuery(query);
                        if (!rs.isBeforeFirst()){
                            System.out.println("Could not find a game matching this query");}
                        else {
                            System.out.println("Found something");
                        }
                    }
                } catch(SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public void initTopUsers() {
        topUsersPanel = new JPanel();
        topUsersButtons = new ArrayList<JButton>();
        topUsersLabels = new ArrayList<JLabel>();

        try {
            Statement s = con.createStatement();
            String query = "SELECT userId, username, avg_rating"
                    + " FROM (SELECT userId, username, AVG(rating) AS avg_rating"
                    + " FROM users u INNER JOIN rate r ON u.userId = r.rated_userId"
                    + " GROUP BY u.userId, username"
                    + " ORDER BY avg_rating)"
                    + " WHERE ROWNUM <= 5";

            ResultSet rs = s.executeQuery(query);
            while(rs.next()) {
                Integer userId = rs.getInt(1);
                String username = rs.getString(2);
                Integer rating = rs.getInt(3);
                JLabel userInfo = new JLabel(username + " " + rating.toString());
                topUsersLabels.add(userInfo);
                topUsersButtons.add(createButtonToUsers(userId));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        topUsersPanel.removeAll();
        final JLabel topUsersLabel = new JLabel("Top 5 Users!");
        topUsersPanel.add(topUsersLabel);
        for(int i = 0; i < topUsersButtons.size(); i++) {
            topUsersPanel.add(topUsersLabels.get(i));
            topUsersPanel.add(topUsersButtons.get(i));
            //TODO: Add Layout-ing
        }
    }

    public void initTopGames() {
        topGamesPanel = new JPanel();
        topGamesButtons = new ArrayList<JButton>();
        topGamesLabels = new ArrayList<JLabel>();

        try {
            Statement s = con.createStatement();
            String query = "SELECT gameId, gameName, gameGenre, avg_rating"
                    + " FROM (SELECT g.gameId, g.gameName, g.gameGenre, AVG(r.rating) AS avg_rating"
                    + " FROM game g INNER JOIN review r ON g.gameId = r.gameId"
                    + " GROUP BY g.gameId, g.gameName, g.gameGenre"
                    + " ORDER BY avg_rating)"
                    + " WHERE ROWNUM <= 10";

            ResultSet rs = s.executeQuery(query);
            while(rs.next()) {
                Integer gameId = rs.getInt(1);
                String gameName = rs.getString(2);
                String gameGenre = rs.getString(3);
                Integer rating = rs.getInt(4);
                JLabel gameInfo = new JLabel(gameName + " " + gameGenre + " " + rating.toString());
                topGamesLabels.add(gameInfo);
                topGamesButtons.add(createButtonToGames(gameId));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        topGamesPanel.removeAll();
        final JLabel topGamesLabel = new JLabel("Top 10 Games!");
        topGamesPanel.add(topGamesLabel);
        for(int i = 0; i < topGamesButtons.size(); i++) {
            topGamesPanel.add(topGamesLabels.get(i));
            topGamesPanel.add(topGamesButtons.get(i));
            // TODO: Layout
        }
    }

    public void initGamesByAll() {
        allGamesPanel = new JPanel();
        allGamesButtons = new ArrayList<JButton>();
        allGamesLabels = new ArrayList<JLabel>();

        try {
            Statement s = con.createStatement();
            String query = "SELECT g.gameId, g.gameName, g.gameGenre"
                    + " FROM game g INNER JOIN owns o ON g.gameId = o.gameId"
                    + " WHERE o.userID IN (SELECT userID"
                    + " FROM users u)"
                    + " GROUP BY g.gameId, gameName, gameGenre"
                    + " HAVING COUNT(*) = (SELECT COUNT(*) FROM users)";
            ResultSet rs = s.executeQuery(query);
            while(rs.next()) {
                Integer gameId = rs.getInt(1);
                String gameName = rs.getString(2);
                String gameGenre = rs.getString(3);
                JLabel gameInfo = new JLabel(gameName + " " + gameGenre);
                allGamesLabels.add(gameInfo);
                allGamesButtons.add(createButtonToGames(gameId));
                System.out.println(gameName + " " + gameGenre);
            }
        } catch(SQLException e1) {
            e1.printStackTrace();
        }

        allGamesPanel.removeAll();
        final JLabel allGamesLabel = new JLabel("Top 5 Games Played By Top 5 Users!");
        allGamesPanel.add(allGamesLabel);
        for(int i = 0; i < allGamesButtons.size(); i++) {
            allGamesPanel.add(allGamesLabels.get(i));
            allGamesPanel.add(allGamesButtons.get(i));
        }
    }

    private JButton createButtonToGames(final Integer gameId) {
        JButton button = new JButton("Details");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("This is the gameId: " + gameId.toString());
            }
        });
        return button;
    }

    private JButton createButtonToUsers(final Integer userId) {
        JButton button = new JButton("Details");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("This is the userId: " + userId.toString());
            }
        });
        return button;
    }

    public void drawMenu(JFrame frame) {
        this.frame = frame;
        mainPanel.add(toolbarPanel);
        mainPanel.add(searchPanel);
        mainPanel.add(topGamesPanel);
        mainPanel.add(allGamesPanel);
        mainPanel.add(topUsersPanel);

        frame.setContentPane(mainPanel);
        frame.revalidate();
        frame.repaint();
    }
}
