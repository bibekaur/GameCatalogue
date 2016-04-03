import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GameInfoGUI extends JFrame{
	private Integer loggedInUserId;
	private Connection con;
	private JFrame frame;
	private JLabel ratingLabel;
	
	private Integer gameId;
	private String gameName;
	private String gameGenre;
	private Integer averageRating;
	
	private boolean isOwned;
	private boolean isWishlist;
	private Integer ownedRating;
	private Integer wishlistRating;
	
	private String platformName;
	private Integer platformId;
	
	private String devName;
	private Integer devId;
	
	private JPanel panel;
	
	public GameInfoGUI (Integer gId, Integer userId, Connection sqlConnection){
		loggedInUserId = userId;
		con = sqlConnection;
		gameId = gId;
		
		//Get the game details
		try {
			Statement s = con.createStatement();
			String query = "SELECT * from game WHERE gameId = " + gameId;
			ResultSet rs = s.executeQuery(query);
			
			if (rs.next()){
				gameName = rs.getString("gameName");
				gameGenre = rs.getString("gameGenre");
			}
			
		} catch (SQLException e1){
			e1.printStackTrace();
		}
		
		checkIfOwned();
		checkInWishlist();
		getAverageRating(); //TODO
		getDeveloper();
		getPlatform();
		
		//TODO: Check if the user is a moderator because they can delete the game
		
	}
	
	private void getDeveloper(){
		try{
			Statement s = con.createStatement();
			String query = "SELECT r.dName, r.dId " +
						"FROM developed d INNER JOIN developer r on d.dId = r.dId " + 
						"WHERE d.gameId=" + gameId;
			ResultSet rs = s.executeQuery(query);
			
			if (rs.next()){
				devName = rs.getString("dName");
				devId = rs.getInt("dId");
			}
			
		} catch (SQLException e1){
			e1.printStackTrace();
		}
	}
	
	private void getPlatform(){
		try{
			Statement s = con.createStatement();
			String query = "SELECT p.pName, p.pId " + 
							"FROM available a INNER JOIN platform p ON a.pId = p.pId " + 
							"WHERE a.gameId =" + gameId;
			ResultSet rs = s.executeQuery(query);
			
			if (rs.next()){
				platformName = rs.getString("pName");
				platformId = rs.getInt("pId");
				
			}
			
		} catch (SQLException e1){
			e1.printStackTrace();
		}
	}
	
	private void getAverageRating(){
		//TODO:
		
		//averageRating = something
	}
	
	private void checkIfOwned(){
		try {
			Statement s = con.createStatement();
			String query = "SELECT * from owns WHERE userId=" + loggedInUserId + " AND gameId=" + gameId;
			ResultSet rs = s.executeQuery(query);
			
			if (rs.next()){
				isOwned = true;
				ownedRating = rs.getInt("rating");
				return;
			}
			
			
		} catch (SQLException e1){
			e1.printStackTrace();
		}
		
		isOwned = false;
		
	}


	private void checkInWishlist(){

		try{
			Statement s = con.createStatement();
			String query = "SELECT rank from wishes WHERE userId=" + loggedInUserId + " AND gameId=" + gameId;
			ResultSet rs = s.executeQuery(query);
			
			if (rs.next()){
				isWishlist = true;
				wishlistRating = rs.getInt("rank");
				return;
			}
			
		}catch (SQLException e1){
			e1.printStackTrace();
		}
		
		isWishlist = false;
		
	}

	
	private void addRateButtonListener(JButton rateButton, JTextField rateText){
		rateButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				//Get the user rating input
				Integer rating = -1;
				try {
					rating = Integer.parseInt(rateText.getText());
					if (rating > 10 || rating < 1){
						rating = -1;
						JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(panel), "Please enter a number between 1 and 10");
					}
										
				} catch(NumberFormatException e){
					rating = -1;
					JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(panel), "Please enter a number between 1 and 10");
				}

				if (rating != -1){
					try{
						Statement s = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				                 ResultSet.CONCUR_UPDATABLE);
						//TODO: apparnetly need to use check statement, also display what the rating is using a label
						//Check if we've already rated this user
						String query = "SELECT userId, rating from owns WHERE userId = " + loggedInUserId + "AND gameId = " + gameId;
						ResultSet rs = s.executeQuery(query);
						if (rs.next()){
							rs.updateInt("rating", rating);
							rs.updateRow();
							ratingLabel.setText("You rated this game " + rating);
						}
						else {
							JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(panel), "You must own this game to rate it!");
						}
						
						
					}
					catch (SQLException e1){
						e1.printStackTrace();
					}
				}
			}
										
		});
	}
	
	private void addOwnButtonListener(JButton ownButton){
		ownButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//Only insert into own if they don't own it already
				try{
					Statement s = con.createStatement();
					String query = "SELECT * from owns WHERE userId = " + loggedInUserId + "AND gameId = " + gameId;
					ResultSet rs = s.executeQuery(query);
					
					if (rs.next()){
						//already owns this game, don't insert
						JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(panel), "You already own this game");
						return;
					}
					else {
						query = "INSERT into owns VALUES ("+ loggedInUserId + "," + gameId + ", CURRENT_TIMESTAMP, NULL)";
						s.executeQuery(query);
					}
					
				}catch (SQLException e1){
					e1.printStackTrace();
				}
				
				//Remove from wishlist 
				try {
					Statement s = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
			                 ResultSet.CONCUR_UPDATABLE);
					String query = "SELECT rank from wishes WHERE userId=" + loggedInUserId + " AND gameId=" + gameId;
					ResultSet rs = s.executeQuery(query);
					
					if (rs.next()){
						rs.deleteRow();
					}
					
				} catch (SQLException e1){
					e1.printStackTrace();
				}
							
				//Update label
				ratingLabel.setText("Please rate this game");				
			}
			
		});
	}
	
	private void addWishlistButtonListener(JButton wishlistButton){
		wishlistButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				try{ //Check if the user owns the game, don't add to wishlist if so (just return)
					Statement s = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
			                 ResultSet.CONCUR_UPDATABLE);
					String query = "SELECT * from owns WHERE userId = " + loggedInUserId + "AND gameId = " + gameId;
					ResultSet rs = s.executeQuery(query);
					
					if (rs.next()){
						//already owns this game, don't insert
						JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(panel), "You can't add a game you own to the wishlist");
						return;
					}
					
					
					//get the rank of the game
					Integer rank;
					try{
						rank = Integer.parseInt((String) JOptionPane.showInputDialog((JFrame) SwingUtilities.getRoot(panel), "Where does this game rank in your wishlist (1-10)?"));
							
						if (rank > 10 || rank < 1){
							JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(panel), "Please enter an integer between 1 and 10");
							return;
						}
							
					} catch (NumberFormatException e){
						JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(panel), "Please enter an integer between 1 and 10");
						return;
					}
						
					
					//Check if the game is already in the wishlist
					query = "SELECT rank from wishes WHERE userId=" + loggedInUserId + " AND gameId=" + gameId;
					rs = s.executeQuery(query);
					
					if (rs.next()){ //Update the rank
						Integer oldRank = rs.getInt("rank");
						rs.updateInt("rank", rank);
						JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(panel), gameName + "'s ranking has been changed from " + oldRank + " to " + rank);
						return;
					}
					
					//Check if a game with that rank already exists
					//if the game exists, display which game it is in a pop up and return
					query = "SELECT g.gameName"
								+ " FROM wishes w INNER JOIN game g ON g.gameId = w.gameId "
								+ "WHERE userId=" + loggedInUserId+ " AND rank=" + rank;
					rs = s.executeQuery(query);
						
					if (rs.next()){
						JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(panel), "You already ranked " + rs.getString("g.gameName") + " with " + rank);
						return;
					}
						
					//if rank is free then insert
					query = "INSERT into wishes VALUES ("+ loggedInUserId + "," + gameId + "," + rank + ")";
					s.executeQuery(query);
					JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(panel), gameName + " has rank " + rank + " in your wishlist");
				}
					catch (SQLException e1){
					e1.printStackTrace();
				}
				//
			}
			
		});
	}
	
	public void addRemoveWishlistButtonListener(JButton removeButton){
		removeButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//Check if the game is in the user's wishlist
				try {
					Statement s = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
			                 ResultSet.CONCUR_UPDATABLE);
					String query = "SELECT rank from wishes WHERE userId=" + loggedInUserId + " AND gameId=" + gameId;
					ResultSet rs = s.executeQuery(query);
					
					if (rs.next()){
						rs.deleteRow();
						JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(panel), gameName + " has been removed from your wishlist");
					}
					else { 
						JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(panel), gameName + " is not in your wishlist");
					}
					
				} catch (SQLException e1){
					e1.printStackTrace();
				}
				
			}
		});
		
	}
	
	private void addDeveloperButtonListener(JButton developerButton){
		developerButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				//Go to the developer's page
				System.out.println("dev id is: " + devId);
				DeveloperInfoGUI devGUI = new DeveloperInfoGUI(con, devId);
				devGUI.setPanel(loggedInUserId, frame);
				
			}
			
		});
	}
	
	public void setPanel(JFrame frame){
		this.frame = frame;
		panel = new JPanel();
		JTextArea gameInfo = new JTextArea("Name: " + gameName + "\n"
				+ "Average rating: " + averageRating + "\n"
				+ "Game genre: " + gameGenre + "\n");

		
		if (!isOwned){
			ratingLabel = new JLabel("You must own this game to rate it");
		}
		else {
			ratingLabel = new JLabel("You rated this game " + ownedRating);
			
		}	
		
		JTextField rateText = new JTextField(2);
		JButton rate = new JButton("Rate"); 
		addRateButtonListener(rate, rateText);
		
		//"I own this game" button that inserts, then updates the label
		JButton ownButton = new JButton("I own this game");
		addOwnButtonListener(ownButton);
		
		
		//"Add this game to my wishlist" button (only add if user doesn't own it)
		//Add a label that shows the rank of the game
		JButton wishlistButton = new JButton("Add to my wishlist");
		addWishlistButtonListener(wishlistButton);
		
		
		//"Remove from wishlist" button (only remove if user has it in their wishlist)
		JButton removeWishlistButton = new JButton("Remove from my wishlist");
		addRemoveWishlistButtonListener(removeWishlistButton);
		
		//TODO: developer button
		JButton developerButton = new JButton("Developer: " + devName);
		addDeveloperButtonListener(developerButton);
		
		
		//TODO: platform button
		
		//TODO: get newest review
		
		panel.add(developerButton);
		panel.add(removeWishlistButton);
		panel.add(wishlistButton);
		panel.add(ownButton);
		panel.add(rate);
		panel.add(rateText);
		panel.add(gameInfo);
		panel.add(ratingLabel);
		frame.setContentPane(panel);
		frame.revalidate();
		frame.repaint();
	}

}
