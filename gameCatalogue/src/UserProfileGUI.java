import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;


public class UserProfileGUI extends JFrame{
	private Connection con;
	private JFrame frame;
	private JPanel panel;
	
	private String username;
	private Integer userId;
	private Integer loggedInUserId;
	private String joinDate;
	private boolean isModerator;
	private Integer userRating;
	
	public UserProfileGUI(Connection sqlConnection, Integer user){
		con = sqlConnection;
		userId = user;
		
		//get the user details
		try{
			Statement s = con.createStatement();
			String query = "SELECT * FROM users WHERE userId = '" + userId + "'";
			ResultSet rs = s.executeQuery(query);
			
			while (rs.next()){
				username = rs.getString(2);
				joinDate = rs.getString(4).toString();
				isModerator = rs.getString(5).equals('1');
			}
			
			//s = con.createStatement();
			query = "SELECT AVG(rating) FROM rate WHERE rated_userId = '" + userId + "'";
			rs = s.executeQuery(query);
			if (rs.next()) {
				userRating = rs.getInt(1);
			}
			System.out.println("userId is: " + userId);
			System.out.println("joindate is: " + joinDate);
			System.out.println("is mod is: " + isModerator);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		
		
	}

	
	public JPanel getPanel(Integer loggedIn){
		loggedInUserId = loggedIn;
		ArrayList<StoreData> owns = new ArrayList<StoreData>();
		ArrayList<StoreData> wish = new ArrayList<StoreData>();
		
		panel = new JPanel();
		JTextArea userInfo = new JTextArea("Username: " + username +"\nRating: " +userRating.toString()+
				                           "\nJoined Since: "+joinDate.substring(0, joinDate.indexOf('.')));
		JButton wishList = new JButton("User Wish List");
		JButton owned = new JButton("User Owned Games");
		panel.add(userInfo);
		
		panel.add(wishList);
		panel.add(owned);
		

		SpringLayout layout = new SpringLayout();
		layout.putConstraint(SpringLayout.NORTH, wishList, 2, SpringLayout.SOUTH, userInfo);
		layout.putConstraint(SpringLayout.WEST, owned, 50, SpringLayout.EAST, wishList);
		layout.putConstraint(SpringLayout.NORTH, owned, 2, SpringLayout.SOUTH, userInfo);
		
		panel.setLayout(layout);
		
		try{
			Statement s = con.createStatement();
			String query = "SELECT g.gameName, g.gameGenre, o.rating, o.since FROM owns o, game g WHERE o.userId = '" + userId + "' AND o.gameId = g.gameId";
			ResultSet rs = s.executeQuery(query);
			while (rs.next()) {    
				 String gameName = rs.getString(1); 
				 String gameGenre = rs.getString(2);
				 Integer rating = rs.getInt(3);
				 String since = rs.getString(4);
				 owns.add(new StoreData(gameName, gameGenre, rating, since, new JButton("Game: "+gameName+" : "+gameGenre),
						                new JTextArea("Rated: "+rating.toString()+" On "+since)));
				 
				 System.out.println(gameName +" "+ gameGenre +" "+ rating.toString() +" "+ since);
			} 
		}
		catch (SQLException e1){
			e1.printStackTrace();
		}
		
		owned.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (StoreData sd : wish) {
					panel.remove(sd.getButton());
					panel.remove(sd.getText());
				}
				panel.add(owns.get(0).getButton());
				panel.add(owns.get(0).getText());
				layout.putConstraint(SpringLayout.NORTH, owns.get(0).getText(), 1, SpringLayout.SOUTH, owns.get(0).getButton());
				
				layout.putConstraint(SpringLayout.NORTH, owns.get(0).getButton(), 4, SpringLayout.SOUTH, wishList);
				
				for (int i=1; i< owns.size(); i++) {
					panel.add(owns.get(i).getButton());
					panel.add(owns.get(i).getText());
					layout.putConstraint(SpringLayout.NORTH, owns.get(i).getText(), 1, SpringLayout.SOUTH, owns.get(i).getButton());
					
					layout.putConstraint(SpringLayout.NORTH, owns.get(i).getButton(), 4, SpringLayout.SOUTH, owns.get(i-1).getText());
				}
			}
		});
		
		if (!userId.equals(loggedInUserId)) {

			JTextField rateText = new JTextField(2);
			JButton rate = new JButton("Rate");
			panel.add(rateText);
			panel.add(rate);
			layout.putConstraint(SpringLayout.WEST, rateText, 100, SpringLayout.EAST, userInfo);
			layout.putConstraint(SpringLayout.WEST, rate, 2, SpringLayout.EAST, rateText);
					
			
			
			rate.addActionListener(new ActionListener(){

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
							
							//Check if we've already rated this user
							String query = "SELECT rating from rate WHERE rater_userId = " + loggedInUserId + "AND rated_userId = " + userId;
							ResultSet rs = s.executeQuery(query);
							
							if (rs.next()){ //We've already rated them, update the entry
								Integer oldRating = rs.getInt(1);
								rs.updateInt(1, rating);
								rs.updateRow();
								JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(panel), 
										"You updated " + username + "'s rating from " + oldRating + " to "  + rating);
	
							}
							else {
								query = "INSERT into rate VALUES (" +loggedInUserId + "," + userId + "," + userRating + ")";
								s.executeUpdate(query);
								JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(panel), "You gave " + username+ " a " + userRating + " rating!");
							}
							
						}
						catch (SQLException e1){
							e1.printStackTrace();
						}
					}
				}
											
			});
		}
		return panel;
	}
	

}
