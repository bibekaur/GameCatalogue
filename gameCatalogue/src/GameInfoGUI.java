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
	
	//Check if the user owns this game
	private boolean checkIfOwned(){
		//TODO
		return false;
	}

	//Check if the game is in the user's wishlist, return the rank if so
	//Return -1 if not in wishlist
	private Integer checkInWishlist(){
		//TODO
		return -1;
	}
	
	public JPanel getPanel(){
		panel = new JPanel();
		JTextArea gameInfo = new JTextArea("Name: " + gameName + "\n"
				+ "Average rating: " + rating + "\n"
				+ "Game genre: " + gameGenre + "\n");
		
		panel.add(gameInfo);
		
		
		
		return panel;
	}

}
