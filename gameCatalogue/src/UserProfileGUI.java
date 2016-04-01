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

	
	public JPanel getPanel(Integer loggedInUserId){
		panel = new JPanel();
		JTextArea userInfo = new JTextArea("Username: " + username +"\nRating: " +userRating.toString()+
				                           "\nJoined Since: "+joinDate.substring(0, joinDate.indexOf('.')));
		JButton wishList = new JButton("User Wish List");
		JButton owned = new JButton("User Owned Games");		

		SpringLayout layout = new SpringLayout();
		layout.putConstraint(SpringLayout.SOUTH, userInfo, 2, SpringLayout.NORTH, wishList);
		layout.putConstraint(SpringLayout.WEST, wishList, 50, SpringLayout.EAST, owned);
		
		panel.setLayout(layout);
		
		panel.add(userInfo);
		panel.add(wishList);
		panel.add(owned);
		
		if (!userId.equals(loggedInUserId)) {

			JTextField rateText = new JTextField(2);
			JButton rate = new JButton("Rate");
			panel.add(rateText);
			panel.add(rate);
			
			//TODO: Check if this user has already rated this person
			
			
			rate.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent arg0) {
					try {
						Integer rating = Integer.parseInt(rateText.getText());
						if (rating > 10 || rating < 1){
							JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(panel), "Please enter a number between 1 and 10");
						}
						else {
							userRating = rating;
							try{
								Statement s = con.createStatement();
								String query = "INSERT into rate VALUES (" +loggedInUserId + "," + userId + "," + userRating + ")";
								s.executeQuery(query);
							}
							catch (SQLException e1){
								e1.printStackTrace();
							}
						}
											
					} catch(NumberFormatException e){
						JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(panel), "Please enter a number between 1 and 10");
					}
								
				}
				
			});
		}
		return panel;
	}
	

}
