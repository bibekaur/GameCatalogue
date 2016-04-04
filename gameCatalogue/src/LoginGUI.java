import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginGUI extends JFrame {
    private Connection con;

    private JFrame frame;

    private JPanel loginPanel;
    private JTextField loginNameBox;
    private JTextField loginPasswordBox;
    private JButton loginButton;
    private JButton signUpButton;

    public LoginGUI(Connection sqlConnection) {
        con = sqlConnection;
        init();
    }

    private void init() {
        loginPanel = new JPanel();

        loginNameBox = new JTextField(10);
        loginPasswordBox = new JPasswordField(10);
        loginButton = new JButton("Login");
        signUpButton = new JButton("Sign-up");

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String loginName = loginNameBox.getText();
                String loginPassword = loginPasswordBox.getText();
                System.out.println("logged in as "+loginName+" with password: "+loginPassword);
                if (!loginName.isEmpty() && !loginPassword.isEmpty()) {
                    //TODO See if the user exists from the database
                    System.out.println("Login Got here!");

                    try {
                        Statement s = con.createStatement();
                        String query = "SELECT * FROM users WHERE username = '" + loginName + "' AND password = '" + loginPassword + "'";
                        //Below line checks if it exists or not
                        ResultSet rs = s.executeQuery(query);
                        if (!rs.next() ) {
                            JOptionPane.showMessageDialog(null, "This username and password combination could not be found.");
                        }
                        else {
                            Integer loggedInUserId = rs.getInt(1);
                            mainMenu m = new mainMenu(con, loggedInUserId);
                            m.drawMenu(frame);
                        }
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid Username/Password combination.");
                }
            }
        });

        signUpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String loginName = loginNameBox.getText();
                String loginPassword = loginPasswordBox.getText();
                System.out.println("signed up as "+loginName+" with password: "+loginPassword);
                if (!loginName.isEmpty() && !loginPassword.isEmpty()) {
                    //TODO add user info in the database
                    System.out.println("Signup Got here!");
                    try{
                        Statement s = con.createStatement();
                        String query = "SELECT username FROM users WHERE username = '" + loginName + "'";
                        ResultSet rs = s.executeQuery(query);
                        if (rs.next()){
                            JOptionPane.showMessageDialog(null, "This username is already used.");
                        }
                        else {
                            String insertionQuery = "INSERT INTO users VALUES(DEFAULT, '" + loginName + "', '" + loginPassword + "', CURRENT_TIMESTAMP, 0)";
                            s.executeUpdate(insertionQuery);
                            // Draw the main menu
                            query = "SELECT * FROM users WHERE username = '" + loginName + "' AND password = '" + loginPassword + "'";
                            rs = s.executeQuery(query);
                            if(rs.next()) {
                                Integer userId = rs.getInt(1);
                                mainMenu m = new mainMenu(con, userId);
                                m.drawMenu(frame);
                            }
                        }

                    }catch (SQLException e1){
                        e1.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Username and/or Password is empty.");
                }
            }
        });
    }
    public void setPanel(JFrame frame) {
        final JLabel loginName = new JLabel("Login NAME: ");
        final JLabel loginPassword = new JLabel("Login PASSWORD: ");
        loginPanel.add(loginName);
        loginPanel.add(loginNameBox);
        loginPanel.add(loginPassword);
        loginPanel.add(loginPasswordBox);
        loginPanel.add(loginButton);
        loginPanel.add(signUpButton);

        this.frame = frame;

        frame.setContentPane(loginPanel);
        frame.revalidate();
        frame.repaint();
    }
}
