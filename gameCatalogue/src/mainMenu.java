import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class MainMenu extends JFrame{
    private Connection con;

    private JFrame frame;
    private JPanel loginPanel;
    private JPanel mainPanel;
    private JPanel toolbarPanel;
    private JButton userProfileButton;
    private JButton loginButton;

    private JButton searchButton;
    private JTextField searchField;
    private String[] options = {"Game", "Platform", "Developer"};
    private JComboBox<String> searchOptions;

    /* Logging in box */
    private JTextField loginNameBox;
    private JTextField loginPasswordBox;
    private boolean isLoggedIn;

    /* Top 10 games */
    private JPanel topGamePanel;
    private ArrayList<JButton> topGamesButtons;
    private ArrayList<JLabel> topGamesLabels;

    /* Top Games Played by All Users */
    private JPanel allGamePanel;
    private ArrayList<JButton> allGamesButtons;
    private ArrayList<JLabel> allGamesLabels;

    public MainMenu() {
        setTitle("Game Catalogue");
        setSize(500,400);
        isLoggedIn = false;
    }

    public void init() {

        userProfileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("WOO\n");
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(null, loginPanel, "Login",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if(result == JOptionPane.OK_OPTION) {
                    String loginName = loginNameBox.getText();
                    String loginPassword = loginPasswordBox.getText();
                    if (!loginName.isEmpty() && !loginPassword.isEmpty()) {
                        try {
                            Statement s = con.createStatement();
                            String query = "SELECT username, password FROM users WHERE username = '" + loginName
                                         + "' AND password = '" + loginPassword + "'";
                            ResultSet rs = s.executeQuery(query);
                            if(!rs.next()) {
                                JOptionPane.showMessageDialog(null, "Incorrect User/Password Combo",
                                        "Incorrect User/Password Combo", JOptionPane.ERROR_MESSAGE);
                            } else {
                                isLoggedIn = true;
                                drawMenu();
                            }
                        } catch(SQLException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Incorrect User/Password Combo",
                                "Incorrect User/Password Combo", JOptionPane.ERROR_MESSAGE);
                    }
                }
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
        topGamePanel.setLayout(layout);
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

        frame = new MainMenu();

        /* Main Menu */
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        toolbarPanel = new JPanel();
        toolbarPanel.setLayout(new FlowLayout());
        userProfileButton = new JButton("My Profile");
        loginButton = new JButton("Login");
        searchOptions = new JComboBox<String>(options);
        searchButton = new JButton("Search");
        searchField = new JTextField(30);

        /* Login Panel */
        loginPanel = new JPanel(new GridLayout(2,2));
        loginNameBox = new JTextField(10);
        loginPasswordBox = new JPasswordField(10);

        final JLabel loginName = new JLabel("Login NAME: ");
        final JLabel loginPassword = new JLabel("Login PASSWORD: ");
        loginPanel.add(loginName);
        loginPanel.add(loginNameBox);
        loginPanel.add(loginPassword);
        loginPanel.add(loginPasswordBox);

        topGamePanel = new JPanel();
        allGamePanel = new JPanel();

        init();
        initTopGames();
        initGamesByAll();
        drawMenu();

    }

    public void drawMenu() {
        mainPanel.removeAll();
        toolbarPanel.removeAll();
        if(isLoggedIn) {
            toolbarPanel.add(userProfileButton);
        } else {
            toolbarPanel.add(loginButton);
        }
        toolbarPanel.add(searchOptions);
        toolbarPanel.add(searchField);
        toolbarPanel.add(searchButton);

        mainPanel.add(toolbarPanel);
        mainPanel.add(topGamePanel);
        frame.setContentPane(mainPanel);
        frame.setResizable(true);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        MainMenu m = new MainMenu();
        m.run();
    }
}
