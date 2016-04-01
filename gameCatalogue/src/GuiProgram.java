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
		mainPanel = new JPanel();
		mainPanel.add(searchField);
		mainPanel.add(searchOptions);
		mainPanel.add(searchGameName);
		frame.setContentPane(mainPanel);
		frame.revalidate();
		frame.repaint();;
	}
	
	//Splitting it into initializing what the buttons do
	public void init(){
		
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
						String query = "SELECT username, password FROM users WHERE username = '" + loginName + "' AND password = '" + loginPassword + "'";
						//Below line checks if it exists or not
						ResultSet rs = s.executeQuery(query);
						if (!rs.next() ) {    
							 System.out.println("No username/password combo found"); 
						} 
						else {
							drawLoggedInScreen();	
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
							System.out.println("This username is already used");
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
			    					+ " WHERE gameName LIKE '%" + search + "%'";
		    			}
		    			else if (option.contentEquals("Platform")){
			    			query = "SELECT pName, cost, releaseDate "
			    					+ "FROM platform "
			    					+ "WHERE pName LIKE '%" + search + "%'";
		    			}
		    			else {
			    			query = "SELECT dName "
			    					+ "FROM developer "
			    					+ "WHERE dName LIKE '%" + search + "%'";
		    			}
		    			ResultSet rs = s.executeQuery(query);
		    			if (!rs.isBeforeFirst()){
		    				System.out.println("Could not find a game matching this query");
		    			}
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
	    					+ " WHERE d2.dName LIKE '%" + search + "%'";
	    			ResultSet rs = s.executeQuery(query);
	    			if (!rs.isBeforeFirst()){
	    				System.out.println("Could not find a game matching this query");
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
	    	con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1522:ug", "ora_i2m8", "a92859115");
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
