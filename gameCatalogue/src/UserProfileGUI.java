import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;


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
	
	public UserProfileGUI(Connection sqlConnection, String user){
		con = sqlConnection;
		username = user;
		
		//get the user id
		try{
			Statement s = con.createStatement();
			String query = "SELECT * FROM users WHERE username = '" + username + "'";
			ResultSet rs = s.executeQuery(query);
			
			while (rs.next()){
				userId = rs.getInt(1);
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
