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
	private Integer rating = 0;
	
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
		
		//TODO: Check if the user is a moderator
		
		//TODO: Get the average rating
	}
	
	//Check if the user owns this game, return the rating if so
	//Return -1 otherwise
	private Integer checkIfOwned(){
		try {
			Statement s = con.createStatement();
			String query = "SELECT rating from owns WHERE userId=" + loggedInUserId + " AND gameId=" + gameId;
			ResultSet rs = s.executeQuery(query);
			
			if (rs.next()){
				return rs.getInt("rating");
			}
			
			
		} catch (SQLException e1){
			e1.printStackTrace();
		}
		
		return -1;
	}

	//Check if the game is in the user's wishlist, return the rank if so
	//Return -1 if not in wishlist
	private Integer checkInWishlist(){

		try{
			Statement s = con.createStatement();
			String query = "SELECT rank from wishes WHERE userId=" + loggedInUserId + " AND gameId=" + gameId;
			ResultSet rs = s.executeQuery(query);
			
			if (rs.next()){
				return rs.getInt("rating");
			}
			
		}catch (SQLException e1){
			e1.printStackTrace();
		}
		
		return -1;
		
	}
	
	public void setPanel(JFrame frame){
		panel = new JPanel();
		JTextArea gameInfo = new JTextArea("Name: " + gameName + "\n"
				+ "Average rating: " + rating + "\n"
				+ "Game genre: " + gameGenre + "\n");
		
		Integer rating = checkIfOwned();
		if (rating == -1){
			Integer ranking = checkInWishlist();
			ratingLabel = new JLabel("You must own this game to rate it");
		}
		else {
			ratingLabel = new JLabel("You rated this game "+ rating);
		}
		
		
		
		JTextField rateText = new JTextField(2);
		JButton rate = new JButton("Rate"); 
		
		
		panel.add(gameInfo);
		frame.setContentPane(panel);
		frame.revalidate();
		frame.repaint();
	}

}
