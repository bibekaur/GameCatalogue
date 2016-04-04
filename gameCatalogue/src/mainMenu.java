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
    private JButton createButton;

    /* Search bar */
    private JPanel searchPanel;
    private JButton searchButton;
    private JTextField searchField;
    private String[] options = {"Game", "Platform", "Developer", "User"};
    private JComboBox<String> searchOptions;
    
    /*Results from Search*/
    private JPanel resultsPanel;

    /* Top 10 games */
    private JPanel topGamesPanel;
    private ArrayList<JButton> topGamesButtons;
    private ArrayList<JLabel> topGamesRatings;
    private ArrayList<JLabel> topGamesNames;

    /* Top Games Played by All Users */
    private JPanel allGamesPanel;
    private ArrayList<JButton> allGamesButtons;
    private ArrayList<JLabel> allGamesNames;
    private ArrayList<JLabel> allGamesGenres;

    /* Top 5 Users */
    private JPanel topUsersPanel;
    private ArrayList<JButton> topUsersButtons;
    private ArrayList<JLabel> topUsersNames;
    private ArrayList<JLabel> topUsersRatings;

    public mainMenu(Connection sqlConnection, Integer userId) {
        con = sqlConnection;
        loggedInUserId = userId;

        mainPanel = new JPanel();

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
        
        if (isModerator()){
        	createButton = new JButton("Create");
        	toolbarPanel.add(createButton);
        	createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                InputFormGUI i = new InputFormGUI(con, loggedInUserId);
                i.setPanel(frame);
            }
        });
        }
        
        toolbarPanel.add(logoutButton);

        userProfileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UserProfileGUI temp = new UserProfileGUI(con, loggedInUserId);
                temp.setPanel(loggedInUserId, frame);
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoginGUI g = new LoginGUI(con);
                g.setPanel(frame);
            }
        });
        

        
    }
    
    private boolean isModerator(){
    	try {
    		Statement s = con.createStatement();
    		String query = "SELECT isModerator from users WHERE userId=" + loggedInUserId;
    		ResultSet rs = s.executeQuery(query);
    		
    		rs.next();
    		String mod = rs.getString("isModerator");
    		
    		if (mod.equals("1")){
    			return true;
    		}
    		
    	} catch (SQLException e1){
			e1.printStackTrace();
		}
    	
    	return false;
    }

    public void initSearch() {

        resultsPanel = new JPanel();
        GridBagLayout resultsLayout = new GridBagLayout();
        resultsPanel.setLayout(resultsLayout);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

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
                            query = "SELECT g.gameId AS id, gameName, gameGenre, pName"
                                + " FROM game g INNER JOIN available a ON g.gameId = a.gameId"
                                + " INNER JOIN platform p ON a.pId = p.pId"
                                + " WHERE UPPER(gameName) LIKE UPPER('%" + search + "%')";
                        }
                        else if (option.contentEquals("Platform")){
                            query = "SELECT pId AS id, pName, cost, releaseDate "
                                    + "FROM platform "
                                    + "WHERE UPPER(pName) LIKE UPPER('%" + search + "%')";
                        }

                        else if (option.contentEquals("Developer")){
                            query = "SELECT dId AS id, dName "
                                    + "FROM developer "
                                    + "WHERE UPPER(dName) LIKE UPPER('%" + search + "%')";
                        }
                        else {
                            query = "SELECT userId AS id, username"
                                    + " FROM users"
                                    + " WHERE UPPER(username) LIKE UPPER('%" + search + "%')";
                        }
                        ResultSet rs = s.executeQuery(query);
                        if (!rs.isBeforeFirst()){
                            System.out.println("Could not find a game matching this query");}
                        else {
                            if (resultsPanel.getComponents() != null){
                                resultsPanel.removeAll();
                            }
                            try{
                                int index = 0;

                                c.gridx = 0;
                                c.gridy = 0;
                                JLabel resultsColNameLabel = new JLabel("Name", SwingConstants.CENTER);
                                resultsPanel.add(resultsColNameLabel, c);

                                if(option.contentEquals("Game")) {
                                    JLabel resultsColPlatLabel = new JLabel("Platform", SwingConstants.CENTER);
                                    c.gridx = 1;
                                    resultsPanel.add(resultsColPlatLabel, c);
                                }
                                while(rs.next() && index < 10){
                                    Integer id = rs.getInt(1);
                                    String name = rs.getString(2);
                                    JLabel nameLabel = new JLabel(name, SwingConstants.CENTER);
                                    JButton resultButton;
                                    JLabel platLabel = null;
                                    if(option.contentEquals("Game")) {
                                        resultButton = createButtonToGames(id);
                                        String platName = rs.getString(4);
                                        platLabel = new JLabel(platName, SwingConstants.CENTER);
                                    } else if(option.contentEquals("Platform")) {
                                        resultButton = createButtonToPlatforms(id);
                                    } else if(option.contentEquals("Developers")) {
                                        resultButton = createButtonToDevelopers(id);
                                    } else {
                                        resultButton = createButtonToUsers(id);
                                    }

                                    if(option.contentEquals("Game")) {
                                        c.gridx = 0;
                                        c.gridy = 1+index;
                                        resultsPanel.add(nameLabel, c);

                                        c.gridx = 1;
                                        resultsPanel.add(platLabel, c);

                                        c.gridx = 2;
                                        resultsPanel.add(resultButton, c);
                                    } else {
                                        c.gridx = 0;
                                        c.gridy = 1 + index;
                                        resultsPanel.add(nameLabel, c);

                                        c.gridx = 1;
                                        resultsPanel.add(resultButton, c);
                                    }

                                    index++;
                                }
                                frame.revalidate();
                                frame.repaint();
                            }catch (SQLException e1){
                                e1.printStackTrace();
                            }
                        }
                    }
                } catch(SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    private void initTopUsers() {
        topUsersPanel = new JPanel();
        topUsersButtons = new ArrayList<JButton>();
        topUsersNames = new ArrayList<JLabel>();
        topUsersRatings = new ArrayList<JLabel>();

        GridBagLayout layout = new GridBagLayout();
        topUsersPanel.setLayout(layout);
        GridBagConstraints c = new GridBagConstraints();

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
                JLabel userName = new JLabel(username, SwingConstants.CENTER);
                JLabel userRating = new JLabel(rating.toString() + "/10", SwingConstants.CENTER);
                //JLabel userInfo = new JLabel(username + " " + rating.toString());
                topUsersNames.add(userName);
                topUsersRatings.add(userRating);
                topUsersButtons.add(createButtonToUsers(userId));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        c.fill = GridBagConstraints.HORIZONTAL;
        final JLabel topUsersLabel = new JLabel("Top 5 Users!", SwingConstants.CENTER);
        c.gridx = 1;
        c.gridy = 0;
        topUsersPanel.add(topUsersLabel, c);

        c.gridx = 0;
        c.gridy = 1;
        topUsersPanel.add(new JLabel("User", SwingConstants.CENTER), c);

        c.gridx = 1;
        c.gridy = 1;
        topUsersPanel.add(new JLabel("Rating", SwingConstants.CENTER), c);

        c.gridx = 2;
        c.gridy = 1;
        topUsersPanel.add(new JLabel(" ", SwingConstants.CENTER), c);

        for(int i = 0; i < topUsersButtons.size(); i++) {
            c.gridx = 0;
            c.gridy = 3+i;
            topUsersPanel.add(topUsersNames.get(i), c);

            c.gridx = 1;
            topUsersPanel.add(topUsersRatings.get(i), c);

            c.gridx = 2;
            topUsersPanel.add(topUsersButtons.get(i));
        }
    }

    private void initTopGames() {
        topGamesPanel = new JPanel();
        topGamesButtons = new ArrayList<JButton>();
        topGamesNames = new ArrayList<JLabel>();
        topGamesRatings = new ArrayList<JLabel>();
        GridBagLayout layout = new GridBagLayout();
        topGamesPanel.setLayout(layout);
        GridBagConstraints c = new GridBagConstraints();

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
                Integer rating = rs.getInt(4);
                JLabel labelName = new JLabel(gameName, SwingConstants.CENTER);
                JLabel labelRating = new JLabel(rating.toString() + "/10", SwingConstants.CENTER);
                topGamesNames.add(labelName);
                topGamesRatings.add(labelRating);
                topGamesButtons.add(createButtonToGames(gameId));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        final JLabel topGamesLabel = new JLabel("Top 10 Games!");
        c.fill = GridBagConstraints.HORIZONTAL;
        //c.anchor = GridBagConstraints.CENTER;
        c.gridx = 1;
        c.gridy = 0;
        topGamesPanel.add(topGamesLabel, c);

        c.gridx = 0;
        c.gridy = 1;
        topGamesPanel.add(new JLabel("Games", SwingConstants.CENTER), c);

        c.gridx = 1;
        c.gridy = 1;
        topGamesPanel.add(new JLabel("Rating", SwingConstants.CENTER), c);

        c.gridx = 2;
        c.gridy = 1;
        topGamesPanel.add(new JLabel(" "), c);

        for(int i = 0; i < topGamesButtons.size(); i++) {
            c.gridx = 0;
            c.gridy = 3+i;
            topGamesPanel.add(topGamesNames.get(i), c);

            c.gridx = 1;
            topGamesPanel.add(topGamesRatings.get(i), c);

            c.gridx = 2;
            topGamesPanel.add(topGamesButtons.get(i), c);
        }
    }

    private void initGamesByAll() {
        allGamesPanel = new JPanel();
        allGamesButtons = new ArrayList<JButton>();
        allGamesNames = new ArrayList<JLabel>();
        allGamesGenres = new ArrayList<JLabel>();

        GridBagLayout layout = new GridBagLayout();
        allGamesPanel.setLayout(layout);
        GridBagConstraints c = new GridBagConstraints();

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
                allGamesNames.add(new JLabel(gameName, SwingConstants.CENTER));
                allGamesGenres.add(new JLabel(gameGenre, SwingConstants.CENTER));
                allGamesButtons.add(createButtonToGames(gameId));
                System.out.println(gameName + " " + gameGenre);
            }
        } catch(SQLException e1) {
            e1.printStackTrace();
        }

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        final JLabel allGamesLabel = new JLabel("Top 5 Games Played By All Users!", SwingConstants.CENTER);
        allGamesPanel.add(allGamesLabel, c);

        c.gridx = 0;
        c.gridy = 1;
        allGamesPanel.add(new JLabel("Games", SwingConstants.CENTER), c);

        c.gridx = 1;
        c.gridy = 1;
        allGamesPanel.add(new JLabel("Genre", SwingConstants.CENTER), c);

        c.gridx = 2;
        c.gridy = 1;
        allGamesPanel.add(new JLabel(" "), c);
        for(int i = 0; i < allGamesButtons.size(); i++) {
            c.gridx = 0;
            c.gridy = 3+i;
            allGamesPanel.add(allGamesNames.get(i), c);

            c.gridx = 1;
            allGamesPanel.add(allGamesGenres.get(i), c);

            c.gridx = 2;
            allGamesPanel.add(allGamesButtons.get(i), c);
        }
    }

    private JButton createButtonToGames(final Integer gameId) {
        JButton button = new JButton("Info");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("This is the gameId: " + gameId.toString());
                GameInfoGUI temp = new GameInfoGUI(gameId, loggedInUserId, con);
                temp.setPanel(frame);
            }
        });
        return button;
    }

    private JButton createButtonToUsers(final Integer userId) {
        JButton button = new JButton("Info");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("This is the userId: " + userId.toString());
                UserProfileGUI temp = new UserProfileGUI(con, userId);
                temp.setPanel(loggedInUserId, frame);
            }
        });
        return button;
    }

    private JButton createButtonToDevelopers(final Integer devId) {
        JButton button = new JButton("Info");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("This is the devId: " + devId.toString());
                DeveloperInfoGUI temp = new DeveloperInfoGUI(con, devId);
                temp.setPanel(loggedInUserId, frame);
            }
        });
        return button;
    }

    private JButton createButtonToPlatforms(final Integer platId) {
        JButton button = new JButton("Info");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("This is the platId: " + platId.toString());
                PlatformInfoGUI temp = new PlatformInfoGUI(platId, loggedInUserId, con);
                temp.setPanel(frame);
            }
        });
        return button;
    }

    public void drawMenu(JFrame frame) {
        this.frame = frame;
        mainPanel.add(toolbarPanel);
        mainPanel.add(searchPanel);
        mainPanel.add(resultsPanel);
        mainPanel.add(topGamesPanel);
        mainPanel.add(allGamesPanel);
        mainPanel.add(topUsersPanel);

        SpringLayout layout = new SpringLayout();
        layout.putConstraint(SpringLayout.NORTH, searchPanel, 0, SpringLayout.SOUTH, toolbarPanel);
        layout.putConstraint(SpringLayout.NORTH, resultsPanel, 0, SpringLayout.SOUTH, searchPanel);
        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, resultsPanel, 0, SpringLayout.HORIZONTAL_CENTER, searchPanel);
        layout.putConstraint(SpringLayout.NORTH, topUsersPanel, 10, SpringLayout.SOUTH, resultsPanel);
        layout.putConstraint(SpringLayout.NORTH, topGamesPanel, 10, SpringLayout.SOUTH, resultsPanel);
        layout.putConstraint(SpringLayout.NORTH, allGamesPanel, 20, SpringLayout.SOUTH, topUsersPanel);
        layout.putConstraint(SpringLayout.WEST, topGamesPanel, 10, SpringLayout.EAST, topUsersPanel);
        layout.putConstraint(SpringLayout.WEST, topGamesPanel, 10, SpringLayout.EAST, allGamesPanel);
        mainPanel.setLayout(layout);

        frame.setContentPane(mainPanel);
        frame.revalidate();
        frame.repaint();
    }
}
