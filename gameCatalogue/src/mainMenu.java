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

    /* Logging in box */
    private JTextField loginNameBox;
    private JTextField loginPasswordBox;
    private JPanel gamePanel;
    private boolean isLoggedIn;

    /* Top 10 games */
    private ArrayList<JButton> gamesButtons;
    private ArrayList<JLabel> gamesLabels;

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
    }

    public void initTopGames() {
        gamesButtons = new ArrayList<JButton>();
        gamesLabels = new ArrayList<JLabel>();

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
                gamesLabels.add(gameInfo);
                gamesButtons.add(createButton(gameId));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for(int i = 0; i < gamesButtons.size(); i++) {
            gamePanel.add(gamesLabels.get(i));
            gamePanel.add(gamesButtons.get(i));
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

        gamePanel = new JPanel();

        drawMenu();
        init();
        initTopGames();

    }

    public void drawMenu() {
        mainPanel.removeAll();
        toolbarPanel.removeAll();
        if(isLoggedIn) {
            toolbarPanel.add(userProfileButton);
        } else {
            toolbarPanel.add(loginButton);
        }

        JTextField test = new JTextField();
        gamePanel.add(test);

        mainPanel.add(toolbarPanel);
        mainPanel.add(gamePanel);
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
