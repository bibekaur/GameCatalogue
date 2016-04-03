import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class GameInfoGUI extends JFrame{
	private Integer loggedInUserId;
	private Connection con;
	private JFrame frame;
	private JLabel ratingLabel;
	private JTextArea newestReview;
	
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
	ArrayList<JTextArea> reviews = new ArrayList<JTextArea>();
	
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
		getAverageRating(); 
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
		try { //Let's get all the reviews and owns ratings
			Statement s = con.createStatement();
			String query = "SELECT AVG(rating) from owns WHERE gameId=" + gameId + " AND userId=" + loggedInUserId;
			ResultSet rs = s.executeQuery(query);
			
			if (rs.next()){
				averageRating = rs.getInt(1);
				return;
			}
			averageRating = 0;
			
		} catch (SQLException e1){
			e1.printStackTrace();
		}
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

	
	private void addRateButtonListener(JButton rateButton, final JTextField rateText){
		rateButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				//Get the user rating input
				Integer rating;
				try {
					rating = Integer.parseInt(rateText.getText());
										
				} catch(NumberFormatException e){
					JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(panel), "Please enter a number between 1 and 10");
					return;
				}

				try{
					Statement s = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				                ResultSet.CONCUR_UPDATABLE);
					String query = "SELECT userId, rating from owns WHERE userId = " + loggedInUserId + "AND gameId = " + gameId;
					ResultSet rs = s.executeQuery(query);
					try {
						if (rs.next()){
							rs.updateInt("rating", rating);
							rs.updateRow();
							ratingLabel.setText("You rated this game " + rating);
						}
						else {
							JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(panel), "You must own this game to rate it!");
						}
					} catch (SQLException e1){
						JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(panel), "You didn't enter a number between 1 and 10. "
								+ "Please enter a number between 1 and 10.");

					}
						
						
				} catch (SQLException e1){
					e1.printStackTrace();
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
					
					checkIfOwned();
					
					if (isOwned){
						//already owns this game, don't insert
						JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(panel), "You already own this game");
						return;
					}
					else {
						Statement s = con.createStatement();
						String query = "INSERT into owns VALUES ("+ loggedInUserId + "," + gameId + ", CURRENT_TIMESTAMP, NULL)";
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
				isOwned = true;
			}
			
		});
	}
	
	private void addWishlistButtonListener(JButton wishlistButton){
		wishlistButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				try{ //Check if the user owns the game, don't add to wishlist if so (just return)
					
					checkIfOwned();
					
					if (isOwned){
						//already owns this game, don't insert
						JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(panel), "You can't add a game you own to the wishlist");
						return;
					}
					
					
					//get the rank of the game
					Integer rank;
					try{
						rank = Integer.parseInt((String) JOptionPane.showInputDialog((JFrame) SwingUtilities.getRoot(panel), "Where does this game rank in your wishlist (1-10)?"));
							
					} catch (NumberFormatException e){
						JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(panel), "Please enter an integer between 1 and 10");
						return;
					}
						
					
					//Check if the game is already in the wishlist
					Statement s = con.createStatement();
					String query = "SELECT rank from wishes WHERE userId=" + loggedInUserId + " AND gameId=" + gameId;
					ResultSet rs = s.executeQuery(query);
					
					if (rs.next()){ //Update the rank
						Integer oldRank = rs.getInt("rank");
						try {
							rs.updateInt("rank", rank);
							rs.updateRow();
							JOptionPane.showMessageDialog(frame, gameName + "'s ranking has been changed from " + oldRank + " to " + rank);
						} catch (SQLException e1){
							JOptionPane.showMessageDialog(frame, "You didn't enter an integer between 1 and 10. Please enter an integer between 1 and 10");
							
						}
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
					try {
						s.executeQuery(query);
					} catch (SQLException e1){
						JOptionPane.showMessageDialog(frame, "You didn't enter an integer between 1 and 10. Please enter an integer between 1 and 10");
						return;
					}
					JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(panel), gameName + " has rank " + rank + " in your wishlist");
				}
					catch (SQLException e1){
					e1.printStackTrace();
				}
				
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
	
	private void displayLowestFiveReviews(){
		try {
			
			Statement s = con.createStatement();
			String query = "SELECT * "
					+ "from review r INNER JOIN users u ON r.userId = u.userId "
					+ "WHERE r.gameId =" + gameId + " AND ROWNUM <= 5"
										+ "ORDER BY r.rating ASC ";
			ResultSet rs = s.executeQuery(query);
								
			for (JTextArea r : this.reviews){
				panel.remove(r);
			}
			
			this.reviews = new ArrayList<JTextArea>();
			
			while (rs.next()){
				String username = rs.getString("username");
				String description = rs.getString("description");
				Integer rating = rs.getInt("rating");
				
				this.reviews.add(new JTextArea(description + "\nUser: " + username + " Rating: " + rating));
				
			}
			
			for (JTextArea r : this.reviews){
				panel.add(r);
			}
			
			
		} catch (SQLException e1){
			e1.printStackTrace();
		}
	}
	
	private void displayTopFiveReviews(){
		try {
			Statement s = con.createStatement();
			String query = "SELECT * "
					+ "from review r INNER JOIN users u ON r.userId = u.userId "
					+ "WHERE r.gameId =" + gameId + " AND ROWNUM <= 5"
					+ "ORDER BY r.rating DESC ";
			ResultSet rs = s.executeQuery(query);
					
			for (JTextArea r : this.reviews){
				panel.remove(r);
			}
			
			this.reviews = new ArrayList<JTextArea>();
			
			while (rs.next()){
				String username = rs.getString("username");
				String description = rs.getString("description");
				Integer rating = rs.getInt("rating");
				
				this.reviews.add(new JTextArea(description + "\nUser: " + username + " Rating: " + rating));
				
			}
			
			for (JTextArea r : this.reviews){
				panel.add(r);
			}
			
		} catch (SQLException e1){
			e1.printStackTrace();
		}
	}
	
	private void addReviewButtonListener(JButton reviewButton){
		reviewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Integer rating;
				String description;
				
				//Gotta check if they own it
				if (!isOwned){
					JOptionPane.showMessageDialog(frame, "You must own this game to review it");
					return;

				}
				
				try {
					rating = Integer.parseInt( (String) JOptionPane.showInputDialog(frame, 
							"Please enter a rating between 1 and 10"));
				} catch (NumberFormatException e){
					JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(panel), "Please enter an integer between 1 and 10");
					return;
				}
				
				description = (String) JOptionPane.showInputDialog(frame, 
						"Please enter the description of the review (should not exceed 3000 characters)");
				if (description.length() > 3000 ){
					JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(panel), "Your description is too long (" + description.length() + " characters long)");
					return;
				}
				else if (description == null || description.length() == 0){
					JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(panel), "Please enter a description");
					return;
				}
				
				try{ //either update or insert the review
					Statement s = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
			                 ResultSet.CONCUR_UPDATABLE);
					String query = "SELECT description, rating "
							+ "FROM review "
							+ "WHERE userId=" + loggedInUserId + " AND gameId=" + gameId;
					ResultSet rs = s.executeQuery(query);
					
					if (rs.next()){
						rs.updateString("description", description);
						try {
							rs.updateInt("rating", rating);
							rs.updateRow();
							JOptionPane.showMessageDialog(frame, "Your review has been updated");
						} catch (SQLException e1){
							JOptionPane.showMessageDialog(frame, "You didn't enter a rating between 1 and 10. Please choose a rating between 1 and 10.");
						}

						return;
						
					}
					
					query = "INSERT into review VALUES (DEFAULT,'" + description + "'," + rating + "," + loggedInUserId + "," + gameId + ")";
					try {
						s.executeQuery(query);
					} catch (SQLException e1){
						JOptionPane.showMessageDialog(frame, "You didn't enter a rating between 1 and 10. Please choose a rating between 1 and 10.");
						return;
					}
					
					JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(panel), "Your review has been saved");
	
				} catch (SQLException e1){
					e1.printStackTrace();
				}
			}
		});
	}
	
	private void addDeleteReviewButtonListener(JButton deleteButton){
		deleteButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//Check if we have a review then delete it
				try {
					Statement s = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
			                 ResultSet.CONCUR_UPDATABLE);
					String query = "SELECT rId from review WHERE userId="+ loggedInUserId + " AND gameId=" + gameId;
					ResultSet rs = s.executeQuery(query);
					
					if (rs.next()){
						rs.deleteRow();
						JOptionPane.showMessageDialog(frame, "Your review has been deleted");
						return;
					}
					
					JOptionPane.showMessageDialog(frame, "You have not written a review yet");
					
				} catch (SQLException e1){
					e1.printStackTrace();
				}
			}
		});
	}
	
	private void addPlatformButtonListener(JButton platformButton){
		platformButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				PlatformInfoGUI platformGUI= new PlatformInfoGUI (platformId, loggedInUserId, con);
				platformGUI.setPanel(frame);
			}
		});
	}
	
	public void setPanel(final JFrame frame){
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
		
		//Clicking on this button switches to the developer gui
		JButton developerButton = new JButton("Developer: " + devName);
		addDeveloperButtonListener(developerButton);
		
		//"Review this game" button
		JButton reviewGameButton = new JButton("Review this game");
		addReviewButtonListener(reviewGameButton);
		
		//"Delete my review" button
		JButton deleteReviewButton = new JButton("Delete my review");
		addDeleteReviewButtonListener(deleteReviewButton);
		
		//Clicking on this button switches to the platform gui
		JButton platformButton = new JButton("Platform: " + platformName);
		addPlatformButtonListener(platformButton);
		
		//Radio buttons to toggle between top 5 and bottom 5 reviews
		JRadioButton topFiveButton = new JRadioButton("5 Highest Reviews");
		JRadioButton lowestFiveButton = new JRadioButton("5 Lowest Reviews");
		ButtonGroup reviewButtonGroup = new ButtonGroup();
		reviewButtonGroup.add(topFiveButton);
		reviewButtonGroup.add(lowestFiveButton);
		
		topFiveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				displayTopFiveReviews();
				frame.revalidate();
				frame.repaint();
			}
				
				
		});
		
		lowestFiveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				displayLowestFiveReviews();
				frame.revalidate();
				frame.repaint();
			}
				
				
		});
		
		JButton mainPage = new JButton("Main Page");
		panel.add(mainPage);
		//layout.putConstraint(SpringLayout.WEST, mainPage, 200, SpringLayout.EAST, userInfo);
		mainPage.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {				
				//TODO call main page function
			}
		});
		
		panel.add(platformButton);
		panel.add(deleteReviewButton);
		panel.add(reviewGameButton);
		panel.add(topFiveButton);
		panel.add(lowestFiveButton);
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
