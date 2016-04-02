import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

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

        gamePanel = new JPanel();

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

        drawMenu();
        init();

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
