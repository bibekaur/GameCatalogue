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
	private Integer averageRating = 0;
	
	private boolean isOwned;
	private boolean isWishlist;
	private Integer ownedRating;
	private Integer wishlistRating;
	
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
		
		//TODO: Check if the user is a moderator
		
		//TODO: Get the average rating
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
				wishlistRating = rs.getInt("rating");
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
	
//	private void addOwnButtonListener(JButton ownButton){
//		ownButton.addActionListener(new);
//	}
	
	public void setPanel(JFrame frame){
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
		//addOwnButtonListener(ownButton);
		
		
		
		//"Add this game to my wishlist" button, ONLY works if we don't own the game
		//Add a label that shows the rank of the game
		
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
