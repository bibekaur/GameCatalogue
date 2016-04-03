import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class MainMenu extends JFrame{
    private Connection con;

    private Integer loggedInUserId;
    private JFrame frame;
    private JPanel loginPanel;
    private JPanel mainPanel;
    private JPanel toolbarPanel;
    private JButton userProfileButton;

    private JButton searchButton;
    private JTextField searchField;
    private String[] options = {"Game", "Platform", "Developer"};
    private JComboBox<String> searchOptions;

    /* Top 10 games */
    private JPanel topGamePanel;
    private ArrayList<JButton> topGamesButtons;
    private ArrayList<JLabel> topGamesLabels;

    /* Top Games Played by All Users */
    private JPanel allGamePanel;
    private ArrayList<JButton> allGamesButtons;
    private ArrayList<JLabel> allGamesLabels;

    /* Top 5 Users */
    private JPanel topUserPanel;
    private ArrayList<JButton> topUsersButtons;
    private ArrayList<JLabel> topUsersLabels;

    public MainMenu(Integer userId) {
        setTitle("Game Catalogue");
        setSize(500,400);
        loggedInUserId = userId;
    }

    public void init() {

        userProfileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("WOO\n");
            }
        });

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
                topUsersButtons.add(createButton(userId));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        topUserPanel.removeAll();
        final JLabel topUsersLabel = new JLabel("Top 5 Users!");
        topUserPanel.add(topUsersLabel);
        for(int i = 0; i < topUsersButtons.size(); i++) {
            topUserPanel.add(topUsersLabels.get(i));
            topUserPanel.add(topUsersButtons.get(i));
            //TODO: Add Layout-ing
        }
    }

    public void initTopGames() {
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
                topGamesButtons.add(createButton(gameId));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        topGamePanel.removeAll();
        final JLabel topGamesLabel = new JLabel("Top 10 Games!");
        SpringLayout layout = new SpringLayout();
        topGamePanel.add(topGamesLabel);
        for(int i = 0; i < topGamesButtons.size(); i++) {
            topGamePanel.add(topGamesLabels.get(i));
            topGamePanel.add(topGamesButtons.get(i));
            if(i == 0) {
                layout.putConstraint(SpringLayout.NORTH, topGamesLabels.get(i), 50, SpringLayout.SOUTH, topGamesLabel);
                layout.putConstraint(SpringLayout.NORTH, topGamesButtons.get(i), 40, SpringLayout.SOUTH, topGamesLabel);
            } else {
                layout.putConstraint(SpringLayout.NORTH, topGamesLabels.get(i), 40, SpringLayout.SOUTH, topGamesLabels.get(i-1));
                layout.putConstraint(SpringLayout.NORTH, topGamesButtons.get(i), 40, SpringLayout.SOUTH, topGamesButtons.get(i-1));
            }
            layout.putConstraint(SpringLayout.WEST, topGamesButtons.get(i), 2, SpringLayout.EAST, topGamesLabels.get(i));
        }
        //topGamePanel.setLayout(layout);
    }

    public void initGamesByAll() {
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
                allGamesButtons.add(createButton(gameId));
                System.out.println(gameName + " " + gameGenre);
            }
        } catch(SQLException e1) {
            e1.printStackTrace();
        }

        allGamePanel.removeAll();
        final JLabel allGamesLabel = new JLabel("Top 5 Games Played By Top 5 Users!");
        SpringLayout layout = new SpringLayout();
        allGamePanel.add(allGamesLabel);
        for(int i = 0; i < allGamesButtons.size(); i++) {
            allGamePanel.add(allGamesLabels.get(i));
            allGamePanel.add(allGamesButtons.get(i));
            if(i == 0) {
                layout.putConstraint(SpringLayout.NORTH, allGamesLabels.get(i), 50, SpringLayout.SOUTH, allGamesLabel);
                layout.putConstraint(SpringLayout.NORTH, allGamesButtons.get(i), 40, SpringLayout.SOUTH, allGamesLabel);
            } else {
                layout.putConstraint(SpringLayout.NORTH, allGamesLabels.get(i), 40, SpringLayout.SOUTH, allGamesLabels.get(i-1));
                layout.putConstraint(SpringLayout.NORTH, allGamesButtons.get(i), 40, SpringLayout.SOUTH, allGamesButtons.get(i-1));
            }
            layout.putConstraint(SpringLayout.WEST, allGamesButtons.get(i), 2, SpringLayout.EAST, allGamesLabels.get(i));
        }
        //allGamePanel.setLayout(layout);
    }

    public JButton createButton(Integer gameId) {
        JButton button = new JButton("Details");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("This is the gameId: " + gameId.toString());
            }
        });
        return button;
    }

    public void run() {

        try{
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            //Change the below line to match your oracle username/password
            con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1522:ug", "ora_b8k8", "a33858127");
        }catch(Exception e){
            e.printStackTrace();
        }

        frame = this;

        /* Main Menu */
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        toolbarPanel = new JPanel();
        toolbarPanel.setLayout(new FlowLayout());
        userProfileButton = new JButton("My Profile");
        searchOptions = new JComboBox<String>(options);
        searchButton = new JButton("Search");
        searchField = new JTextField(30);

        topGamePanel = new JPanel();
        allGamePanel = new JPanel();
        topUserPanel = new JPanel();

        init();
        initTopGames();
        initGamesByAll();
        initTopUsers();
        drawMenu();

    }

    public void drawMenu() {
        mainPanel.removeAll();
        toolbarPanel.removeAll();
        toolbarPanel.add(userProfileButton);
        toolbarPanel.add(searchOptions);
        toolbarPanel.add(searchField);
        toolbarPanel.add(searchButton);

        mainPanel.add(toolbarPanel);
        mainPanel.add(topGamePanel);
        mainPanel.add(allGamePanel);
        mainPanel.add(topUserPanel);
        frame.setContentPane(mainPanel);
        frame.setResizable(true);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        MainMenu m = new MainMenu(1);
        m.run();
    }
}
