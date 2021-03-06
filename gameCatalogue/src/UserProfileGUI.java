import javax.swing.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


@SuppressWarnings("serial")
public class UserProfileGUI extends JFrame{
	private Connection con;
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
				isModerator = rs.getString(5).equals("1");
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

	
	public void setPanel(Integer loggedIn, final JFrame frame){
		loggedInUserId = loggedIn;
		final ArrayList<StoreData> owns = new ArrayList<StoreData>();
		final ArrayList<StoreData> wish = new ArrayList<StoreData>();
		
		panel = new JPanel();
		JTextArea userInfo = new JTextArea("Username: " + username +"\nRating: " +userRating.toString()+
				                           "\nJoined Since: "+joinDate.substring(0, joinDate.indexOf('.')));
		final JButton wishList = new JButton("User Wish List");
		JButton owned = new JButton("User Owned Games");
		panel.add(userInfo);
		
		panel.add(wishList);
		panel.add(owned);
		

		final SpringLayout layout = new SpringLayout();
		layout.putConstraint(SpringLayout.NORTH, wishList, 2, SpringLayout.SOUTH, userInfo);
		layout.putConstraint(SpringLayout.WEST, owned, 50, SpringLayout.EAST, wishList);
		layout.putConstraint(SpringLayout.NORTH, owned, 2, SpringLayout.SOUTH, userInfo);
		
		panel.setLayout(layout);
		
		try{
			Statement s = con.createStatement();
			String query = "SELECT g.gameName, g.gameGenre, g.gameId, o.rating, o.since FROM owns o, game g WHERE o.userId = '" + userId + "' AND o.gameId = g.gameId";
			ResultSet rs = s.executeQuery(query);
			while (rs.next()) {    
				 String gameName = rs.getString(1); 
				 String gameGenre = rs.getString(2);
				 Integer gameId = rs.getInt(3);
				 Integer rating = rs.getInt(4);
				 String since = rs.getString(5);
				 owns.add(new StoreData(gameId, gameName, gameGenre, rating, since, new JButton("Game: "+gameName+" : "+gameGenre),
						                new JTextArea("Rated: "+rating.toString()+" On "+since)));
				 
				 //System.out.println(gameName +" "+ gameGenre +" "+ rating.toString() +" "+ since);
				 //panel.add(owns.get(0).getButton());
			} 
		}
		catch (SQLException e1){
			e1.printStackTrace();
		}
		
		wishList.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {				
				for (StoreData sd : owns) {
					panel.remove(sd.getButton());
					panel.remove(sd.getText());
				}
                if (!wish.isEmpty()) {
                    panel.add(wish.get(0).getButton());
                    panel.add(wish.get(0).getText());
                    layout.putConstraint(SpringLayout.NORTH, wish.get(0).getText(), 1, SpringLayout.SOUTH, wish.get(0).getButton());
                    
                    layout.putConstraint(SpringLayout.NORTH, wish.get(0).getButton(), 4, SpringLayout.SOUTH, wishList);
                    
                    for (int i=1; i< wish.size(); i++) {
                        panel.add(wish.get(i).getButton());
                        panel.add(wish.get(i).getText());
                        layout.putConstraint(SpringLayout.NORTH, wish.get(i).getText(), 1, SpringLayout.SOUTH, wish.get(i).getButton());
                        
                        layout.putConstraint(SpringLayout.NORTH, wish.get(i).getButton(), 4, SpringLayout.SOUTH, wish.get(i-1).getText());
                    }
                }
                frame.revalidate();
                frame.repaint();
			}
		});
        
        owned.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {				
				for (StoreData sd : wish) {
					panel.remove(sd.getButton());
					panel.remove(sd.getText());
				}
                if (!owns.isEmpty()) {
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
                frame.revalidate();
                frame.repaint();
			}
		});
        
        for (final StoreData sd : wish) {
            sd.getButton().addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {				
				GameInfoGUI game = new GameInfoGUI(sd.getGameId(), loggedInUserId, con);
                game.setPanel(frame);
			}
		});
        }
        
        for (final StoreData sd : owns) {
            sd.getButton().addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {				
				GameInfoGUI game = new GameInfoGUI(sd.getGameId(), loggedInUserId, con);
                game.setPanel(frame);
			}
		});
        }
		
		if (!userId.equals(loggedInUserId)) {

			final JTextField rateText = new JTextField(2);
			JButton rate = new JButton("Rate");
			panel.add(rateText);
			panel.add(rate);
			layout.putConstraint(SpringLayout.WEST, rateText, 100, SpringLayout.EAST, userInfo);
			layout.putConstraint(SpringLayout.WEST, rate, 2, SpringLayout.EAST, rateText);
					
			
			
			rate.addActionListener(new ActionListener(){

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
						String query = "SELECT rating from rate WHERE rater_userId = " + loggedInUserId + "AND rated_userId = " + userId;
						ResultSet rs = s.executeQuery(query);
							
						if (rs.next()){ //We've already rated them, update the entry
							Integer oldRating = rs.getInt(1);
							try{
								rs.updateInt(1, rating);
								rs.updateRow();
								JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(panel), 
											"You updated " + username + "'s rating from " + oldRating + " to "  + rating);
							} catch (SQLException e1){
								JOptionPane.showMessageDialog(frame, "You didn't enter a rating between 1 and 10. Please enter an integer between 1 and 10");
								return;
							}
	
						}
						else {
							try {
								query = "INSERT into rate VALUES (" +loggedInUserId + "," + userId + "," + rating + ")";
								s.executeUpdate(query);
								JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(panel), "You gave " + username+ " a " + rating + " rating!");
							} catch (SQLException e1){
								JOptionPane.showMessageDialog(frame, "You didn't enter a rating between 1 and 10. Please enter an integer between 1 and 10");
								return;
							}
						}
						
							
					}
					catch (SQLException e1){
						e1.printStackTrace();
					}
				}
											
			});
		}
		
		boolean loggerIsMod = false;
		try{
			Statement s = con.createStatement();
			String query = "SELECT * FROM users WHERE userId = '" + loggedInUserId + "'";
			ResultSet rs = s.executeQuery(query);
			
			while (rs.next()){
				loggerIsMod = rs.getString(5).equals("1");
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		JButton mainPage = new JButton("Main Page");
		final JTextArea isMod = new JTextArea("is a Moderator");
		final JButton deleteUser = new JButton("Delete User");
		final JButton upgrade = new JButton("Upgrade to Moderator");
		
		panel.add(mainPage);
		layout.putConstraint(SpringLayout.WEST, mainPage, 200, SpringLayout.EAST, userInfo);
		
		layout.putConstraint(SpringLayout.WEST, isMod, 200, SpringLayout.EAST, userInfo);
		layout.putConstraint(SpringLayout.NORTH, isMod, 0, SpringLayout.SOUTH, mainPage);

		layout.putConstraint(SpringLayout.WEST, deleteUser, 200, SpringLayout.EAST, userInfo);
		layout.putConstraint(SpringLayout.NORTH, deleteUser, 0, SpringLayout.SOUTH, mainPage);

		layout.putConstraint(SpringLayout.WEST, upgrade, 200, SpringLayout.EAST, userInfo);
		layout.putConstraint(SpringLayout.NORTH, upgrade, 0, SpringLayout.SOUTH, deleteUser);
		
		System.out.println("logger: "+loggerIsMod+" user: "+isModerator);
		if (isModerator) {
			panel.add(isMod);
		} else if (loggerIsMod && !loggedInUserId.equals(userId)) {
			panel.add(deleteUser);
			panel.add(upgrade);
		}
		
		mainPage.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {				
				mainMenu m = new mainMenu(con, loggedInUserId);
				m.drawMenu(frame);
			}
		});
		
		deleteUser.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {				
				try {
					Statement s = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
			                 ResultSet.CONCUR_UPDATABLE);
					String query = "SELECT userId FROM users WHERE userId = '" + userId + "'";
					ResultSet rs = s.executeQuery(query);
					
					if (rs.next()){
						rs.deleteRow();
						mainMenu m = new mainMenu(con, loggedInUserId);
						m.drawMenu(frame);
						JOptionPane.showMessageDialog(null, "Successfully Deleted User.");
					}
					
				} catch (SQLException e1){
					e1.printStackTrace();
				}
			
			}
		});
		
		upgrade.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {				
				try {
					Statement s = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
			                ResultSet.CONCUR_UPDATABLE);
					String query = "SELECT userId, isModerator FROM users WHERE userId = '" + userId + "'";
					ResultSet rs = s.executeQuery(query);
					
					if (rs.next()){
						rs.updateString("isModerator", "1");
						rs.updateRow();
					}
				} catch (SQLException e1){
					e1.printStackTrace();
	
				}
				panel.remove(upgrade);
				panel.remove(deleteUser);
				panel.add(isMod);
				
				frame.setContentPane(panel);
				frame.revalidate();
				frame.repaint();
			}
		});
		
		frame.setContentPane(panel);
		frame.revalidate();
		frame.repaint();
	}
	

}
