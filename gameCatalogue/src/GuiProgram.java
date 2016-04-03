import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GuiProgram extends JFrame{
	private Connection con;
	
	private JFrame frame;

	private JPanel mainPanel;
	private JPanel resultsPanel;
	
	private UserProfileGUI userGUI;
	private GameInfoGUI gameGUI;
	private PlatformInfoGUI platformGUI;
	private JPanel UserPanel;
	private JPanel GamePanel;
	private String loginName;
    private String loginPassword;
    private Integer loggedInUserId;
    
	private JTextField loginNameBox;
	private JTextField loginPasswordBox;
	private JButton loginButton;
	private JButton signUpButton;
	private JButton searchGameName;
	private JButton searchDeveloperName;
	private JButton searchPlatformName;
	private JButton searchGameViaDeveloper;
	private String[] options = {"Game", "Platform", "Developer"};
	private JComboBox<String> searchOptions;
	
	private JTextField searchField;
	
	public GuiProgram() {
		setTitle("Game Catalogue");
		setSize(500,400); // default size is 0,0
		setLocation(400,200); // default is 0,0 (top left corner)
	}
	
	public void drawLoggedInScreen(){
		frame.remove(mainPanel);
		
		/* Comment out lines below when you want to test a specific GUI */
	
		//gameGUI = new GameInfoGUI(1, 2, con);
		//gameGUI.setPanel(frame);
		//DeveloperInfoGUI devGUI = new DeveloperInfoGUI(con, 5);
		//devGUI.setPanel(loggedInUserId, frame);
		//platformGUI = new PlatformInfoGUI(1, loggedInUserId, con);
		//platformGUI.setPanel(frame);
		//userGUI = new UserProfileGUI(con, 3);
		//userGUI.setPanel(loggedInUserId, frame);
		

		mainPanel = new JPanel();
		mainPanel.add(searchField);
		mainPanel.add(searchOptions);
		mainPanel.add(searchGameName);
		frame.setContentPane(mainPanel);
		frame.revalidate();
		frame.repaint();
	}
	
	//Splitting it into initializing what the buttons do
	public void init(){
		
	    loginButton.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) { 
			    loginName = loginNameBox.getText();
			    loginPassword = loginPasswordBox.getText();
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
							loggedInUserId = rs.getInt(1);
							//drawLoggedInScreen();
							MainMenu m = new MainMenu(con, loggedInUserId);
							m.drawMenu(frame);
						}
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
	    });
	    signUpButton.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) { 
				loginName = loginNameBox.getText();
				loginPassword = loginPasswordBox.getText();
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
							drawLoggedInScreen();
						}

					}catch (SQLException e1){
						e1.printStackTrace();
					}
				}
	    	}
	    });
	    
	    searchGameName.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent e){
	    		String search = searchField.getText();
	    		String option = (String) searchOptions.getSelectedItem();
	    		System.out.println("Attempting to search for " + search);
	    		try {
	    			if (!search.isEmpty()){
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
		    				System.out.println("Could not find a game matching this query");
		    				if (resultsPanel.getComponents() != null){
		    					resultsPanel.removeAll();
		    				}
		    				JLabel temp = new JLabel("No results found.");
		    				resultsPanel.add(temp);
		    				mainPanel.add(resultsPanel);
		    				frame.revalidate();
		    				frame.repaint();
		    				System.out.println("?");		    			}
		    			else {
		    				addGamesToPanel(rs);
		    			}
	    			}
	    		} catch (SQLException e1){
	    			e1.printStackTrace();
	    		}
	    	}
	    });
	    //Probably we'll use this query on the developer page?
	    searchGameViaDeveloper.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent e){
	    		String search = searchField.getText();
	    		System.out.println("Attempting to search for " + search);
	    		try {
	    			Statement s = con.createStatement();
	    			String query = "SELECT gameName, gameGenre "
	    					+ "FROM game g INNER JOIN developed d ON g.gameId = d.gameId "
	    					+ "INNER JOIN developer d2 ON d.dId = d2.dId"
	    					+ " WHERE UPPER(d2.dName) LIKE UPPER('%" + search + "%')";
	    			ResultSet rs = s.executeQuery(query);
	    			if (!rs.isBeforeFirst()){
	    				System.out.println("Could not find a game matching this query");
	    				if (resultsPanel.getComponents() != null){
	    					resultsPanel.removeAll();
	    				}
	    				JLabel temp = new JLabel("No results found.");
	    				resultsPanel.add(temp);
	    				mainPanel.add(resultsPanel);
	    				frame.revalidate();
	    				frame.repaint();
	    				System.out.println("?");
	    			}
	    			else {
	    				addGamesToPanel(rs);
	    			}
	    		} catch (SQLException e1){
	    			e1.printStackTrace();
	    		}
	    	}	       	
	    });
	    
	    //Query to find game by platform
	    /*String query = "SELECT gameName, gameGenre "
	    		+ "FROM game g INNER JOIN available a ON g.gameId = a.gameId "
	    		+ "INNER JOIN platform p ON a.pId = p.pId "
	    		+ "WHERE p.pName LIKE '%" + "query" + "%;";*/
	    
	    //Query to find game by average rating?
	    /*String query = "SELECT gameName, gameGenre, avg_rating"
	    		+ "FROM (SELECT gameName, gameGenre, AVG(rating) AS avg_rating"
	    		+ "FROM game g INNER JOIN review r ON g.gameId = r.gameId"
	    		+ "GROUP BY g.gameId)"
	    		+ "WHERE avg_rating > " + 5;*/
	    
	    //Query to find the top 10 best games
//	    String query = "SELECT gameName, gameGenre, avg_rating"
//	    		+ "FROM (SELECT gameName, gameGenre, AVG(rating) AS avg_rating"
//	    		+ "FROM game g INNER JOIN review r ON g.gameId = r.gameId"
//	    		+ "GROUP BY g.gameId)"
//	    		+ "WHERE avg_rating > " + 5;*/
//
//	    //Query to find the top 10 best games
////	    String query = "SELECT gameName, gameGenre, avg_rating"
////	    		+ "FROM (SELECT gameName, gameGenre, AVG(rating) AS avg_rating"
////	    		+ "FROM game g INNER JOIN review r ON g.gameId = r.gameId"
////	    		+ "GROUP BY g.gameId) "
////	    		+ "ORDER BY avg_rating LIMIT 10";
//
//	    //Query to find the top 10 best rated users -- this needs some testing
////	    String query = "SELECT username, avg_rating"
////	    		+ "FROM (SELECT username, AVG(rating) AS avg_rating"
////	    		+ "FROM users u INNER JOIN rate r ON u.userId = r.rated_userId"
////	    		+ "GROUP BY u.username)"
////	    		+ "ORDER BY avg_rating LIMIT 10";
//
//	    //Query to find games played by the top 10 reated users
////	    String query = "SELECT gameName, gameGenre "
////	    		+ "FROM game g INNER JOIN owns o ON g.gameId = o.gameId"
////	    		+ "WHERE o.userId IN (SELECT userId"
////	    		+ "FROM (SELECT username, userId, AVG(rating) AS avg_rating"
////	    		+ "FROM users u INNER JOIN rate r ON u.userId = r.rated_userId"
////	    		+ "GROUP BY u.username)"
////	    		+ "ORDER BY avg_rating LIMIT 10)";
//
//	    //Select games that are owned by everybody
//	    //This SHOULD be division.
//	    String query = "SELECT gameName, gameGenre "
//	    		+ "FROM game g INNER JOIN owns o ON g.gameId = o.gameId"
//	    		+ "WHERE o.userId IN (SELECT userId"
//	    		+ "FROM (SELECT username, userId, AVG(rating) AS avg_rating"
//	    		+ "FROM users u INNER JOIN rate r ON u.userId = r.rated_userId"
//	    		+ "GROUP BY u.username)"
//	    		+ "ORDER BY avg_rating LIMIT 10)";	    
	    
	    //Select games that are owned by everybody
	    //This SHOULD be division.
	    String query = "SELECT gameName, gameGenre "
	    		+ "FROM game g INNER JOIN owns o ON g.gameId = o.gameId"
	    		+ "WHERE o.userID in (SELECT userID"
	    		+ "FROM users u)"
	    		+ "GROUP BY gameName"
	    		+ "HAVING COUNT(*) = (SELECT COUNT(*) FROM users)";
	}
	
	public void addGamesToPanel(ResultSet rs){
		if (resultsPanel.getComponents() != null){
			resultsPanel.removeAll();
		}
		
		resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.PAGE_AXIS));
		try{
			int columnCount = rs.getMetaData().getColumnCount();
			
			while(rs.next()){				
				String s = "";
				
				for (int i = 1; i <= columnCount; i++){
					if (i != columnCount){
						s += rs.getString(i) + " - ";
					}
					else {
						s+= rs.getString(i);
					}
				}
				s+="\n";
				JLabel temp = new JLabel(s);
				resultsPanel.add(temp);
				
			}
		}catch (SQLException e1){
			e1.printStackTrace();
		}
		mainPanel.add(resultsPanel);
		frame.revalidate();
		frame.repaint();
	}
			
	
	public void run() {
	    try{
	    	DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
	    	//Change the below line to match your oracle username/password
	    	con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1522:ug", "ora_o2n8", "a39088125");
	    	//con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:ug", "ora_r9j8", "a15093123");

	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	    
		
		frame = new GuiProgram();
		mainPanel = new JPanel();
		resultsPanel = new JPanel();

		final JLabel loginName = new JLabel("Login NAME: ");
		loginNameBox = new JTextField(10);
		final JLabel loginPassword = new JLabel("Login PASSWORD: ");
		loginPasswordBox = new JPasswordField(10);
		loginButton = new JButton("Login");
		signUpButton = new JButton("Sign-up");
		searchGameName = new JButton("Search");
		searchDeveloperName = new JButton("Search");
		searchPlatformName = new JButton("Search");
		searchGameViaDeveloper = new JButton("Search");
		searchOptions = new JComboBox<String>(options);
		
		searchField = new JTextField(30);

		
		mainPanel.add(loginName);
		mainPanel.add(loginNameBox);
		mainPanel.add(loginPassword);
		mainPanel.add(loginPasswordBox);
		mainPanel.add(loginButton);
		mainPanel.add(signUpButton);

		frame.setContentPane(mainPanel);
		frame.setLayout(new FlowLayout());
		frame.setResizable(true);
	    frame.setVisible(true);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
		init();
	    
		
	}
	
	public static void main (String [ ] args) {
	    GuiProgram g = new GuiProgram();

	    g.run();
	}
}
